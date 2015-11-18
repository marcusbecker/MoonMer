/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.DataConfig;
import br.com.mvbos.mymer.xml.field.FieldPosition;
import br.com.mvbos.mymer.xml.field.Table;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author MarcusS
 */
public class XMLUtil {

    public static int tableCount;

    public static final DataBaseElement DEFAULT_DATA_BASE;

    public static final List<TableElement> filter;
    public static final List<DataBaseElement> filterBases = new ArrayList<>(10);

    public static Set<DataBaseElement> dataBases = new LinkedHashSet<>(10);

    private static final int LIST_TABLE_SIZE = 60;
    private static final Boolean FORMATTED_OUTPUT = Boolean.TRUE;
    private static final File FILE_STORE = new File("dbs");

    private static final File DIR_CONFIG = new File("config");
    private static final File FILE_CONFIG = new File(DIR_CONFIG, "config.xml");
    private static final File FILE_POSITION_STORE = new File(DIR_CONFIG, "field_config.xml");

    static {
        filter = importFields();
        importFieldsPosition(filter);
        importConfig();

        tableCount = filter.size();

        DEFAULT_DATA_BASE = new DataBaseElement("New data base", new Color(74, 189, 218));
    }

    public static boolean exportFields() {
        return exportFields(filter);
    }

    public static boolean exportFields(List<TableElement> lst) {

        Map<String, List<Table>> map = new HashMap<>(10);

        if (!DEFAULT_DATA_BASE.getTables().isEmpty()) {
            map.put(DEFAULT_DATA_BASE.getName(), new ArrayList<Table>(DEFAULT_DATA_BASE.getTables().size()));
        }

        for (DataBaseElement db : dataBases) {
            map.put(db.getName(), new ArrayList<Table>(db.getTables().size()));
        }

        for (TableElement e : lst) {
            Table tb = new Table(e.getName());
            tb.setFields(e.getFields());

            map.get(e.getDataBase().getName()).add(tb);
        }

        for (String k : map.keySet()) {

            DataBase db = new DataBase(k);

            for (Table tb : map.get(k)) {
                db.addTable(tb);
            }

            DataBaseStore dbs = new DataBaseStore();
            dbs.addBase(db);

            File dst = new File(FILE_STORE, k.concat(".xml"));

            try {
                JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);
                //m.marshal(fps, System.out);

                m.marshal(dbs, dst);

            } catch (Exception ex) {
                Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

                return false;
            }
        }

