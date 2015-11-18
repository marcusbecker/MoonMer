/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.mymer.xml.field.Field;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author MarcusS
 */
public class FieldTableModel extends AbstractTableModel {

    private Class[] columnTypes = {Integer.class, String.class, Integer.class, String.class, String.class, String.class};
    private String[] columnNames = {"#", "Name", "Size", "Type", "Format", "Default"};

    //private Object[][] data = {{"cod", 40, Common.comboTypes[0], "", ""}};
    //private Object[][] data = {};
    private List<Field> data = Collections.EMPTY_LIST;

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 0;
    }

    @Override
    public Class getColumnClass(int c) {
        //return getValueAt(0, c).getClass();
        return columnTypes[c];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Field f = data.get(row);

        switch (col) {
            case 1:
                f.setName((String) value);
                return;
            case 2:
                f.setSize((Integer) value);
                return;
            case 3:
                f.setType((String) value);
                return;
            case 4:
                f.setFormat((String) value);
                return;
            case 5:
                f.setDefualt((String) value);
                return;
        }

        fireTableCellUpdated(row, col);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {

        Field f = data.get(row);

        switch (col) {
            case 0:
                return row + 1;
            case 1:
                return f.getName();
            case 2:
                return f.getSize();
            case 3:
                return f.getType();
            case 4:
                return f.getFormat();
            case 5:
                return f.getDefualt();
        }

        return null;
        //return data[rowIndex][columnIndex];
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Field> getData() {
        return data;
    }

    public void setData(List<Field> data) {
        this.data = data;
    }

}
