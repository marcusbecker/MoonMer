/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.table;

import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.xml.field.Field;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Marcus Becker
 */
public class RelationshipTableModel extends AbstractTableModel {

    private RelationshipElement relationShip;
    private List<Field> parentFields = Collections.EMPTY_LIST;

    @Override
    public int getRowCount() {
        return parentFields.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "#";
            case 1:
                return getParentTitle();
            default:
                return getChildTitle();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowIndex + 1;
        } else if (columnIndex == 1) {
            return parentFields.get(rowIndex).getName();
        } else {
            return getLink(parentFields.get(rowIndex));
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Field f = parentFields.get(rowIndex);

        if (value == null) {
            relationShip.getGroup().remove(f);
        } else {
            for (Field ff : relationShip.getChildFields()) {
                if (ff.getName().equals(value)) {
                    relationShip.getGroup().put(f, ff);
                    break;
                }
            }
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > 1;
    }

    public RelationshipElement getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(RelationshipElement relationShip) {
        this.relationShip = relationShip;
        this.parentFields = Collections.EMPTY_LIST;

        if (relationShip != null && relationShip.getParent() != null) {
            if (relationShip.getParent().getFields() != null) {
                this.parentFields = relationShip.getParent().getFields();
            }
        }
    }

    private String getParentTitle() {
        if (relationShip != null) {
            return relationShip.getParent().getName();
        }

        return "";
    }

    private String getChildTitle() {
        if (relationShip != null) {
            return relationShip.getChild().getName();
        }

        return "";
    }

    private String getLink(Field f) {
        if (relationShip == null) {
            return "";
        }

        Field child = relationShip.getGroup().get(f);
        return child == null ? "" : child.getName();

    }

}
