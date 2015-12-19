/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.mymer.xml.field.DataBase;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author MarcusS
 */
public class DataBaseElement {

    private String name;
    private Color color;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<TableElement> getTables() {
        return tables;
    }

    public void setTables(List<TableElement> tables) {
        this.tables = tables;
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
