/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.combo;

/**
 *
 * @author Marcus Becker
 */
public class Option {

    private short index;
    private String label;
    private Object value;

    public Option(short index, Object value, String label) {
        this.index = index;
        this.label = label;
        this.value = value;
    }

    public short getIndex() {
        return index;
    }

    public void setIndex(short index) {
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.index;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Option other = (Option) obj;
        return this.index == other.index;
    }

    @Override
    public String toString() {
        return label;
    }

}
