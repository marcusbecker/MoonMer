/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.Common;
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
import br.com.mvbos.mymer.xml.field.View;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author MarcusS
 */
public class XMLUtil {

    public static int tableCount;

    public static final DataBaseElement DEFAULT_DATA_BASE;

    private static final List<TableElement> filter;
    public static final List<IndexElement> indices;
    public static final List<RelationshipElement> relations;
    private static final List<DataBaseElement> filterBases = new ArrayList<>(10);

    public static Set<DataBaseElement> dataBases = new LinkedHashSet<>(10);

    private static final int LIST_TABLE_SIZE = 60;
    private static final Boolean FORMATTED_OUTPUT = Boolean.TRUE;

    /*Folders*/
    private static final File CURRENT_PATH = new File(Common.currentPath);
    private static final File DIR_CONFIG = new File(CURRENT_PATH, "config");
    private static final File FILE_DIR_DB = new File(CURRENT_PATH, "dbs");
    private static final File FILE_DIR_REL = new File(CURRENT_PATH, "relations");
    private static final File FILE_DIR_INDEX = new File(CURRENT_PATH, "index");
    private static final File FILE_DIR_VIEWS = new File(CURRENT_PATH, "views");

    /* Files */
    private static final File FILE_CONFIG = new File(DIR_CONFIG, "config.xml");
    private static final File FILE_INDEX_STORE = new File(FILE_DIR_INDEX, "index.xml");
    private static final File FILE_VIEW_STORE = new File(FILE_DIR_VIEWS, "view.xml");
    private static final File FILE_POSITION_STORE = new File(DIR_CONFIG, "field_config.xml");
    private static final File FILE_RELATIONSHIP_STORE = new File(FILE_DIR_REL, "relationship_config.xml");

    public static final int EVT_ADD = 0;
    public static final int EVT_REMOVE = 1;

    private static final List<ActionListener> listern = new ArrayList<>(4);

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

