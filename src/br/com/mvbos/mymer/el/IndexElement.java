/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Index;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author marcuss
 */
public class IndexElement extends ElementModel {

    private String name;
    private Boolean primary;
    private Boolean unique;
    private Boolean active;
    private List<Field> fields;

    private final TableElement table;

    public IndexElement(String name, TableElement table) {
        this.name = name;
        this.table = table;
    }

    public IndexElement(String name, Boolean primary, Boolean unique, Boolean active, TableElement table) {
        this.name = name;
        this.primary = primary;
        this.unique = unique;
        this.active = active;
        this.table = table;
    }

    public IndexElement(Index i, TableElement tb) {
        this(i.getName(), i.getPrimary(), i.getUnique(), i.getActive(), tb);
    }

    public Boolean getPrimary() {
        return primary == null ? Boolean.FALSE : primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Boolean getUnique() {
        return unique == null ? Boolean.FALSE : unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getActive() {
        return active == null ? Boolean.FALSE : active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Field> getFields() {
        if (fields == null) {
            fields = new ArrayList<>(5);
        }

        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public TableElement getTable() {
        return table;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.name);
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

        final IndexElement other = (IndexElement) obj;

        return Objects.equals(this.getName(), other.getName()) && Objects.equals(this.getTable(), other.getTable());

    }

    @Override
    public String toString() {
        return "Index{" + "name=" + name + ", primary=" + primary + ", unique=" + unique + ", active=" + active + ", fields=" + fields + ", table=" + table.getName() + '}';
    }

    public String getOrgId() {
       return getName();
    }

}
