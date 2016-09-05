/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el.draw;

import br.com.mvbos.mymer.annotation.TableFieldAnnotation;
import br.com.mvbos.mymer.xml.field.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author MarcusS
 */
public class DrawTableModel implements TableModel {

    private List<Field> data;
    private final java.lang.reflect.Field[] fields;
    private final String[] colNames;

    public DrawTableModel() {
        data = Collections.EMPTY_LIST;
        java.lang.reflect.Field[] temp = Field.class.getDeclaredFields();
        List<java.lang.reflect.Field> lst = new ArrayList<>(temp.length);

        for (java.lang.reflect.Field t : temp) {
            TableFieldAnnotation a = t.getAnnotation(TableFieldAnnotation.class);

            if (a == null || !a.ignore()) {
                lst.add(t);
            }
        }

        fields = lst.toArray(new java.lang.reflect.Field[0]);

        colNames = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            java.lang.reflect.Field f = fields[i];
            colNames[i] = f.getName();
        }
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int col) {

        Field f = data.get(row);

        try {
            java.lang.reflect.Field ff = fields[col];
            ff.setAccessible(true);

            return ff.get(f) == null ? "" : ff.get(f);

        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(DrawTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }

    public List<Field> getData() {
        return data;
    }

    public void setData(List<Field> data) {
        this.data = data;
    }

}