                m.marshal(dbs, getFileOutputStream(dst));

            } catch (JAXBException | FileNotFoundException ex) {
                Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

                return false;
            }
        }

        return true;
    }

    public static List<TableElement> stringToTables(String s) {
        DataBaseStore dbs = null;
        List<TableElement> tables = null;

        try {
            JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
            Unmarshaller um = context.createUnmarshaller();
            dbs = (DataBaseStore) um.unmarshal(new StreamSource(new StringReader(s)));

        } catch (JAXBException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (dbs != null) {

            if (tables == null) {
                tables = new ArrayList<>(30);
            }

            for (DataBase db : dbs.getBases()) {
                DataBaseElement dbEl = XMLUtil.findByName(db.getName());
                List<TableElement> elTables;

                if (dbEl == null) {
                    dbEl = new DataBaseElement(db);

                    elTables = new ArrayList<>(db.getTables().size());
                    dbEl.setTables(elTables);

                } else {
                    elTables = dbEl.getTables();
                }

                for (Table t : db.getTables()) {
                    TableElement e = new TableElement(0, 0, dbEl, t.getName());

                    if (t.getFields() != null) {
                        e.setFields(t.getFields());
                    }

                    if (elTables.contains(e)) {
                        e.setName(e.getName() + " " + ++XMLUtil.tableCount);
                    }

                    elTables.add(e);
                    tables.add(e);
                }
            }
        }

        return tables;
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

            m.marshal(fps, getFileOutputStream(FILE_POSITION_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
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

            m.marshal(rStore, getFileOutputStream(FILE_RELATIONSHIP_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
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

            m.marshal(rStore, getFileOutputStream(FILE_INDEX_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
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

            m.marshal(cs, getFileOutputStream(FILE_CONFIG));

        } catch (JAXBException | FileNotFoundException ex) {
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

            config = (ConfigStore) um.unmarshal(getFileInputStream(FILE_CONFIG));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (config != null && config.getBases() != null) {

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

                dbs = (DataBaseStore) um.unmarshal(getFileInputStream(f));

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

                rStore = (RelationshipStore) um.unmarshal(getFileInputStream(FILE_RELATIONSHIP_STORE));
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

                iStore = (IndexStore) um.unmarshal(getFileInputStream(FILE_INDEX_STORE));
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

                IndexElement ie = new IndexElement(i, tb);

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

            fps = (FieldPositionStore) um.unmarshal(getFileInputStream(FILE_POSITION_STORE));

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

        ActionEvent evt = new ActionEvent(db, EVT_ADD, "ADD_DATABASE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }
    }

    public static void removeDataBase(DataBaseElement db) {
        filterBases.remove(db);
        dataBases.remove(db);
    }

    public static List<DataBaseElement> getDataBase() {
        return filterBases;
    }

    public static void removeTable(TableElement e) {
        removeTable(e.getDataBase(), e);
    }

    public static void removeTable(DataBaseElement db, TableElement e) {
        db.getTables().remove(e);
        filter.remove(e);

        Undo.add(e);

        ActionEvent evt = new ActionEvent(e, EVT_REMOVE, "REMOVE_TABLE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }
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

    public static Set<RelationshipElement> findRelationship(List<TableElement> lst) {
        Set<RelationshipElement> set = new HashSet<>(10);

        for (RelationshipElement re : XMLUtil.relations) {
            if (lst.contains(re.getParent()) && lst.contains(re.getChild())) {
                set.add(re);
            }
        }

        return set;
    }

    public static Set<RelationshipElement> findRelationship(TableElement e) {
        Set<RelationshipElement> set = new HashSet<>(10);

        for (RelationshipElement re : XMLUtil.relations) {
            if (re.getParent().equals(e) || re.getChild().equals(e)) {
                set.add(re);
            }
        }

        return set;
    }

    public static List<View> loadViews() {

        List<View> lst = new ArrayList<>(20);

        if (!FILE_DIR_VIEWS.exists()) {
            return null;
        }

        File[] files = FILE_DIR_VIEWS.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml");
            }
        });

        for (File f : files) {
            try {
                JAXBContext context = JAXBContext.newInstance(ViewStore.class);
                Unmarshaller um = context.createUnmarshaller();

                ViewStore vs = (ViewStore) um.unmarshal(getFileInputStream(f));

                if (vs.getViews() != null) {
                    lst.addAll(vs.getViews());
                }

            } catch (JAXBException | FileNotFoundException ex) {
                Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return lst;
    }

    public static boolean saveViews(List<View> lst) {

        try {
            if (!FILE_DIR_VIEWS.exists()) {
                FILE_DIR_VIEWS.mkdir();
            }

            ViewStore vs = new ViewStore();
            vs.setViews(lst);

            JAXBContext context = JAXBContext.newInstance(ViewStore.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FORMATTED_OUTPUT);

            m.marshal(vs, getFileOutputStream(FILE_VIEW_STORE));

        } catch (JAXBException | FileNotFoundException ex) {
            Logger.getLogger(XMLUtil.class.getName()).log(Level.SEVERE, null, ex);

            return false;
        }

        return true;
    }

    private static InputStreamReader getFileInputStream(File file) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(file), Common.charset);
    }

    private static OutputStreamWriter getFileOutputStream(File file) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(file), Common.charset);
    }

    public static void addFilterTable(TableElement te) {
        XMLUtil.filter.add(te);

        ActionEvent evt = new ActionEvent(te, EVT_ADD, "ADD_TABLE");
        for (ActionListener a : listern) {
            a.actionPerformed(evt);
        }
    }

    public static List<TableElement> getFilterTable() {
        return filter;
    }

    public static void addActionListern(ActionListener actionListener) {
        listern.add(actionListener);
    }

    public static void addIndex(IndexElement indexElement) {
        indices.add(indexElement);
    }

    public static void removeIndex(IndexElement indexElement) {
        indices.remove(indexElement);
    }

}
