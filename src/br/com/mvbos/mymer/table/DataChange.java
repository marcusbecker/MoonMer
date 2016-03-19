/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.table;

/**
 *
 * @author MarcusS
 */
public class DataChange {

    private int row;
    private int col;
    private Object source;

    private Object oldValue;
    private Object newValue;

    public DataChange(int row, int col, Object source, Object oldValue, Object newValue) {
        this.row = row;
        this.col = col;
        this.source = source;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "DataChange{" + "row=" + row + ", col=" + col + ", source=" + source + ", oldValue=" + oldValue + ", newValue=" + newValue + '}';
    }

}
