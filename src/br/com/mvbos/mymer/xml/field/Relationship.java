/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import br.com.mvbos.mymer.el.RelationshipElement;
import java.util.HashSet;
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

    private Set<FieldGroup> fieldGroup = new HashSet<>(5);

    public Relationship() {
    }

    public Relationship(RelationshipElement e) {
        this(e.getType().ordinal(), e.getParent().getName(), e.getChild().getName(), e.getParent().getDataBase().getName(), e.getChild().getDataBase().getName());
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

    public Set<FieldGroup> getFieldGroup() {
        return fieldGroup;
    }

    public void setFieldGroup(Set<FieldGroup> fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.parent);
        hash = 19 * hash + Objects.hashCode(this.child);
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
        if (!Objects.equals(this.child, other.child)) {
            return false;
        }
        if (!Objects.equals(this.dbParente, other.dbParente)) {
            return false;
        }
        return Objects.equals(this.dbChild, other.dbChild);
    }

    @Override
    public String toString() {
        return "Relationship{" + "type=" + type + ", parent=" + parent + ", child=" + child + '}';
    }

}
