/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import br.com.mvbos.mymer.el.TableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author marcuss
 */
@XmlRootElement
public class View {

    private String name;
    private List<ViewTable> tables = new ArrayList<>(20);

    private List<TableElement> tempTables = new ArrayList<>(tables.size());

    public View() {
    }

    public View(String viewName) {
        this.name = viewName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ViewTable> getTables() {
        return tables;
    }

    public void setTables(List<ViewTable> tables) {
        this.tables = tables;
    }

    @XmlTransient
    public List<TableElement> getTempTables() {
        return tempTables;
    }

    public void setTempTables(List<TableElement> tempTables) {
        this.tempTables = tempTables;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final View other = (View) obj;
        return Objects.equals(this.name, other.name);
    }

    public void addTempTable(TableElement t) {
        tempTables.add(t);
    }

}
