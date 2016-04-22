/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcus Becker
 */
@XmlRootElement
public class FieldGroup {

    private Field parent;
    private Field child;

    public FieldGroup() {
    }

    public FieldGroup(Field parent, Field child) {
        this.parent = parent;
        this.child = child;
    }

    public Field getParent() {
        return parent;
    }

    public void setParent(Field parent) {
        this.parent = parent;
    }

    public Field getChild() {
        return child;
    }

    public void setChild(Field child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "FieldGroup{" + "parent=" + parent + ", child=" + child + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.parent);
        hash = 83 * hash + Objects.hashCode(this.child);
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
        final FieldGroup other = (FieldGroup) obj;
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.child, other.child)) {
            return false;
        }
        return true;
    }

}
