/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and openFromCache the template in the editor.
 */
package br.com.mvbos.mymer.sync;

import br.com.mvbos.mm.MMProperties;
import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.entity.DataBaseEntity;
import br.com.mvbos.mymer.entity.EntityManager;
import br.com.mvbos.mymer.entity.EntityUtil;
import br.com.mvbos.mymer.entity.IndexEntity;
import br.com.mvbos.mymer.tree.FieldTreeNode;
import br.com.mvbos.mymer.util.FileUtil;
import br.com.mvbos.mymer.xml.DataBaseStore;
import br.com.mvbos.mymer.xml.XMLUtil;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Index;
import br.com.mvbos.mymer.xml.field.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

/**
 *
 * @author MarcusS
 */
public class QuickImportBases extends javax.swing.JFrame {

    private class FieldChange {

        private Field field;
        private short position;
        private String tableName;
        private final FieldTreeNode.Diff diff;

        public FieldChange() {
            diff = FieldTreeNode.Diff.NONE;
        }

        public FieldChange(String tableName, Field field, FieldTreeNode.Diff diff) {
            this.field = field;
            this.tableName = tableName;
            this.diff = diff;
        }
    }

    private class IndexChange {

        private Index index;
        private Field removedField;
        private TableElement table;
        private final FieldTreeNode.Diff diff;

        public IndexChange() {
            diff = FieldTreeNode.Diff.NONE;
        }

        public IndexChange(TableElement table, Index index, FieldTreeNode.Diff diff) {
            this.index = index;
            this.table = table;
            this.diff = diff;
        }
    }

    private static Logger logger;

    private JFrame parent;

    private final EntityManager em = EntityManager.e();
    private final Map<String, Table> remoteTables = new HashMap<>(30);
    private final Map<String, TableElement> localTalbles = new HashMap<>(30);

    private final List<FieldChange> fieldChanges = new ArrayList<>(100);
    private final List<IndexChange> indexChanges = new ArrayList<>(100);

    private final List<Table> newRemoteTables = new ArrayList<>(30);
    private final List<TableElement> lstRemoveLocalTable = new ArrayList<>(30);

    /**
     * Creates new form QuickImportBases
     */
    public QuickImportBases() {
        initComponents();
    }

    public void hideOnStart(JFrame parent) {
        this.parent = parent;
    }

    public void startImport() {

        SwingWorker sw = new SwingWorker<Void, Void>() {

            final StringBuilder log = new StringBuilder(500);

            @Override
            protected Void doInBackground() throws Exception {

                if (parent != null) {
                    parent.setVisible(false);

                    /*RepaintManager r = RepaintManager.currentManager(parent); //.markCompletelyClean(parent.getRootPane());
                     for (Component c : parent.getComponents()) {
                     r.markCompletelyClean((JComponent) c);
                     }*/
                }

                try {
                    final String path = MMProperties.get("quick_import_file", "");

                    pBar.setValue(10);
                    tfResult.setText("Loading file: " + path);

                    final DataBaseStore db = validadeAndLoadFile(path);

                    if (db == null || EntityUtil.notNull(db.getBases()).isEmpty()) {
                        log.append("Nothing to import.");
                        return null;
                    }

                    pBar.setValue(40);
                    final short inc = (short) Math.round(pBar.getValue() / (float) db.getBases().size());
                    for (DataBase remoteBase : db.getBases()) {

                        clearLists();

                        populeLocalRemoteTables(remoteBase, log);

                        for (String tbName : remoteTables.keySet()) {

                            Table remoteTable = remoteTables.get(tbName);
                            TableElement localTable = localTalbles.get(tbName);

                            if (localTable == null) {
                                newRemoteTables.add(remoteTables.get(tbName));
                                log.append(String.format("New table %s ", tbName));
                                log.append(" added to ").append(remoteBase.getName()).append(".");
                                log.append(System.lineSeparator());

                            } else {
                                processFieldsChange(remoteBase, localTable, remoteTable, log);
                                processIndicesChange(remoteBase, localTable, remoteTable, log);
                            }

                        }

                        persist(remoteBase);
                        pBar.setValue(pBar.getValue() + inc);
                    }

                } catch (Exception ex) {
                    log.append("Error to process ").append(ex.getMessage());
                }

                return null;
            }

            @Override
            protected void done() {
                tfResult.setText(log.toString());
                pBar.setValue(100);

                if (parent != null) {
                    parent.setVisible(true);
                    QuickImportBases.this.requestFocus();
                }

                if (QuickImportBases.logger == null) {
                    QuickImportBases.logger = FileUtil.getLoggerToFile(QuickImportBases.this.getClass(), "quick_import");
                }

                if (QuickImportBases.logger != null) {
                    QuickImportBases.logger.setUseParentHandlers(false);
                    QuickImportBases.logger.info(log.toString());
                }
            }

        };

        sw.execute();

    }

