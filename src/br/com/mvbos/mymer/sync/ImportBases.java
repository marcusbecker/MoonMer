/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.sync;

import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.combo.Option;
import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.tree.FieldTreeNode;
import br.com.mvbos.mymer.tree.IndexTreeNode;
import br.com.mvbos.mymer.tree.TableTreeNode;
import br.com.mvbos.mymer.xml.DataBaseStore;
import br.com.mvbos.mymer.xml.XMLUtil;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Index;
import br.com.mvbos.mymer.xml.field.Table;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;

/**
 *
 * @author marcuss
 */
public class ImportBases extends javax.swing.JFrame {

    private DataBase remoteBase;

    private Map<String, Set<FieldChange>> updateFields;
    private Map<String, Set<IndexChange>> updateIndices;

    private final StringBuilder sb = new StringBuilder();
    private final Map<FieldTreeNode, String> logFields = new HashMap<>(20);
    private final Map<IndexTreeNode, String> logIndices = new HashMap<>(20);

    private final List<Table> newRemoteTables = new ArrayList<>(30);
    private final Map<String, Table> remoteTables = new HashMap<>(30);
    private final Map<String, TableElement> localTalbles = new HashMap<>(30);
    private final List<TableElement> lstRemoveLocalTable = new ArrayList<>(30);

    private final boolean filterBySelected = true;

    private int dbStart;
    private int dbCount;
    private DataBaseStore dbStore;

    class FieldChange {

        Field field;
        FieldTreeNode.Diff diff;

        public FieldChange(Field field, FieldTreeNode.Diff diff) {
            this.field = field;
            this.diff = diff;
        }

        @Override
        public int hashCode() {
            int hash = field.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Field && field.equals(obj);
        }

        @Override
        public String toString() {
            return "FieldChange{" + "field=" + field + ", diff=" + diff + '}';
        }

    }

    class IndexChange {

        IndexElement index;
        FieldTreeNode.Diff diff;

        public IndexChange(IndexElement index, FieldTreeNode.Diff diff) {
            this.index = index;
            this.diff = diff;
        }

        @Override
        public int hashCode() {
            int hash = index.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Index && index.equals(obj);
        }

        @Override
        public String toString() {
            return "IndexChange{" + "index=" + index + ", diff=" + diff + '}';
        }

    }

    /**
     * Creates new form ImportBases
     */
    public ImportBases() {
        initComponents();
        changeTab(0);
        fc.setFileFilter(new FileNameExtensionFilter("XML File", "xml"));
        tfOrigin.setText(Common.importURL);
    }

    private void startImport(DataBaseStore db) {
        this.dbStore = db;

        if (db != null && db.hasBases()) {
            dbStart = 0;
            dbCount = db.getBases().size();

            nextDataBase();

            lblInfo.setText("Import successful.");
            lblInfo.setForeground(Color.BLACK);

            btnNext.setEnabled(true);

        } else {
            btnNext.setEnabled(false);
            lblDBInfo.setText("No data base selected.");
        }
    }

    private void nextDataBase() {
        populeListOrg(dbStore.getBases().get(dbStart));
        dbStart++;

        changeTab(0);
        updateNextButton();
    }

    private short optIndex;

    private void populeListOrg(DataBase db) {

        remoteBase = db;
        optIndex = 0;

        localTalbles.clear();
        remoteTables.clear();
        newRemoteTables.clear();
        lstRemoveLocalTable.clear();

        DefaultListModel<Option> org = (DefaultListModel<Option>) lstOrg.getModel();
        org.removeAllElements();

        DefaultListModel<Option> dst = (DefaultListModel<Option>) lstDst.getModel();
        dst.removeAllElements();

        DataBaseElement dbe = XMLUtil.findByName(remoteBase.getName());
        if (dbe != null) {
            for (TableElement te : dbe.getTables()) {
                localTalbles.put(te.getName(), te);
            }
        }

        List<Option> lstOrdered = new ArrayList<>(remoteBase.getTables().size());

        for (Table tb : remoteBase.getTables()) {
            remoteTables.put(tb.getName(), tb);

            String name = tb.getName();
            short tempIndex = optIndex++;

            if (!localTalbles.containsKey(name)) {
                name += " (new)";
                tempIndex = (short) -tempIndex;
            }

            lstOrdered.add(new Option(tempIndex, tb, name));
        }

        Collections.sort(lstOrdered, new Comparator<Option>() {

            @Override
            public int compare(Option o1, Option o2) {
                return Short.compare(o1.getIndex(), o2.getIndex());
            }
        });

        for (Option p : lstOrdered) {
            org.addElement(p);
        }

        lblDBInfo.setText("Database: " + remoteBase.getName());
    }

