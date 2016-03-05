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
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.ViewTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static TableElement clone(TableElement te) {
        TableElement nte = new TableElement(te.getPx() + 5, te.getPy() + 5, te.getWidth(), te.getHeight(), te.getDataBase(), "copy_" + te.getName());
        for (Field f : te.getFields()) {
            Field ff = new Field(f.getName(), f.getType());
            nte.addFields(ff);
        }

        nte.update();
        te.getDataBase().getTables().add(nte);

        return nte;
    }

    public static TableElement copy(TableElement t) {
        TableElement copy = new TableElement(0, 0, t.getWidth(), t.getHeight(), t.getDataBase(), t.getName());
        copy.setFields(t.getFields());
        copy.update();

        return copy;
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
        DataBaseElement dbe = findBaseByName(dataBases, baseName);

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

}
