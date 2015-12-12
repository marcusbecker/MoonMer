/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.Find;
import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.DataConfig;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.FieldPosition;
import br.com.mvbos.mymer.xml.field.Index;
import br.com.mvbos.mymer.xml.field.Relationship;
import br.com.mvbos.mymer.xml.field.Table;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
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
    public static final List<IndexElement> indices;
    public static final List<RelationshipElement> relations;
    public static final List<DataBaseElement> filterBases = new ArrayList<>(10);

    public static Set<DataBaseElement> dataBases = new LinkedHashSet<>(10);

    private static final int LIST_TABLE_SIZE = 60;
    private static final Boolean FORMATTED_OUTPUT = Boolean.TRUE;


    /*Folders*/
    private static final File CURRENT_PATH = new File(".");
    private static final File DIR_CONFIG = new File(CURRENT_PATH, "config");
    private static final File FILE_DIR_DB = new File(CURRENT_PATH, "dbs");
    private static final File FILE_DIR_REL = new File(CURRENT_PATH, "relations");
    private static final File FILE_DIR_INDEX = new File(CURRENT_PATH, "index");

    /* Files */
    private static final File FILE_CONFIG = new File(DIR_CONFIG, "config.xml");
    private static final File FILE_INDEX_STORE = new File(FILE_DIR_INDEX, "index.xml");
    private static final File FILE_POSITION_STORE = new File(DIR_CONFIG, "field_config.xml");
    private static final File FILE_RELATIONSHIP_STORE = new File(FILE_DIR_REL, "relationship_config.xml");

    static {
        DEFAULT_DATA_BASE = new DataBaseElement("New data base", new Color(74, 189, 218));

        filter = importFields();
        importFieldsPosition(filter);
        importConfig();

        relations = importRelations();

        indices = importIndices();

        tableCount = filter.size();

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

            File dst = new File(FILE_DIR_DB, k.concat(".xml"));

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

    public static String tablesToString(List<ElementModel> lst) {

        StringWriter sw = new StringWriter(500);
        Map<DataBaseElement, List<TableElement>> map = new HashMap<>(10);

        for (ElementModel el : lst) {

            if (!(el instanceof TableElement)) {
                continue;
            }

            TableElement tb = (TableElement) el;

            if (!map.containsKey(tb.getDataBase())) {
                map.put(tb.getDataBase(), new ArrayList<TableElement>(lst.size()));
            }

            map.get(tb.getDataBase()).add(tb);
        }

        for (DataBaseElement dbe : map.keySet()) {

            DataBase db = new DataBase(dbe);

            for (TableElement tb : map.get(dbe)) {
                db.addTable(new Table(tb));
            }

            DataBaseStore dbs = new DataBaseStore();
            dbs.addBase(db);

            try {
                JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);

                m.marshal(dbs, sw);

            } catch (Exception ex) {
                Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return sw.toString();
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

    public static boolean exportRelations() {

        RelationshipStore rStore = new RelationshipStore();
        List<Relationship> rel = new ArrayList<>(relations.size());

        for (RelationshipElement e : relations) {
            Relationship r = new Relationship(e.getType().ordinal(), e.getParent().getName(), e.getChild().getName(), e.getParent().getDataBase().getName(), e.getChild().getDataBase().getName());

            r.setChildFields(new LinkedHashSet<>(e.getChildFields()));
            r.setParentFields(new LinkedHashSet<>(e.getParentFields()));

            rel.add(r);
        }

        rStore.setRelations(rel);

        try {
            if (!FILE_DIR_REL.exists()) {
                FILE_DIR_REL.mkdir();
            }

            JAXBContext context = JAXBContext.newInstance(RelationshipStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);

            m.marshal(rStore, FILE_RELATIONSHIP_STORE);

        } catch (Exception ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    public static boolean exportIndices() {

        IndexStore rStore = new IndexStore();
        List<Index> ind = new ArrayList<>(indices.size());

        for (IndexElement ie : indices) {
            Index i = new Index(ie.getTable().getDataBase().getName(), ie.getTable().getName(), ie.getName(), ie.getPrimary(), ie.getUnique(), ie.getActive(), ie.getFields());
            ind.add(i);
        }

        rStore.setIndices(ind);

        try {
            if (!FILE_DIR_INDEX.exists()) {
                FILE_DIR_INDEX.mkdir();
            }

            JAXBContext context = JAXBContext.newInstance(IndexStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);

            m.marshal(rStore, FILE_INDEX_STORE);

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
                    if (DEFAULT_DATA_BASE.getName().equals(db.getName()) && db.getTables().isEmpty()) {
                        continue;
                    }

                    DataBaseElement dbEl = new DataBaseElement();

                    List<TableElement> elTables = new ArrayList<>(db.getTables().size());

                    dbEl.setName(db.getName());
                    dbEl.setTables(elTables);

                    dataBases.add(dbEl);
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

        return allTables == null ? new ArrayList<TableElement>(10) : allTables;
    }

    public static List<RelationshipElement> importRelations() {

        RelationshipStore rStore = null;
        List<RelationshipElement> lst = null;

        try {

            if (!FILE_DIR_REL.exists()) {
                FILE_DIR_REL.mkdir();
            }

            if (FILE_RELATIONSHIP_STORE.exists()) {

                JAXBContext context = JAXBContext.newInstance(RelationshipStore.class);
                Unmarshaller um = context.createUnmarshaller();

                rStore = (RelationshipStore) um.unmarshal(new FileReader(FILE_RELATIONSHIP_STORE));
            }

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (rStore != null && rStore.getRelations() != null && !rStore.getRelations().isEmpty()) {

            lst = new ArrayList<>(rStore.getRelations().size());

            for (Relationship r : rStore.getRelations()) {

                if (r.getType() < 0 || r.getType() > RelationshipElement.Type.values().length) {
                    continue;
                }

                TableElement parent = findByName(r.getDbParente(), r.getParent());
                if (parent == null) {
                    continue;
                }

                TableElement child = findByName(r.getDbChild(), r.getChild());
                if (child == null) {
                    continue;
                }

                RelationshipElement.Type type = RelationshipElement.Type.values()[r.getType()];
                RelationshipElement re = new RelationshipElement(type, parent, child);

                for (Field f : r.getParentFields()) {
                    Field ff = Find.findByName(parent.getFields(), f.getName());
                    if (ff != null) {
                        re.getParentFields().add(ff);
                    }
                }

                for (Field f : r.getChildFields()) {
                    Field ff = Find.findByName(child.getFields(), f.getName());
                    if (ff != null) {
                        re.getChildFields().add(ff);
                    }
                }

                lst.add(re);
            }
        }

        return lst == null ? new ArrayList<RelationshipElement>(10) : lst;
    }

    public static List<IndexElement> importIndices() {

        IndexStore iStore = null;
        List<IndexElement> lst = null;

        try {

            if (!FILE_DIR_INDEX.exists()) {
                FILE_DIR_INDEX.mkdir();
            }

            if (FILE_INDEX_STORE.exists()) {

                JAXBContext context = JAXBContext.newInstance(IndexStore.class);
                Unmarshaller um = context.createUnmarshaller();

                iStore = (IndexStore) um.unmarshal(new FileReader(FILE_INDEX_STORE));
            }

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (iStore != null && iStore.getIndices() != null && !iStore.getIndices().isEmpty()) {

            lst = new ArrayList<>(iStore.getIndices().size());

            for (Index i : iStore.getIndices()) {

                TableElement tb = findByName(i.getDataBaseName(), i.getTableName());
                if (tb == null) {
                    continue;
                }

                IndexElement ie = new IndexElement(i.getName(), i.getPrimary(), i.getUnique(), i.getActive(), tb);

                if (i.getFields() != null) {
                    List<Field> lstField = new ArrayList<>(i.getFields().size());
                    for (Field f : i.getFields()) {
                        int index = tb.getFields().indexOf(f);

                        if (index != -1) {
                            lstField.add(tb.getFields().get(index));
                        }
                    }

                    ie.setFields(lstField);
                }

                lst.add(ie);
            }
        }

        return lst == null ? new ArrayList<IndexElement>(10) : lst;
    }

    public static DataBaseStore parseToDataBase(InputStreamReader is) throws JAXBException {

        DataBaseStore dbs = null;

        JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
        Unmarshaller um = context.createUnmarshaller();

        return (DataBaseStore) um.unmarshal(is);
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

    public static TableElement findByName(String dbName, String name) {
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

    public static TableElement findByName(DataBaseElement dbe, String name) {
        for (TableElement e : dbe.getTables()) {
            if (e.getName().equals(name)) {
                return e;
            }
        }

        return null;
    }

    public static void addNewRelationship(RelationshipElement.Type type, TableElement elLeft, TableElement elRight) {
        relations.add(new RelationshipElement(type, elLeft, elRight));
    }

    public static List<IndexElement> findIndex(TableElement e) {
        List<IndexElement> lst = new ArrayList<>(5);

        for (IndexElement ie : indices) {
            if (ie.getTable().equals(e)) {
                lst.add(ie);
            }
        }

        return lst;
    }

    public static IndexElement findIndexByName(String name, TableElement e) {

        for (IndexElement ie : indices) {
            if (ie.getTable().equals(e)) {
                if (ie.getName().equals(name)) {
                    return ie;
                }
            }
        }

        return null;
    }

    public static IndexElement findIndex(String dbName, String tbName, String indName) {
        for (IndexElement ie : indices) {

            if (!tbName.equalsIgnoreCase(ie.getTable().getName())) {
                continue;
            }

            if (!dbName.equalsIgnoreCase(ie.getTable().getDataBase().getName())) {
                continue;
            }

            if (ie.getName().equals(indName)) {
                return ie;
            }

        }

        return null;
    }

}