    private FieldTreeNode.Diff compareFields(Field fa, Field fb, StringBuilder log) {
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
                    log.append("<b>");
                    log.append(fa.getName()).append("</b> change <b>").append(fl.getName());
                    log.append("</b> from <i>").append(fl.get(fa)).append("</i> to <i>").append(fr.get(fb));
                    log.append("</i><br />");

                    change = true;
                }

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(ImportBases.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        fc = new javax.swing.JFileChooser();
        tab = new javax.swing.JTabbedPane();
        pnStepOne = new javax.swing.JPanel();
        tfOrigin = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();
        btnURL = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstOrg = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstDst = new javax.swing.JList();
        btnAddAll = new javax.swing.JButton();
        btnRem = new javax.swing.JButton();
        btnRemAll = new javax.swing.JButton();
        pnStepTwo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstTablesConflict = new javax.swing.JList();
        btnRemoveLocalTable = new javax.swing.JButton();
        pnStepThree = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        treeFieldImport = new javax.swing.JTree();
        btnUpdateAll = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tfLog = new javax.swing.JEditorPane();
        pnStepFour = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        treeIndexImport = new javax.swing.JTree();
        btnUpdateAllIndices = new javax.swing.JButton();
        btnUpdateIndex = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        tfLogIndex = new javax.swing.JEditorPane();
        btnNext = new javax.swing.JButton();
        lblDBInfo = new javax.swing.JLabel();
        lblInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Import Bases");

        btnFile.setText("File");
        btnFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFileActionPerformed(evt);
            }
        });

        btnURL.setText("URL");
        btnURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnURLActionPerformed(evt);
            }
        });

        lstOrg.setModel(new DefaultListModel<Option>());
        jScrollPane1.setViewportView(lstOrg);

        btnAdd.setText(">");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        lstDst.setModel(new DefaultListModel<Option>());
        jScrollPane2.setViewportView(lstDst);

        btnAddAll.setText(">>");
        btnAddAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAllActionPerformed(evt);
            }
        });

        btnRem.setText("<");
        btnRem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemActionPerformed(evt);
            }
        });

        btnRemAll.setText("<<");
        btnRemAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnStepOneLayout = new javax.swing.GroupLayout(pnStepOne);
        pnStepOne.setLayout(pnStepOneLayout);
        pnStepOneLayout.setHorizontalGroup(
            pnStepOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStepOneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnStepOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnStepOneLayout.createSequentialGroup()
                        .addComponent(tfOrigin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnURL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFile))
                    .addGroup(pnStepOneLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnStepOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAdd)
                            .addComponent(btnAddAll)
                            .addComponent(btnRem)
                            .addComponent(btnRemAll))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pnStepOneLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAdd, btnRem, btnRemAll});

        pnStepOneLayout.setVerticalGroup(
            pnStepOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStepOneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnStepOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfOrigin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFile)
                    .addComponent(btnURL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnStepOneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addGroup(pnStepOneLayout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemAll)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        tab.addTab("Remote Tables", pnStepOne);

        jLabel1.setText("New local tables");

        lstTablesConflict.setModel(new DefaultListModel());
        jScrollPane3.setViewportView(lstTablesConflict);

        btnRemoveLocalTable.setText("Remove");
        btnRemoveLocalTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveLocalTableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnStepTwoLayout = new javax.swing.GroupLayout(pnStepTwo);
        pnStepTwo.setLayout(pnStepTwoLayout);
        pnStepTwoLayout.setHorizontalGroup(
            pnStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStepTwoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnStepTwoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnRemoveLocalTable)))
                .addContainerGap())
        );
        pnStepTwoLayout.setVerticalGroup(
            pnStepTwoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStepTwoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoveLocalTable)
                .addContainerGap())
        );

        tab.addTab("Local Tables", pnStepTwo);

        treeFieldImport.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeFieldImportValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(treeFieldImport);

        btnUpdateAll.setText("Update al fields");
        btnUpdateAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateAllActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update field");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        tfLog.setContentType("text/html"); // NOI18N
        jScrollPane7.setViewportView(tfLog);

        javax.swing.GroupLayout pnStepThreeLayout = new javax.swing.GroupLayout(pnStepThree);
        pnStepThree.setLayout(pnStepThreeLayout);
        pnStepThreeLayout.setHorizontalGroup(
            pnStepThreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnStepThreeLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdateAll)
                .addContainerGap())
            .addComponent(jScrollPane7)
        );
        pnStepThreeLayout.setVerticalGroup(
            pnStepThreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStepThreeLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnStepThreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdateAll)
                    .addComponent(btnUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        );

        tab.addTab("Fields Conflict", pnStepThree);

        treeIndexImport.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeIndexImportValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(treeIndexImport);

        btnUpdateAllIndices.setText("Update all indices");
        btnUpdateAllIndices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateAllIndicesActionPerformed(evt);
            }
        });

        btnUpdateIndex.setText("Update index");
        btnUpdateIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateIndexActionPerformed(evt);
            }
        });

        tfLogIndex.setContentType("text/html"); // NOI18N
        jScrollPane8.setViewportView(tfLogIndex);

        javax.swing.GroupLayout pnStepFourLayout = new javax.swing.GroupLayout(pnStepFour);
        pnStepFour.setLayout(pnStepFourLayout);
        pnStepFourLayout.setHorizontalGroup(
            pnStepFourLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnStepFourLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdateIndex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdateAllIndices)
                .addContainerGap())
            .addComponent(jScrollPane8)
        );
        pnStepFourLayout.setVerticalGroup(
            pnStepFourLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnStepFourLayout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnStepFourLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdateAllIndices)
                    .addComponent(btnUpdateIndex))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        );

        tab.addTab("Index Conflict", pnStepFour);

        btnNext.setText("Next");
        btnNext.setEnabled(false);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        lblDBInfo.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblDBInfo.setText("No data base selected.");

        lblInfo.setText("Use URL or select a File");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tab)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblDBInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNext)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDBInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNext)
                    .addComponent(lblInfo))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnURLActionPerformed

        if (tfOrigin.getText().trim().isEmpty()) {
            lblInfo.setText("No URL defined.");
            return;
        }

        tfOrigin.setEnabled(false);
        btnURL.setEnabled(false);

        try {
            URL url = new URL(tfOrigin.getText());

            DataBaseStore db;
            try (InputStreamReader stream = new InputStreamReader(url.openStream(), Common.importCharset)) {
                db = XMLUtil.parseToDataBase(stream);
                startImport(db);
            }

        } catch (MalformedURLException ex) {
            lblInfo.setText(ex.getMessage());
            lblInfo.setForeground(Color.RED);

            Logger.getLogger(ImportBases.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            lblInfo.setText(ex.getMessage());
            lblInfo.setForeground(Color.RED);

            Logger.getLogger(ImportBases.class.getName()).log(Level.SEVERE, null, ex);

        } catch (Exception ex) {
            lblInfo.setText(ex.getMessage());
            lblInfo.setForeground(Color.RED);

            Logger.getLogger(ImportBases.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            tfOrigin.setEnabled(true);
            btnURL.setEnabled(true);
        }

    }//GEN-LAST:event_btnURLActionPerformed

    private void btnFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFileActionPerformed

        fc.setCurrentDirectory(new File(tfOrigin.getText()));
        fc.showOpenDialog(this);

        File f = fc.getSelectedFile();

        if (f != null) {
            try {
                DataBaseStore db;
                try (InputStreamReader stream = new InputStreamReader(new FileInputStream(f), Common.charset)) {
                    db = XMLUtil.parseToDataBase(stream);
                    tfOrigin.setText(f.getAbsolutePath());
                    startImport(db);
                }

            } catch (Exception ex) {
                lblInfo.setText(ex.getMessage());
                lblInfo.setForeground(Color.RED);
                Logger.getLogger(ImportBases.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }//GEN-LAST:event_btnFileActionPerformed

    private void btnAddAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAllActionPerformed

        DefaultListModel org = (DefaultListModel) lstOrg.getModel();
        DefaultListModel dst = (DefaultListModel) lstDst.getModel();

        for (Object o : org.toArray()) {
            dst.addElement(o);
        }

        org.removeAllElements();

    }//GEN-LAST:event_btnAddAllActionPerformed

    private void btnRemAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemAllActionPerformed

        DefaultListModel org = (DefaultListModel) lstDst.getModel();
        DefaultListModel dst = (DefaultListModel) lstOrg.getModel();

        for (Object o : org.toArray()) {
            dst.addElement(o);
        }

        org.removeAllElements();

    }//GEN-LAST:event_btnRemAllActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed

        DefaultListModel org = (DefaultListModel) lstOrg.getModel();
        DefaultListModel dst = (DefaultListModel) lstDst.getModel();

        for (Object o : lstOrg.getSelectedValuesList()) {
            dst.addElement(o);
            org.removeElement(o);
        }


    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemActionPerformed
        DefaultListModel org = (DefaultListModel) lstOrg.getModel();
        DefaultListModel dst = (DefaultListModel) lstDst.getModel();

        for (Object o : lstDst.getSelectedValuesList()) {
            org.addElement(o);
            dst.removeElement(o);
        }

    }//GEN-LAST:event_btnRemActionPerformed

    private DefaultMutableTreeNode tableRoot;
    private DefaultMutableTreeNode indexRoot;


    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed

        updateNextButton();

        if (tab.getSelectedIndex() == 0) {
            runStepOne();

        } else if (tab.getSelectedIndex() == 1) {
            runStepTwo();

        } else if (tab.getSelectedIndex() == 2) {
            runStepThree();

        } else if (dbStart < dbCount) {
            persist();
            nextDataBase();

        } else {
            persist();
            finish();
        }

    }//GEN-LAST:event_btnNextActionPerformed

    private void updateNextButton() {
        if (tab.getSelectedIndex() == tab.getTabCount() - 2) {
            btnNext.setText(String.format("Finish %d of %d", dbStart, dbCount));
        } else {
            btnNext.setText(String.format("Next %d of %d", dbStart, dbCount));
        }
    }


    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed

        TreePath[] selection = treeFieldImport.getSelectionPaths();

        if (selection == null) {
            return;
        }

        for (TreePath p : selection) {
            DefaultMutableTreeNode path = (DefaultMutableTreeNode) p.getLastPathComponent();

            if (path.isRoot()) {
                continue;
            }

            if (path instanceof TableTreeNode) {
                TableTreeNode t = (TableTreeNode) path;

                Enumeration children = path.children();
                while (children.hasMoreElements()) {
                    FieldTreeNode f = (FieldTreeNode) children.nextElement();
                    addToUpdate(t.get(), f);
                }

                tableRoot.remove(t);
                treeFieldImport.updateUI();

            } else {
                TableTreeNode t = (TableTreeNode) p.getParentPath().getLastPathComponent();
                FieldTreeNode f = (FieldTreeNode) path;
                addToUpdate(t.get(), f);

                t.remove(f);

                if (t.isLeaf() && t.isNodeChild(tableRoot)) {
                    tableRoot.remove(t);
                }

                treeFieldImport.updateUI();
            }
        }

    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnUpdateIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateIndexActionPerformed

        TreePath[] selection = treeIndexImport.getSelectionPaths();

        if (selection == null) {
            return;
        }

        for (TreePath p : selection) {
            DefaultMutableTreeNode path = (DefaultMutableTreeNode) p.getLastPathComponent();

            if (path.isRoot()) {
                continue;
            }

            if (path instanceof IndexTreeNode) {
                IndexTreeNode ind = (IndexTreeNode) path;
                TableTreeNode ttn = (TableTreeNode) ind.getParent();

                addToUpdate(ttn.get(), ind);

                ttn.remove(ind);

                if (ttn.isLeaf() && ttn.isNodeChild(indexRoot)) {
                    indexRoot.remove(ttn);
                }

            } else {
                TableTreeNode ttn = (TableTreeNode) path;
                Enumeration children = path.children();

                while (children.hasMoreElements()) {
                    IndexTreeNode i = (IndexTreeNode) children.nextElement();

                    addToUpdate(ttn.get(), i);
                }

                indexRoot.remove(ttn);
            }

            treeIndexImport.updateUI();
        }

    }//GEN-LAST:event_btnUpdateIndexActionPerformed

    private void treeIndexImportValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeIndexImportValueChanged

        JTree tree = (JTree) evt.getSource();
        DefaultMutableTreeNode path = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (path instanceof IndexTreeNode) {
            tfLogIndex.setText(logIndices.get((IndexTreeNode) path));
        }

    }//GEN-LAST:event_treeIndexImportValueChanged

    private void treeFieldImportValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeFieldImportValueChanged

        JTree tree = (JTree) evt.getSource();
        DefaultMutableTreeNode path = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (path instanceof FieldTreeNode) {
            tfLog.setText(logFields.get((FieldTreeNode) path));
        }

    }//GEN-LAST:event_treeFieldImportValueChanged

    private void btnUpdateAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateAllActionPerformed

        Enumeration rootChildren = tableRoot.children();

        while (rootChildren.hasMoreElements()) {
            TableTreeNode t = (TableTreeNode) rootChildren.nextElement();

            Enumeration children = t.children();

            while (children.hasMoreElements()) {
                FieldTreeNode f = (FieldTreeNode) children.nextElement();

                addToUpdate(t.get(), f);
            }
        }

        tableRoot.removeAllChildren();
        treeFieldImport.updateUI();


    }//GEN-LAST:event_btnUpdateAllActionPerformed

    private void btnUpdateAllIndicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateAllIndicesActionPerformed

        Enumeration rootChildren = indexRoot.children();

        while (rootChildren.hasMoreElements()) {
            TableTreeNode ttn = (TableTreeNode) rootChildren.nextElement();
            Enumeration children = ttn.children();

            while (children.hasMoreElements()) {
                IndexTreeNode i = (IndexTreeNode) children.nextElement();

                addToUpdate(ttn.get(), i);
            }

        }

        indexRoot.removeAllChildren();
        treeIndexImport.updateUI();

    }//GEN-LAST:event_btnUpdateAllIndicesActionPerformed

    private void btnRemoveLocalTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLocalTableActionPerformed

        if (lstTablesConflict.getSelectedValue() != null) {
            String name = lstTablesConflict.getSelectedValue().toString();
            lstRemoveLocalTable.add(localTalbles.get(name));

            ((DefaultListModel) lstTablesConflict.getModel()).removeElement(lstTablesConflict.getSelectedValue());
        }

    }//GEN-LAST:event_btnRemoveLocalTableActionPerformed

    private void changeTab(int index) {
        tab.setSelectedIndex(index);

        for (int i = 0; i < tab.getTabCount(); i++) {
            tab.setEnabledAt(i, i == index);
        }

    }

    /**
     * Check new local tables
     */
    private void runStepOne() {
        DefaultListModel model = (DefaultListModel) lstTablesConflict.getModel();
        model.removeAllElements();
        for (String name : localTalbles.keySet()) {
            if (!remoteTables.containsKey(name)) {
                model.addElement(name);
            }
        }

        changeTab(1);
    }

    /**
     * Check fields conflicts and new remote tables
     */
    private void runStepTwo() {

        if (tableRoot == null) {
            tableRoot = new DefaultMutableTreeNode("Tables");
        } else {
            tableRoot.removeAllChildren();
            tfLog.setText(null);
        }

        DefaultListModel<Option> dst = (DefaultListModel<Option>) lstDst.getModel();

        for (Object tbName : dst.toArray()) {
            TableElement lt = localTalbles.get(tbName.toString());

            if (lt == null) {
                Option opt = (Option) tbName;
                newRemoteTables.add((Table) opt.getValue());
                continue;
            }

            Table rt = remoteTables.get(tbName.toString());

            TableTreeNode ttn = new TableTreeNode(lt);

            //Compare local / remote
            for (Field locField : lt.getFields()) {
                sb.delete(0, sb.length());

                int idx = rt.getFields().indexOf(locField);

                if (idx > -1) {
                    Field remField = rt.getFields().get(idx);
                    FieldTreeNode.Diff diff = compareFields(locField, remField, sb);

                    if (diff != FieldTreeNode.Diff.NONE) {
                        FieldTreeNode ftn = new FieldTreeNode(remField);
                        ftn.setDiff(diff);
                        ttn.add(ftn);

                        logFields.put(ftn, sb.toString());
                    }

                } else {
                    FieldTreeNode ftn = new FieldTreeNode(locField);
                    ftn.setDiff(FieldTreeNode.Diff.DELETED);
                    ttn.add(ftn);

                    sb.append("Field <b>").append(locField.getName()).append("</b> removed.");
                    logFields.put(ftn, sb.toString());
                }
            }

            //Compare remote / local
            for (Field remField : rt.getFields()) {

                if (!lt.getFields().contains(remField)) {
                    sb.delete(0, sb.length());

                    FieldTreeNode ftn = new FieldTreeNode(remField);
                    ftn.setDiff(FieldTreeNode.Diff.NEW);
                    ttn.add(ftn);

                    sb.append("Field <b>").append(remField.getName()).append("</b> added.");
                    logFields.put(ftn, sb.toString());
                }
            }

            if (!ttn.isLeaf()) {
                tableRoot.add(ttn);
            }
        }

        treeFieldImport.setModel(new DefaultTreeModel(tableRoot));
        updateFields = new LinkedHashMap<>(tableRoot.getChildCount());

        changeTab(2);
    }

    private void runStepThree() {

        if (indexRoot == null) {
            indexRoot = new DefaultMutableTreeNode("Indices");
        } else {
            indexRoot.removeAllChildren();
            logIndices.clear();
            tfLogIndex.setText(null);
        }

        for (String tbName : remoteTables.keySet()) {
            TableElement lte = localTalbles.get(tbName);

            if (lte == null) {
                continue;
            }

            DefaultListModel model = (DefaultListModel) lstDst.getModel();

            if (filterBySelected && !model.contains(tbName)) {
                continue;
            }

            Table rt = remoteTables.get(tbName);

            if (rt.getIndices() == null || rt.getIndices().isEmpty()) {
                continue;
            }

            DefaultMutableTreeNode ttn = new TableTreeNode(lte);

            for (Index ridx : rt.getIndices()) {

                if (ridx.getName().trim().isEmpty() || "default".equalsIgnoreCase(ridx.getName().trim())) {
                    continue;
                }

                sb.delete(0, sb.length());

                IndexElement locIndex = XMLUtil.findIndexByName(ridx.getName(), lte);
                IndexTreeNode itn = null;

                if (locIndex == null) {
                    if (ridx.getFields() == null || ridx.getFields().isEmpty()) {
                        continue;
                    }

                    itn = new IndexTreeNode(createIndexElement(ridx, lte));
                    itn.setDiff(FieldTreeNode.Diff.NEW);

                    sb.append("New index.");

                } else {
                    //Compare add or remove fields of index
                    for (Field locField : locIndex.getFields()) {

                        if (locField.getName().trim().isEmpty()) {
                            continue;
                        }

                        if (updateFields.containsKey(tbName) && updateFields.get(tbName).contains(locField)) {
                            itn = new IndexTreeNode(createIndexElement(ridx, lte));
                            itn.setDiff(FieldTreeNode.Diff.NEW);

                            sb.append("New field <b>").append(locField.getName()).append("</b> addeded to update.");
                            sb.append("<br />");

                        } else if (!ridx.getFields().contains(locField)) {
                            itn = new IndexTreeNode(createIndexElement(ridx, lte));
                            itn.setDiff(FieldTreeNode.Diff.DELETED);

                            sb.append("Field <b>").append(locField.getName()).append("</b> removed from index.");
                            sb.append("<br />");

                        } else {
                            //itn.setDiff(FieldTreeNode.Diff.NONE);
                        }
                    }

                    for (Field remField : ridx.getFields()) {

                        if (!locIndex.getFields().contains(remField)) {
                            itn = new IndexTreeNode(createIndexElement(ridx, lte));
                            itn.setDiff(FieldTreeNode.Diff.FIELD);

                            sb.append("New field <b>").append(remField.getName()).append("</b> addeded.");
                            sb.append("<br />");

                        } else {
                            //itn.setDiff(FieldTreeNode.Diff.NONE);
                        }
                    }
                }

                if (itn != null) {
                    ttn.add(itn);
                    logIndices.put(itn, sb.toString());
                }
            }

            if (!ttn.isLeaf()) {
                indexRoot.add(ttn);
            }
        }

        tfLog.setText(sb.toString());
        treeIndexImport.setModel(new DefaultTreeModel(indexRoot));
        updateIndices = new LinkedHashMap<>(indexRoot.getChildCount());

        changeTab(3);
    }

    private IndexElement createIndexElement(Index ridx, TableElement lte) {
        IndexElement ie = new IndexElement(ridx.getName(), lte);
        ie.setActive(ridx.getActive());
        ie.setPrimary(ridx.getPrimary());
        ie.setUnique(ridx.getUnique());
        ie.setFields(ridx.getFields());

        return ie;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddAll;
    private javax.swing.JButton btnFile;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnRem;
    private javax.swing.JButton btnRemAll;
    private javax.swing.JButton btnRemoveLocalTable;
    private javax.swing.JButton btnURL;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateAll;
    private javax.swing.JButton btnUpdateAllIndices;
    private javax.swing.JButton btnUpdateIndex;
    private javax.swing.JFileChooser fc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lblDBInfo;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JList lstDst;
    private javax.swing.JList lstOrg;
    private javax.swing.JList lstTablesConflict;
    private javax.swing.JPanel pnStepFour;
    private javax.swing.JPanel pnStepOne;
    private javax.swing.JPanel pnStepThree;
    private javax.swing.JPanel pnStepTwo;
    private javax.swing.JTabbedPane tab;
    private javax.swing.JEditorPane tfLog;
    private javax.swing.JEditorPane tfLogIndex;
    private javax.swing.JTextField tfOrigin;
    private javax.swing.JTree treeFieldImport;
    private javax.swing.JTree treeIndexImport;
    // End of variables declaration//GEN-END:variables

    private void addToUpdate(TableElement table, FieldTreeNode field) {
        if (!updateFields.containsKey(table.getName())) {
            updateFields.put(table.getName(), new HashSet<FieldChange>(20));
        }

        updateFields.get(table.getName()).add(new FieldChange(field.get(), field.getDiff()));
    }

    private void addToUpdate(TableElement table, IndexTreeNode index) {
        if (!updateIndices.containsKey(table.getName())) {
            updateIndices.put(table.getName(), new HashSet<IndexChange>(20));
        }

        updateIndices.get(table.getName()).add(new IndexChange(index.get(), index.getDiff()));
    }

    private void finish() {
        this.dispose();
    }

    private void persist() {

        lblInfo.setForeground(Color.BLACK);
        lblInfo.setText(String.format("Changes on %s were updated.", remoteBase.getName()));

        DataBaseElement db = XMLUtil.findByName(remoteBase.getName());

        if (db == null) {
            db = new DataBaseElement(remoteBase);
            XMLUtil.addDataBase(db);

        } else {
            for (TableElement t : lstRemoveLocalTable) {
                XMLUtil.removeTable(db, t);
            }
        }

        for (Table tb : newRemoteTables) {
            TableElement tbe = new TableElement(db, tb);

            if (tb.getIndices() != null) {
                for (Index i : tb.getIndices()) {
                    XMLUtil.addIndex(new IndexElement(i, tbe));
                }
            }

            tbe.update();
            db.addTable(tbe);
            XMLUtil.addFilterTable(tbe);
            //te.setPxy(Camera.c().getCpx(), Camera.c().getCpy());
        }

        for (String tbName : updateFields.keySet()) {
            Set<FieldChange> fchg = updateFields.get(tbName);

            TableElement tb = XMLUtil.findByName(db.getName(), tbName);

            for (FieldChange f : fchg) {

                int idx = tb.getFields().indexOf(f.field);

                if (idx == -1) {
                    if (FieldTreeNode.Diff.NEW == f.diff) {
                        tb.getFields().add(f.field);
                    }
                } else if (FieldTreeNode.Diff.DELETED == f.diff) {
                    tb.getFields().remove(idx);

                } else if (FieldTreeNode.Diff.FIELD == f.diff) {
                    tb.getFields().set(idx, f.field);
                }
            }
        }

        for (String tbName : updateIndices.keySet()) {
            Set<IndexChange> ichg = updateIndices.get(tbName);

            for (IndexChange k : ichg) {

                int idx = XMLUtil.indices.indexOf(k.index);

                if (idx == -1) {
                    if (FieldTreeNode.Diff.NEW == k.diff) {
                        XMLUtil.addIndex(k.index);
                    }
                } else if (FieldTreeNode.Diff.DELETED == k.diff) {
                    XMLUtil.indices.remove(idx);

                } else if (FieldTreeNode.Diff.FIELD == k.diff) {
                    XMLUtil.indices.set(idx, k.index);
                }
            }
        }

    }

}
