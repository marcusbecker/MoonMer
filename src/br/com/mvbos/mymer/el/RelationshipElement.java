/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.jeg.window.Camera;
import br.com.mvbos.mymer.xml.field.Field;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Marcus Becker
 */
public class RelationshipElement extends ElementModel {

    public enum Type {

        ONE_TO_ONE("1 .. 1"), ONE_TO_MORE("1 .. *");

        public String label;

        Type(String label) {
            this.label = label;
        }

    }

    private static Color defaultColor = Color.LIGHT_GRAY;

    private Camera cam;

    private Type type;// = Type.ONE_TO_ONE;
    private TableElement parent;
    private TableElement child;

    private List<Field> parentFields;
    private List<Field> childFields;

    private Map<Field, Field> group = new HashMap<>(5);

    public RelationshipElement() {
    }

    public RelationshipElement(Type type, TableElement parent, TableElement child) {
        this.type = type;
        this.parent = parent;
        this.child = child;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TableElement getParent() {
        return parent;
    }

    public void setParent(TableElement parent) {
        this.parent = parent;
    }

    public TableElement getChild() {
        return child;
    }

    public void setChild(TableElement child) {
        this.child = child;
    }

    public List<Field> getParentFields() {
        if (parentFields == null) {
            parentFields = new ArrayList<>(5);
        }

        return parentFields;
    }

    public void setParentFields(List<Field> parentField) {
        this.parentFields = parentField;
    }

    public List<Field> getChildFields() {
        if (childFields == null) {
            childFields = new ArrayList<>(5);
        }

        return childFields;
    }

    public void setChildFields(List<Field> childField) {
        this.childFields = childField;
    }

    public Camera getCam() {
        return cam == null ? Camera.c() : cam;
    }

    public void setCam(Camera cam) {
        this.cam = cam;
    }

    public Map<Field, Field> getGroup() {
        return group;
    }

    public void setGroup(Map<Field, Field> group) {
        this.group = group;
    }

    @Override
    public void drawMe(Graphics2D g) {

        if (parent == null || child == null || !isVisible()) {
            return;
        }

        if (parent.equals(child)) {
            g.setColor(parent.getColor());
            g.drawRect(getPx() - 10, getAllHeight() - 10, 20, 20);

        } else {

            int stpxA = (int) getCam().fx(parent.getAllWidth() - parent.getHalfWidth());
            int stpxB = (int) getCam().fx(child.getAllWidth() - child.getHalfWidth());

            int stpyA = (int) getCam().fy(parent.getAllHeight() - parent.getHalfHeight());
            int stpyB = (int) getCam().fy(child.getAllHeight() - child.getHalfHeight());

            final int middle = (stpxA + stpxB) / 2;

            g.setColor(parent.getColor());
            g.drawLine(stpxA, stpyA, middle, stpyA);

            g.setColor(child.getColor());
            g.drawLine(stpxB, stpyB, middle, stpyB);

            g.setColor(defaultColor);
            g.drawLine(middle, stpyA, middle, stpyB);

            final int fSize = g.getFontMetrics(g.getFont()).stringWidth(getDesc(type, false));

            g.drawString(getDesc(type, parent.getPx() > child.getPx()), middle - fSize / 2, (stpyA + stpyB) / 2);
        }

    }

    private String getDesc(Type type, boolean invert) {
        switch (type) {
            case ONE_TO_ONE:
                return "1. .1";
            case ONE_TO_MORE:
                return invert ? "*. .1" : "1. .*";
            default:
                return "";
        }
    }

    @Override
    public String toString() {
        return "RelationshipElement{" + "type=" + type + ", parent=" + parent.toString() + ", child=" + child.toString() + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
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

        final RelationshipElement other = (RelationshipElement) obj;

        if (this.type != other.type) {
            return false;
        }

        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }

        return Objects.equals(this.child, other.child);
    }

    public boolean isPart(TableElement t) {
        if (t == null) {
            return false;
        }

        return parent == t || child == t;

    }

}
