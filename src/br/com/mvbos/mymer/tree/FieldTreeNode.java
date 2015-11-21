/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.tree;

import br.com.mvbos.mymer.xml.field.Field;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Marcus Becker
 */
public class FieldTreeNode extends DefaultMutableTreeNode {

    public enum Diff {

        NONE, NEW, DELETED, FIELD
    }

    private Diff diff = Diff.NONE;

    public FieldTreeNode(Field userObject) {
        super(userObject);
    }

    public Diff getDiff() {
        return diff;
    }

    public void setDiff(Diff diff) {
        this.diff = diff;
    }

    @Override
    public String toString() {
        if (userObject == null) {
            return "";

        }

        String name = ((Field) userObject).getName();
        switch (diff) {
            case DELETED:
                name += " ( - )";
                break;
            case NEW:
                name += " ( + )";
                break;
            case FIELD:
                name += " ( ~ )";
                break;
        }

        return name;
    }

    public Field get() {
        return (Field) userObject;
    }

}
