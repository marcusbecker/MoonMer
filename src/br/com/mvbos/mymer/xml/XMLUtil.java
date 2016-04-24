/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.entity.DataBaseEntity;
import br.com.mvbos.mymer.entity.EntityManager;
import br.com.mvbos.mymer.entity.IndexEntity;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.Index;
import br.com.mvbos.mymer.xml.field.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static final Boolean FORMATTED_OUTPUT = Boolean.TRUE;

    /*Folders*/
    public static final File CURRENT_PATH = new File(Common.currentPath);

    public static TransportDTO stringToTables(String s) {
        DataBaseStore dbs = null;
        List<TableElement> tables = null;
        List<IndexElement> indexes = new ArrayList<>(30);
        TransportDTO dto = new TransportDTO();

        DataBaseEntity dbEntity = EntityManager.e().getEntity(DataBaseEntity.class);

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
                DataBaseElement dbEl = dbEntity.findByName(db.getName());
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
                        e.setName(e.getName() + " " + ++DataBaseEntity.tableCount);
                    }

                    for (Index i : t.getIndices()) {
                        indexes.add(new IndexElement(i, e));
                    }

                    elTables.add(e);
                    tables.add(e);
                }
            }
        }

        dto.setTables(tables);
        dto.setIndexes(indexes);

        return dto;
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
                Table t = new Table(tb);

                IndexEntity ie = EntityManager.e().getEntity(IndexEntity.class);
                List<Index> indexLst = new ArrayList<>(15);

                for (IndexElement i : ie.findIndexByTable(tb)) {
                    indexLst.add(new Index(tb, i));
                }

                t.setIndices(indexLst);
                db.addTable(t);
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

    public static DataBaseStore parseToDataBase(InputStreamReader is) throws Exception {
        JAXBContext context = JAXBContext.newInstance(DataBaseStore.class);
        Unmarshaller um = context.createUnmarshaller();

        return (DataBaseStore) um.unmarshal(is);
    }

    public static InputStreamReader getFileInputStream(File file) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(file), Common.charset);
    }

    public static OutputStreamWriter getFileOutputStream(File file) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(file), Common.charset);
    }

}
