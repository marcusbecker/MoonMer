/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.el.draw.FullDraw;
import br.com.mvbos.mymer.el.draw.SimpleDraw;
import br.com.mvbos.mymer.el.draw.TinyDraw;
import br.com.mvbos.mymer.entity.EntityUtil;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.field.Table;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author MarcusS
 */
public class TableElement extends ElementModel {

    public enum State {

        NONE, ALLWAYS_VISIBLE
    }

    //public static final Font headerFont = new Font("Helvetica Neue,Helvetica,Arial,sans-serif", Font.BOLD, 14);
    public static final Font headerFont = new Font("Consolas", Font.BOLD, 14);
    public static final Font textFont = new Font("Arial", Font.PLAIN, 14);
    public static final Font typeFont = new Font("Arial", Font.PLAIN, 12);
    public static final Font fitFont = new Font("Arial", Font.PLAIN, 10);

    private DataBaseElement dataBase;
    private String description;
    private List<Field> fields = new ArrayList<>(10);

    private boolean autoWidth = true;
    private boolean autoHeight = true;

    private State state = State.NONE;

    private final SimpleDraw[] drawModes;

    public TableElement(DataBaseElement dataBase, Table tb) {
        this(0, 0, 50, 50, dataBase, tb.getName(), tb.getDescription());
        this.fields.addAll(tb.getFields());

    }

    public TableElement(int width, int height, DataBaseElement dataBase, String name) {
        this(0, 0, width, height, dataBase, name, "");
    }

    public TableElement(float px, float py, int width, int height, DataBaseElement dataBase, String name) {
        this(0, 0, width, height, dataBase, name, "");
    }

    public TableElement(float px, float py, int width, int height, DataBaseElement dataBase, String name, String description) {
        super(px, py, width, height, name);
        this.dataBase = dataBase;
        this.description = description;

        if (dataBase == null) {
            this.setColor(new Color(74, 189, 218));

        } else {
            if (dataBase.getColor() == null) {
                dataBase.setColor(new Color(74, 189, 218));
            }

            this.setColor(dataBase.getColor());
        }

        drawModes = new SimpleDraw[3];
        drawModes[0] = new SimpleDraw(this);
        drawModes[1] = new FullDraw(this);
        drawModes[2] = new TinyDraw(this);
    }

    public void addFields(Field field) {
        this.fields.add(field);
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public DataBaseElement getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBaseElement dataBase) {
        this.dataBase = dataBase;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void update() {
        SimpleDraw sd = getSelectedDrawMode();
        sd.update(Common.graphics);
    }

    @Override
    public void drawMe(Graphics2D g) {
        SimpleDraw sd = getSelectedDrawMode();
        sd.draw(g);
    }

    public boolean isAutoWidth() {
        return autoWidth;
    }

    public void setAutoWidth(boolean autoWidth) {
        this.autoWidth = autoWidth;
    }

    public boolean isAutoHeight() {
        return autoHeight;
    }

    public void setAutoHeight(boolean autoHeight) {
        this.autoHeight = autoHeight;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.name);
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

        final TableElement other = (TableElement) obj;
        return EntityUtil.compareName(this.name, other.name) && Objects.equals(this.getDataBase(), other.getDataBase());
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isCrop() {
        return state != State.ALLWAYS_VISIBLE && Common.crop;
    }

    private SimpleDraw getSelectedDrawMode() {
        return drawModes[0];
    }

}
