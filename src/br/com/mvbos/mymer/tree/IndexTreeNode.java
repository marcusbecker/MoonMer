/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.tree;

import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.xml.field.Field;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Marcus Becker
 */
public class IndexTreeNode extends DefaultMutableTreeNode {

    private FieldTreeNode.Diff diff;

    public IndexTreeNode(IndexElement userObject) {
        super(userObject);
    }

    @Override
    public String toString() {
        if (userObject == null) {
            return "";

        }

        IndexElement ie = (IndexElement) userObject;

        StringBuilder sb = new StringBuilder(ie.getName());

        if (ie.getPrimary()) {
            sb.append(" PK ");
        }

        if (ie.getUnique()) {
            sb.append(" UQ ");
        }

        if (ie.getActive()) {
            sb.append(" A ");
        }

        sb.append(" [");

        if (ie.getFields() != null && !ie.getFields().isEmpty()) {
            for (Field f : ie.getFields()) {
                sb.append(f.getName()).append(", ");
            }

            sb.delete(sb.length() - ", ".length(), sb.length());
        }

        sb.append("]");

        if (diff != null) {
            switch (diff) {
                case DELETED:
                    sb.append(" ( - )");
                    break;
                case NEW:
                    sb.append(" ( + )");
                    break;
                case FIELD:
                    sb.append(" ( ~ )");
                    break;
            }
        }

        return sb.toString();
    }

    public IndexElement get() {
        return (IndexElement) userObject;
    }

    public FieldTreeNode.Diff getDiff() {
        return diff;
    }

    public void setDiff(FieldTreeNode.Diff diff) {
        this.diff = diff;
    }

}
