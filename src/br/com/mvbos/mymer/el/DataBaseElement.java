/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.xml.field.DataBase;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author MarcusS
 */
public class DataBaseElement extends ElementModel {

    private List<TableElement> tables = new ArrayList<>(20);

    public DataBaseElement() {
    }

    public DataBaseElement(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public DataBaseElement(DataBase db) {
        this.name = db.getName();
    }

    public List<TableElement> getTables() {
        return tables;
    }

    public void setTables(List<TableElement> tables) {
        if (tables == null) {
            throw new IllegalArgumentException();
        }

        this.tables = tables;
    }

    public void addTable(TableElement tbe) {
        if (!tables.contains(tbe)) {
            tables.add(tbe);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
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
        final DataBaseElement other = (DataBaseElement) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "DataBaseElement{" + "name=" + name + ", color=" + color + '}';
    }

}
