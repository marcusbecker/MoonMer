/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.table;

import br.com.mvbos.mymer.annotation.TableFieldAnnotation;
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
    private List<Field> data;
    private final java.lang.reflect.Field[] fields;

    private DataChangeListener dataChangeListener;
    private boolean disable;

    public void addDataChangeListener(DataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public void disableEdition(boolean disable) {
        this.disable = disable;
    }

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

        colTypes = new ArrayList<>(fields.length + 1);
        colTypes.add(new ColType("#", Integer.class, false));

        for (java.lang.reflect.Field f : fields) {
            TableFieldAnnotation a = f.getAnnotation(TableFieldAnnotation.class);
            String name = (a != null && !a.tableLabel().isEmpty()) ? a.tableLabel() : f.getName();

            colTypes.add(new ColType(name, f.getType(), true));
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return !disable && colTypes.get(columnIndex).edit;
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
            Object old;

            java.lang.reflect.Field ff = fields[classCol];
            ff.setAccessible(true);
            old = ff.get(f);
            ff.set(f, value);

            fireTableCellUpdated(row, col);

            if (dataChangeListener != null) {
                dataChangeListener.dataChange(new DataChange(row, classCol, f, old, value));
            }

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

    public boolean moveRow(int sel, boolean up) {
        int pos = up ? sel - 1 : sel + 1;
        if (pos < 0 || pos >= data.size()) {
            return false;
        }

        Field temp = data.get(pos);
        data.set(pos, data.get(sel));
        data.set(sel, temp);

        fireTableRowsUpdated(up ? pos : sel, up ? sel : pos);

        return true;
    }

    public void addField(Field field) {
        addField(-1, field);
    }

    public void addField(int index, Field field) {
        if (index > -1) {
            data.add(index, field);
        } else {
            data.add(field);
        }

        fireTableRowsInserted(index == -1 ? data.size() : index, index == -1 ? data.size() : index);
        //fireTableDataChanged();
    }

    public void removeField(int position) {
        data.remove(position);
        fireTableRowsDeleted(position, position);
    }

    public void removeField(Field field) {
        int index = data.indexOf(field);
        if (index > - 1) {
            removeField(index);
        }
    }

}
