/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.ViewTable;

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

}
