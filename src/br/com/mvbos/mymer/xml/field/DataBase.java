/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import br.com.mvbos.mymer.el.DataBaseElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class DataBase {

    private String name;
    private List<Table> tables = new ArrayList<>(10);

    public DataBase() {
    }

    public DataBase(String name) {
        this.name = name;
    }

    public DataBase(String name, List<Table> tables) {
        this.name = name;
        this.tables = tables;
    }

    public DataBase(DataBaseElement db) {
        this.name = db.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public void addTable(Table tb) {
        tables.add(tb);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.name);
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
        final DataBase other = (DataBase) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "DataBase{" + "name=" + name + '}';
    }

}