    private boolean isNewField(String tableName, Field field, List<FieldChange> lst) {

        for (FieldChange f : lst) {
            //if (updateFields.containsKey(tbName) && updateFields.get(tbName).contains(locField)) 
            if (FieldTreeNode.Diff.NEW != f.diff || !f.tableName.equals(tableName)) {
                continue;
            }

            if (f.field.getName().equals(field.getName())) {
                return true;
            }
        }

        return false;
    }

    private void processFieldsChange(DataBase base, TableElement lt, Table rt, StringBuilder log) {

        //Compare local / remote
        for (Field locField : lt.getFields()) {
            int idx = rt.getFields().indexOf(locField);

            if (idx != -1) {
                Field remField = rt.getFields().get(idx);
                FieldTreeNode.Diff diff = compareFields(rt.getName(), locField, remField, log);

                if (diff != FieldTreeNode.Diff.NONE) {
                    fieldChanges.add(new FieldChange(rt.getName(), remField, diff));
                }

            } else {
                fieldChanges.add(new FieldChange(lt.getName(), locField, FieldTreeNode.Diff.DELETED));
                log.append("Field ").append(locField.getName());
                log.append(" removed from ").append(lt.getName());
                log.append(System.lineSeparator());
            }
        }

        //Compare remote / local
        for (Field remField : rt.getFields()) {
            if (!lt.getFields().contains(remField)) {
                final FieldChange fc = new FieldChange(rt.getName(), remField, FieldTreeNode.Diff.NEW);
                fc.position = (short) rt.getFields().indexOf(remField);
                fieldChanges.add(fc);

                log.append(String.format("New field %s ", remField.getName()));
                log.append(" added to ").append(rt.getName()).append(".");
                log.append(System.lineSeparator());
            }
        }
    }

    private void processIndicesChange(DataBase base, TableElement lt, Table rt, StringBuilder log) {

        final IndexEntity indEnt = em.getEntity(IndexEntity.class);

        for (Index remoteIndex : EntityUtil.notNull(rt.getIndices())) {

            if (!EntityUtil.hasValue(remoteIndex.getName()) || "default".equalsIgnoreCase(remoteIndex.getName().trim())) {
                continue;
            }

            final IndexElement locIndex = indEnt.findByName(remoteIndex.getName(), lt);

            if (remoteIndex.getFields() == null) {
                remoteIndex.setFields(Collections.EMPTY_LIST);
            }

            if (locIndex == null) {
                //IndexElement ie = new IndexElement(ridx, lte);
                indexChanges.add(new IndexChange(lt, remoteIndex, FieldTreeNode.Diff.NEW));
                log.append(String.format("New index %s ", remoteIndex.getName()));
                log.append(" to ").append(lt.getName()).append(".");
                log.append(System.lineSeparator());

                continue;
            }

            FieldTreeNode.Diff diff = compareIndex(rt.getName(), locIndex, remoteIndex, log);

            if (diff != FieldTreeNode.Diff.NONE) {
                indexChanges.add(new IndexChange(lt, remoteIndex, diff));
            }

            //Compare add or remove fields of index
            for (Field locField : locIndex.getFields()) {

                if (!EntityUtil.hasValue(locField.getName())) {
                    continue;
                }

                if (isNewField(lt.getName(), locField, fieldChanges)) {
                    indexChanges.add(new IndexChange(lt, remoteIndex, FieldTreeNode.Diff.FIELD));

                    log.append("New field ").append(locField.getName()).append(" addeded to update.");
                    log.append(System.lineSeparator());

                } else if (!remoteIndex.getFields().contains(locField)) {
                    final IndexChange ic = new IndexChange(lt, remoteIndex, FieldTreeNode.Diff.REMOVED_FROM_INDEX);
                    ic.removedField = locField;
                    indexChanges.add(ic);

                    log.append("Field ").append(locField.getName());
                    log.append(" removed from index ").append(remoteIndex.getName());
                    log.append(" to ").append(lt.getName()).append(".");
                    log.append(System.lineSeparator());

                } else {
                    //itn.setDiff(FieldTreeNode.Diff.NONE);
                }
            }

            for (Field remField : remoteIndex.getFields()) {

                if (!locIndex.getFields().contains(remField)) {
                    indexChanges.add(new IndexChange(lt, remoteIndex, FieldTreeNode.Diff.FIELD));

                    log.append("New field ").append(remField.getName()).append(" addeded to ");
                    log.append(remoteIndex.getName()).append(" to ");
                    log.append(lt.getName()).append(".");
                    log.append(System.lineSeparator());

                } else {
                    //itn.setDiff(FieldTreeNode.Diff.NONE);
                }
            }

        }
    }

