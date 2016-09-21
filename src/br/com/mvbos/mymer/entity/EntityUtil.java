/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.entity;

import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.DataBaseStore;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Table;
import br.com.mvbos.mymer.xml.field.ViewTable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcuss
 */
public class EntityUtil {

    public static boolean compare(TableElement t, ViewTable v) {
        if (t == null || v == null) {
            return false;
        }

        if (t.getDataBase().getName().equalsIgnoreCase(v.getDataBaseName())) {
            return t.getName().equalsIgnoreCase(v.getTableName());
        }

        return false;
    }

    /**
     * Generate a new TableElement object with new Field objects
     *
     * @param te
     * @return
     */
    public static TableElement clone(TableElement te) {
        TableElement nte = new TableElement(te.getPx() + 5, te.getPy() + 5, te.getWidth(), te.getHeight(), te.getDataBase(), "copy_" + te.getName());

        //nte.setColor(te.getColor());
        for (Field f : te.getFields()) {
            Field ff = new Field(f.getName(), f.getType());
            nte.addFields(ff);
        }

        nte.update();
        te.getDataBase().getTables().add(nte);

        return nte;
    }

    /**
     * Generate a new TableElement object but share the Fields object
     *
     * @param t
     * @return
     */
    public static TableElement copy(TableElement t) {
        TableElement copy = new TableElement(0, 0, t.getWidth(), t.getHeight(), t.getDataBase(), t.getName());
        //copy.setColor(t.getColor());
        copy.setFields(t.getFields());
        copy.update();

        return copy;
    }

    public static int indexOfFieldByName(List<Field> fields, String name) {
        for (Field f : fields) {
            if (compareName(f.getName(), name)) {
                return fields.indexOf(f);
            }
        }

        return -1;
    }

    public static Field findFieldByName(List<Field> fields, String name) {
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                return f;
            }
        }

        return null;
    }

    public static DataBaseElement findBaseByName(List<DataBaseElement> dataBases, String baseName) {
        /*Iterator<DataBaseElement> it = dataBases.iterator();

         while (it.hasNext()) {
         DataBaseElement dbe = it.next();

         if (dbe.getName().equals(baseName)) {
         return dbe;
         }
         }*/

        for (DataBaseElement d : dataBases) {
            if (d.getName().equals(baseName)) {
                return d;
            }
        }

        return null;
    }

    public static TableElement findTableByName(List<DataBaseElement> dataBases, String baseName, String tableName) {
        DataBaseElement dbe = EntityUtil.findBaseByName(dataBases, baseName);

        if (dbe == null) {
            return null;
        }

        for (TableElement e : dbe.getTables()) {
            if (e.getName().equals(tableName)) {
                return e;
            }
        }

        return null;
    }

    public static List<IndexElement> findIndex(List<IndexElement> indices, TableElement e) {
        List<IndexElement> lst = new ArrayList<>(5);

        for (IndexElement ie : indices) {
            if (ie.getTable().equals(e)) {
                lst.add(ie);
            }
        }

        return lst;
    }

    public static IndexElement findIndex(List<IndexElement> indices, String dbName, String tbName, String indName) {
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

    public static Set<RelationshipElement> findRelationship(List<RelationshipElement> relations, List<TableElement> lst) {
        Set<RelationshipElement> set = new HashSet<>(10);

        for (RelationshipElement re : relations) {
            if (lst.contains(re.getParent()) && lst.contains(re.getChild())) {
                set.add(re);
            }
        }

        return set;
    }

    public static Set<RelationshipElement> findRelationship(List<RelationshipElement> relations, TableElement e) {
        Set<RelationshipElement> set = new HashSet<>(10);

        for (RelationshipElement re : relations) {
            if (re.getParent().equals(e) || re.getChild().equals(e)) {
                set.add(re);
            }
        }

        return set;
    }

    public static boolean maths(String filter, Field f) {
        return f.getName().toLowerCase().contains(filter.toLowerCase());
    }

    public static boolean maths(String filter, IndexElement i) {
        return i.getName().toLowerCase().contains(filter.toLowerCase());
    }

    public static boolean maths(String filter, TableElement t) {
        return t.getName().toLowerCase().contains(filter.toLowerCase());
    }

    /**
     * Convert ViewTable in TableElement
     *
     * @param tableElement
     * @param viewTable
     * @return tableElement
     */
    public static TableElement convert(TableElement tableElement, ViewTable viewTable) {
        TableElement copy = EntityUtil.copy(tableElement);

        copy.setPxy(viewTable.getPx(), viewTable.getPy());

        return copy;

    }

    public static DataBase findBaseByName(DataBaseStore dbs, String baseName) {
        if (dbs != null && dbs.hasBases()) {
            for (DataBase db : dbs.getBases()) {
                if (db.getName().equals(baseName)) {
                    return db;
                }
            }
        }

        return null;
    }

    public static Table findTableByName(DataBase db, String tableName) {
        for (Table t : db.getTables()) {
            if (t.getName().equalsIgnoreCase(tableName)) {
                return t;
            }
        }

        return null;
    }

    public static TableElement findTableByName(DataBaseElement db, String tableName) {
        for (TableElement t : db.getTables()) {
            if (t.getName().equalsIgnoreCase(tableName)) {
                return t;
            }
        }

        return null;
    }

    public static int sumTables(DataBaseStore db) {
        int sum = 0;
        for (DataBase d : db.getBases()) {
            sum += d.getTables().size();
        }

        return sum;
    }

    public static <T> int query(List<T> lst, String fieldName, String value) {

        if (lst == null || lst.isEmpty() || fieldName == null || value == null) {
            throw new IllegalArgumentException();
        }

        java.lang.reflect.Field[] fields = lst.get(0).getClass().getDeclaredFields();

        try {
            for (int i = 0; i < lst.size(); i++) {
                T f = lst.get(i);

                for (java.lang.reflect.Field fl : fields) {
                    if (!fl.getName().equals(fieldName)) {
                        continue;
                    }

                    fl.setAccessible(true);

                    if (value.equals(fl.get(f))) {
                        return i;
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(EntityUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return -1;
    }

    public static boolean compareName(String name, String other) {
        return name == null ? other == null : name.equalsIgnoreCase(other);
    }

    public static Field findRelationshipByName(List<Field> fields, String name) {
        for (Field f : fields) {

            if (EntityUtil.compareName(f.getName(), name)) {
                return f;
            }
        }

        return null;
    }

    public static int indexOfIndexByName(List<IndexElement> lst, String name) {
        for (int i = 0; i < lst.size(); i++) {
            if (compareName(lst.get(i).getName(), name)) {
                return i;
            }
        }

        return -1;
    }

    public static <T> Collection<T> notNull(Collection<T> list) {
        return list == null ? Collections.EMPTY_LIST : list;
    }

    public static boolean hasValue(String string) {
        return string != null && !string.trim().isEmpty();
    }

}
