/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.jeg.window.Camera;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

/**
 *
 * @author Marcus Becker
 */
public class RelationshipElement extends ElementModel {
    
    public enum Type {
        
        ONE_TO_ONE, ONE_TO_MORE,
    }
    
    private static Color defaultColor = Color.LIGHT_GRAY;
    
    private Type type;// = Type.ONE_TO_ONE;
    private TableElement parent;
    private TableElement child;
    
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
    
    @Override
    public void drawMe(Graphics2D g) {
        
        if (parent.equals(child)) {
            g.setColor(parent.getColor());
            g.drawRect(getPx() - 10, getAllHeight() - 10, 20, 20);
            System.out.println("0000");
            
        } else {
            
            int stpxA = (int) Camera.c().fx(parent.getAllWidth() - parent.getHalfWidth());
            int stpxB = (int) Camera.c().fx(child.getAllWidth() - child.getHalfWidth());
            
            int stpyA = (int) Camera.c().fy(parent.getAllHeight() - parent.getHalfHeight());
            int stpyB = (int) Camera.c().fy(child.getAllHeight() - child.getHalfHeight());
            
            final int middle = (stpxA + stpxB) / 2;
            
            g.setColor(parent.getColor());
            g.drawLine(stpxA, stpyA, middle, stpyA);
            
            g.setColor(child.getColor());
            g.drawLine(stpxB, stpyB, middle, stpyB);
            
            g.setColor(defaultColor);
            g.drawLine(middle, stpyA, middle, stpyB);
            
            final int fSize = g.getFontMetrics(g.getFont()).stringWidth(getDesc(type));
            g.drawString(getDesc(type), middle - fSize / 2, (stpyA + stpyB) / 2);
        }
        
    }
    
    private String getDesc(Type type) {
        switch (type) {
            case ONE_TO_ONE:
                return "1. .1";
            case ONE_TO_MORE:
                return "1. .*";
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
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.child, other.child)) {
            return false;
        }
        return true;
    }
    
}