    private void populeLocalRemoteTables(DataBase remoteBase, StringBuilder log) {
        DataBaseEntity dbEntity = em.getEntity(DataBaseEntity.class);
        DataBaseElement base = dbEntity.findByName(remoteBase.getName());

        if (base != null) {
            for (TableElement te : base.getTables()) {
                localTalbles.put(te.getName(), te);
            }
        }

        for (Table tb : remoteBase.getTables()) {
            remoteTables.put(tb.getName(), tb);
        }

        for (String name : localTalbles.keySet()) {
            if (!remoteTables.containsKey(name)) {
                lstRemoveLocalTable.add(localTalbles.get(name));
                log.append(String.format("Table %s ", name));
                log.append(" was removed from ").append(remoteBase.getName()).append(".");
                log.append(System.lineSeparator());
            }
        }
    }

    private void clearLists() {
        fieldChanges.clear();
        indexChanges.clear();
        localTalbles.clear();
        remoteTables.clear();
        newRemoteTables.clear();
        lstRemoveLocalTable.clear();
    }

    private void persist(DataBase remoteBase) {

        DataBaseEntity dbEntity = em.getEntity(DataBaseEntity.class);
        IndexEntity indexEntity = em.getEntity(IndexEntity.class);

        DataBaseElement db = dbEntity.findByName(remoteBase.getName());

        if (db == null) {
            db = new DataBaseElement(remoteBase);
            dbEntity.add(db);

        } else {
            for (TableElement t : lstRemoveLocalTable) {
                dbEntity.removeTable(t);
            }
        }

        for (Table tb : newRemoteTables) {
            TableElement tbe = new TableElement(db, tb);

            //Rename field controll
            for (Field f : tbe.getFields()) {
                //f.setOrgId(f.getName());
            }

            if (tb.getIndices() != null) {
                for (Index i : tb.getIndices()) {
                    indexEntity.add(new IndexElement(i, tbe));
                }
            }

            tbe.update();
            db.addTable(tbe);
            dbEntity.addTable(tbe);
        }

        for (FieldChange f : fieldChanges) {
            TableElement tb = dbEntity.findByTableName(db.getName(), f.tableName);

            int idx = tb.getFields().indexOf(f.field);

            if (idx == -1) {
                if (FieldTreeNode.Diff.NEW == f.diff) {
                    short px = f.position;
                    if (px < tb.getFields().size()) {
                        tb.getFields().add(px, f.field);
                    } else {
                        tb.getFields().add(f.field);
                    }
                }
            } else if (FieldTreeNode.Diff.DELETED == f.diff) {
                tb.getFields().remove(idx);

            } else if (FieldTreeNode.Diff.FIELD == f.diff) {
                tb.getFields().set(idx, f.field);
            }
        }

        for (IndexChange i : indexChanges) {
            IndexElement temp = indexEntity.findByName(i.index.getName(), i.table);
            int idx = indexEntity.getList().indexOf(temp);

            if (idx == -1) {
                if (FieldTreeNode.Diff.NEW == i.diff) {
                    indexEntity.add(new IndexElement(i.index, i.table));
                }

            } else if (FieldTreeNode.Diff.DELETED == i.diff) {
                indexEntity.remove(temp);

            } else if (FieldTreeNode.Diff.REMOVED_FROM_INDEX == i.diff) {
                temp.getFields().remove(i.removedField);

            } else if (FieldTreeNode.Diff.FIELD == i.diff) {
                indexEntity.replace(idx, new IndexElement(i.index, i.table));
            }
        }
    }

