/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class Relationship {

    private int type;
    private String parent;
    private String child;

    private String dbParente;
    private String dbChild;

    private Set<Field> parentFields = new LinkedHashSet<>(5);
    private Set<Field> childFields = new LinkedHashSet<>(5);

    public Relationship() {
    }

    public Relationship(int type, String parent, String child, String db) {
        this(type, parent, child, db, db);
    }

    public Relationship(int type, String parent, String child, String dbParente, String dbChild) {
        this.type = type;
        this.parent = parent;
        this.child = child;
        this.dbParente = dbParente;
        this.dbChild = dbChild;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getDbParente() {
        return dbParente;
    }

    public void setDbParente(String dbParente) {
        this.dbParente = dbParente;
    }

    public String getDbChild() {
        return dbChild;
    }

    public void setDbChild(String dbChild) {
        this.dbChild = dbChild;
    }

    public Set<Field> getParentFields() {
        return parentFields;
    }

    public void setParentFields(Set<Field> parentFields) {
        this.parentFields = parentFields;
    }

    public Set<Field> getChildFields() {
        return childFields;
    }

    public void setChildFields(Set<Field> childFields) {
        this.childFields = childFields;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.parent);
        hash = 89 * hash + Objects.hashCode(this.child);
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
        final Relationship other = (Relationship) obj;
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }

        return Objects.equals(this.child, other.child);
    }

    @Override
    public String toString() {
        return "Relationship{" + "type=" + type + ", parent=" + parent + ", child=" + child + '}';
    }

}
