/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.table;

import java.util.Objects;

/**
 *
 * @author Marcus Becker
 */
public class RowItemSelection {

    private Boolean selected;
    private String name;
    private Object value;

    public RowItemSelection(String name, Object value) {
        this.name = name;
        this.value = value;
        selected = Boolean.FALSE;
    }

    public RowItemSelection(Boolean selected, String name, Object value) {
        this.selected = selected;
        this.name = name;
        this.value = value;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        hash = 83 * hash + Objects.hashCode(this.value);
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
        final RowItemSelection other = (RowItemSelection) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return "RowItemSelection{" + "selected=" + selected + ", name=" + name + ", value=" + value + '}';
    }

}