    private DataBaseStore validadeAndLoadFile(String path) {
        final DataBaseStore db;

        if (path.isEmpty()) {
            return null;
        }

        File f = new File(path);

        if (!f.exists() || f.isDirectory()) {
            return null;
        }

        try {
            final InputStreamReader stream = new InputStreamReader(new FileInputStream(f), Common.charset);

            db = XMLUtil.parseToDataBase(stream);

            if (db != null && db.hasBases()) {

                DataBaseEntity dbEntity = em.getEntity(DataBaseEntity.class);

                List<DataBase> temp = new ArrayList(db.getBases());
                for (DataBase d : temp) {
                    DataBaseElement base = dbEntity.findByName(d.getName());
                    if (base == null) {
                        db.getBases().remove(d);
                    }
                }

                //Create cache
                FileUtil.storeToCache(db);
                return db;
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuickImportBases.class.getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            Logger.getLogger(QuickImportBases.class.getName()).log(Level.WARNING, null, ex);
        }

        return null;
    }

    private FieldTreeNode.Diff compareFields(String tableName, Field fa, Field fb, StringBuilder log) {
        boolean change = false;

        if (!fa.equals(fb)) {
            return FieldTreeNode.Diff.NONE;
        }

        java.lang.reflect.Field[] fields = fa.getClass().getDeclaredFields();

        for (java.lang.reflect.Field fl : fields) {
            try {
                java.lang.reflect.Field fr = fb.getClass().getDeclaredField(fl.getName());
                fl.setAccessible(true);
                fr.setAccessible(true);

                if (fl.get(fa) == null || fr.get(fb) == null) {
                    continue;
                }

                if (!fl.get(fa).equals(fr.get(fb))) {
                    log.append(tableName).append(": ");
                    log.append(fa.getName()).append(" change ").append(fl.getName());
                    log.append(" from ").append(fl.get(fa)).append(" to ").append(fr.get(fb)).append(".");
                    log.append(System.lineSeparator());

                    change = true;
                }

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(ImportBases.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return change ? FieldTreeNode.Diff.FIELD : FieldTreeNode.Diff.NONE;
    }

    private FieldTreeNode.Diff compareIndex(String tableName, IndexElement li, Index ri, StringBuilder log) {
        boolean change = false;

        //TODO create annotation "@comparable" to compare fields dynamically
        if (!li.getActive().equals(ri.getActive())) {
            log.append(tableName).append(": ");
            log.append(li.getName()).append(" change active");
            log.append(" from ").append(li.getActive()).append(" to ").append(ri.getActive()).append(".");
            log.append(System.lineSeparator());

            change = true;
        }

        if (!li.getPrimary().equals(ri.getPrimary())) {
            log.append(tableName).append(": ");
            log.append(li.getName()).append(" change primary");
            log.append(" from ").append(li.getPrimary()).append(" to ").append(ri.getPrimary()).append(".");
            log.append(System.lineSeparator());

            change = true;
        }

        if (!li.getUnique().equals(ri.getUnique())) {
            log.append(tableName).append(": ");
            log.append(li.getName()).append(" change unique");
            log.append(" from ").append(li.getUnique()).append(" to ").append(ri.getUnique()).append(".");
            log.append(System.lineSeparator());

            change = true;
        }

        return change ? FieldTreeNode.Diff.FIELD : FieldTreeNode.Diff.NONE;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pBar = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tfResult = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quick Import");

        tfResult.setColumns(20);
        tfResult.setRows(5);
        jScrollPane1.setViewportView(tfResult);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(pBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JProgressBar pBar;
    private javax.swing.JTextArea tfResult;
    // End of variables declaration//GEN-END:variables
}
