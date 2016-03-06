/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.entity;

import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.DataBaseStore;
import br.com.mvbos.mymer.xml.XMLUtil;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.Table;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Marcus Becker
 */
public class DataBaseEntity implements IElementEntity<DataBaseElement> {

    public static int tableCount;

    public static final DataBaseElement DEFAULT_DATA_BASE = new DataBaseElement("New data base", new Color(74, 189, 218));

    private static final int LIST_TABLE_SIZE = 300;
    private static final File FILE_DIR_DB = new File(XMLUtil.CURRENT_PATH, "dbs");

    private static final List<ActionListener> listern = new ArrayList<>(4);

    private final List<TableElement> allTables = new ArrayList<>(LIST_TABLE_SIZE);
    private final List<DataBaseElement> filterBases = new ArrayList<>(10);

    private static File[] getDBFiles() {

        if (!FILE_DIR_DB.exists()) {
            FILE_DIR_DB.mkdir();
        }

        File[] arr = FILE_DIR_DB.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml");
            }
        });

        return arr;
    }

    @Override
    public boolean add(DataBaseElement e) {
        if (filterBases.contains(e)) {
            return false;
        }

        filterBases.add(e);

        ActionEvent evt = new ActionEvent(e, IElementEntity.EVT_ADD, "ADD_DATABASE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }

        return true;
    }

    @Override
    public boolean remove(DataBaseElement e) {
        if (!filterBases.contains(e)) {
            return false;
        }

        filterBases.remove(e);

        ActionEvent evt = new ActionEvent(e, IElementEntity.EVT_REMOVE, "REMOVE_DATABASE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }

        return true;
    }

    public boolean addTable(TableElement e) {
        if (allTables.contains(e)) {
            return false;
        }

        add(e.getDataBase());
        e.getDataBase().addTable(e);

        allTables.add(e);

        ActionEvent evt = new ActionEvent(e, IElementEntity.EVT_ADD, "ADD_TABLE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }

        return true;
    }

    public boolean removeTable(TableElement e) {
        if (!allTables.contains(e)) {
            return false;
        }

        allTables.remove(e);
        e.getDataBase().getTables().remove(e);

        ActionEvent evt = new ActionEvent(e, IElementEntity.EVT_REMOVE, "REMOVE_TABLE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }

        return true;
    }

    @Override
    public boolean save(IElementEntity p) {
        for (DataBaseElement e : filterBases) {

            DataBase db = new DataBase(e.getName());
            DataBaseStore dbs = new DataBaseStore();

            for (TableElement te : e.getTables()) {
                Table tb = new Table(te.getName());
                tb.setFields(te.getFields());

                db.addTable(tb);
            }

            dbs.addBase(db);

            File dst = new File(FILE_DIR_DB, db.getName().concat(".xml"));

            try {
                JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, XMLUtil.FORMATTED_OUTPUT);
                //m.marshal(fps, System.out);

                m.marshal(dbs, XMLUtil.getFileOutputStream(dst));

            } catch (JAXBException | FileNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

                return false;
            }
        }

        return true;
    }

    @Override
    public boolean load(IElementEntity parent) {
        DataBaseStore dbs = null;

        for (File f : getDBFiles()) {
            Logger.getLogger(DataBaseEntity.class.getName()).log(Level.INFO, "load {0}", f.getName());

            try {
                JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
                Unmarshaller um = context.createUnmarshaller();

                dbs = (DataBaseStore) um.unmarshal(XMLUtil.getFileInputStream(f));

            } catch (JAXBException | FileNotFoundException ex) {
                Logger.getLogger(DataBaseEntity.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (dbs != null) {

                for (DataBase db : dbs.getBases()) {
                    if (DEFAULT_DATA_BASE.getName().equals(db.getName()) && db.getTables().isEmpty()) {
                        continue;
                    }

                    DataBaseElement dbEl = new DataBaseElement(db);
                    List<TableElement> elTables = new ArrayList<>(db.getTables().size());

                    dbEl.setTables(elTables);

                    filterBases.add(dbEl);

                    for (Table t : db.getTables()) {
                        TableElement e = new TableElement(0, 0, dbEl, t.getName());

                        if (t.getFields() != null) {
                            e.setFields(t.getFields());
                        }

                        elTables.add(e);
                        allTables.add(e);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public List<DataBaseElement> getList() {
        return filterBases;
    }

    @Override
    public DataBaseElement findByName(String name) {
        for (DataBaseElement d : filterBases) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }

        return null;
    }

    @Override
    public List<DataBaseElement> findBy(IEntityFilter filter) {
        List<DataBaseElement> temp = new ArrayList<>(filterBases.size());

        for (DataBaseElement d : filterBases) {
            if (filter.accept(d)) {
                temp.add(d);
            }

        }

        return temp;
    }

    @Override
    public void addActionListern(ActionListener actionListener) {
        listern.add(actionListener);
    }

    /**
     * Use removeTable(TableElement e)
     *
     * @param db
     * @param e
     * @return
     * @deprecated
     */
    @Deprecated
    public boolean removeTable(DataBaseElement db, TableElement e) {
        db.getTables().remove(e);
        allTables.remove(e);

        ActionEvent evt = new ActionEvent(e, IElementEntity.EVT_REMOVE, "REMOVE_TABLE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }

        return true;
    }

    public List<TableElement> getTableList() {
        return allTables;
    }

    public TableElement findByTableName(String name) {
        for (TableElement t : allTables) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }

        return null;
    }

    public List<TableElement> findTableBy(IEntityFilter filter) {
        List<TableElement> temp = new ArrayList<>(allTables.size());

        for (TableElement t : allTables) {
            if (filter.accept(t)) {
                temp.add(t);
            }

        }

        return temp;
    }

    public TableElement findByTableName(String dbName, String name) {
        DataBaseElement dbe = findByName(dbName);

        if (dbe == null) {
            return null;
        }

        for (TableElement e : dbe.getTables()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }

        return null;
    }
}