        return true;
    }

    public static boolean exportFieldsPosition() {
        return exportFieldsPosition(filter);
    }

    public static boolean exportFieldsPosition(List<TableElement> lst) {

        FieldPositionStore fps = new FieldPositionStore();
        List<FieldPosition> fieldsPxy = new ArrayList<>(lst.size());

        for (TableElement e : lst) {
            FieldPosition fieldPosition = new FieldPosition(e.getPx(), e.getPy(), e.getDataBase().getName() + "." + e.getName());
            fieldPosition.setColorName(e.getColor().getRGB());

            fieldsPxy.add(fieldPosition);
        }

        fps.setFields(fieldsPxy);

        try {
            if (!DIR_CONFIG.exists()) {
                DIR_CONFIG.mkdir();
            }

            JAXBContext context = JAXBContext.newInstance(FieldPositionStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);
            //m.marshal(fps, System.out);

            m.marshal(fps, FILE_POSITION_STORE);

        } catch (Exception ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    public static boolean exportConfig() {

        ConfigStore cs = new ConfigStore();
        List<DataConfig> bases = new ArrayList<>(dataBases.size());

        for (DataBaseElement e : dataBases) {

            DataConfig dc = new DataConfig(e.getName());
            if (e.getColor() != null) {
                dc.setColor(e.getColor().getRGB());
            }

            dc.setTableCount(e.getTables().size());

            bases.add(dc);
        }

        cs.setBases(bases);

        try {
            if (!DIR_CONFIG.exists()) {
                DIR_CONFIG.mkdir();
            }

            JAXBContext context = JAXBContext.newInstance(ConfigStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);

            m.marshal(cs, FILE_CONFIG);

        } catch (Exception ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    public static void importConfig() {

        ConfigStore config = null;

        if (!FILE_CONFIG.exists()) {
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(ConfigStore.class);
            Unmarshaller um = context.createUnmarshaller();

            config = (ConfigStore) um.unmarshal(new FileReader(FILE_CONFIG));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (config != null) {

            for (DataBaseElement db : dataBases) {
                for (DataConfig c : config.getBases()) {
                    if (db.getName().equals(c.getName())) {
                        db.setColor(new Color(c.getColor()));
                    }
                }
            }
        }

    }

    public static List<TableElement> importFields() {

        DataBaseStore dbs = null;
        List<TableElement> allTables = null;

        for (File f : getDBFiles()) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.INFO, "load {0}", f.getName());

            try {
                JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
                Unmarshaller um = context.createUnmarshaller();

                dbs = (DataBaseStore) um.unmarshal(new FileReader(f));

            } catch (JAXBException | FileNotFoundException ex) {
                Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (dbs != null) {

                if (allTables == null) {
                    allTables = new ArrayList<>(LIST_TABLE_SIZE);
                }

                for (DataBase db : dbs.getBases()) {
                    DataBaseElement dbEl = new DataBaseElement();

                    List<TableElement> dbTable = new ArrayList<>(30);

                    dbEl.setName(db.getName());
                    dbEl.setTables(dbTable);

                    dataBases.add(dbEl);
                    filterBases.add(dbEl);

                    for (Table t : db.getTables()) {
                        TableElement e = new TableElement(0, 0, dbEl, t.getName());

                        e.setFields(t.getFields() == null ? Collections.EMPTY_LIST : t.getFields());

                        dbTable.add(e);
                        allTables.add(e);
                    }
                }
            }
        }

        return allTables == null ? new ArrayList<TableElement>(10) : allTables;
    }

    public static DataBaseStore parseToDataBase(InputStreamReader is) {

        DataBaseStore dbs = null;

        try {
            JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
            Unmarshaller um = context.createUnmarshaller();

            dbs = (DataBaseStore) um.unmarshal(is);

        } catch (JAXBException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dbs;
    }

    public static void importFieldsPosition(List<TableElement> lst) {

        FieldPositionStore fps = null;

        if (!FILE_POSITION_STORE.exists()) {
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(FieldPositionStore.class);
            Unmarshaller um = context.createUnmarshaller();

            fps = (FieldPositionStore) um.unmarshal(new FileReader(FILE_POSITION_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (fps != null && fps.getFields() != null && !fps.getFields().isEmpty()) {
            for (TableElement e : lst) {
                for (FieldPosition fp : fps.getFields()) {
                    String fullName = e.getDataBase().getName() + "." + e.getName();
                    if (fullName.equals(fp.getFullName())) {
                        e.setPxy(fp.getPx(), fp.getPy());

                        if (fp.getColorName() != 0) {
                            e.setColor(new Color(fp.getColorName()));
                        }
                    }
                }
            }
        }
    }

    private static File[] getDBFiles() {

        if (!FILE_STORE.exists()) {
            FILE_STORE.mkdir();
        }

        File[] arr = FILE_STORE.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml");
            }
        });

        return arr;
    }

    public static void addDataBase(DataBaseElement db) {
        filterBases.add(db);
        dataBases.add(db);
    }

    public static void removeDataBase(DataBaseElement db) {
        filterBases.remove(db);
        dataBases.remove(db);
    }

    public static void removeField(TableElement e) {
        DataBaseElement db = e.getDataBase();
        db.getTables().remove(e);
        filter.remove(e);

        Undo.add(e);
    }

    public static DataBaseElement findByName(String name) {
        Iterator<DataBaseElement> it = dataBases.iterator();

        while (it.hasNext()) {
            DataBaseElement dbe = it.next();

            if (dbe.getName().equals(name)) {
                return dbe;
            }
        }

        return null;
    }

    public static TableElement findByName(DataBaseElement dbe, String name) {
        for (TableElement e : dbe.getTables()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }

        return null;
    }

}
