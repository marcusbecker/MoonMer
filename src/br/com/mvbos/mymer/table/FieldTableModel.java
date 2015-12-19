/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.table;

import br.com.mvbos.mymer.xml.field.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author MarcusS
 */
public class FieldTableModel extends AbstractTableModel {

    private final List<ColType> colTypes;
    private List<Field> data = Collections.EMPTY_LIST;
    private final java.lang.reflect.Field[] fields = Field.class.getDeclaredFields();

    private class ColType {

        String name;
        Class type;
        boolean edit;

        public ColType(String name, Class type, boolean edit) {
            this.name = name;
            this.type = type;
            this.edit = edit;
        }

    }

    public FieldTableModel() {
        colTypes = new ArrayList<>(fields.length + 1);
        colTypes.add(new ColType("#", Integer.class, false));

        for (java.lang.reflect.Field f : fields) {
            colTypes.add(new ColType(f.getName(), f.getType(), true));
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return colTypes.get(columnIndex).edit;
    }

    @Override
    public Class getColumnClass(int c) {
        return colTypes.get(c).type;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        int classCol = col - 1;

        if (classCol < 0) {
            return;
        }

        Field f = data.get(row);

        try {
            java.lang.reflect.Field ff = fields[classCol];
            ff.setAccessible(true);
            ff.set(f, value);

            fireTableCellUpdated(row, col);

        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(FieldTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String getColumnName(int column) {
        return colTypes.get(column).name;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return colTypes.size();
    }

    @Override
    public Object getValueAt(int row, int col) {

        int classCol = col - 1;

        if (classCol < 0) {
            return row + 1;
        }

        Field f = data.get(row);

        try {
            java.lang.reflect.Field ff = fields[classCol];
            ff.setAccessible(true);
            return ff.get(f);

        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(FieldTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public List<Field> getData() {
        return data;
    }

    public void setData(List<Field> data) {
        this.data = data;
    }

}
