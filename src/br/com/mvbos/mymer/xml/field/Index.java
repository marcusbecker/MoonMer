/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author marcuss
 */
public class Index implements Serializable {

    private String tableName;
    private String dataBaseName;

    private String name;
    private Boolean primary;
    private Boolean unique;
    private Boolean active;
    private List<Field> fields;

    public Index() {
    }

    public Index(String dataBaseName, String tableName, String name) {
        this.tableName = tableName;
        this.dataBaseName = dataBaseName;
        this.name = name;
    }

    public Index(String dataBaseName, String tableName, String name, Boolean primary, Boolean unique, Boolean active, List<Field> fields) {
        this.tableName = tableName;
        this.dataBaseName = dataBaseName;
        this.name = name;
        this.primary = primary;
        this.unique = unique;
        this.active = active;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.name);
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
        final Index other = (Index) obj;
        if (!Objects.equals(this.tableName, other.tableName)) {
            return false;
        }
        if (!Objects.equals(this.dataBaseName, other.dataBaseName)) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return "Index{" + "tableName=" + tableName + ", dataBaseName=" + dataBaseName + ", name=" + name + ", primary=" + primary + ", unique=" + unique + ", active=" + active + '}';
    }

}
