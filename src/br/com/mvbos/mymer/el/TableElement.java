/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.Common;
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

    private static final int LEFT_BORDER = 5;

    private short headerSize = 10;
    private short fieldSize = 10;
    //Field index type size
    private short fitSize;
    private DataBaseElement dataBase;
    private String description;
    private List<Field> fields = new ArrayList<>(10);

    private boolean autoWidth = true;
    private boolean autoHeight = true;
    private Color headerColor = Color.DARK_GRAY;

    private State state = State.NONE;

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
        if (Common.graphics != null) {

            Graphics2D g = Common.graphics;

            int c = (getColor().getRed() + getColor().getGreen() + getColor().getBlue()) / 3;
            headerColor = c < 128 ? Color.LIGHT_GRAY : Color.DARK_GRAY;

            if (autoHeight) {

                headerSize = (short) g.getFontMetrics(headerFont).getHeight();
                fieldSize = (short) (g.getFontMetrics(textFont).getHeight() + LEFT_BORDER);

                if (isCrop()) {
                    setHeight(fieldSize * (fields.size() > Common.maxRow ? Common.maxRow + 1 : fields.size()) + headerSize + 20);
                } else {
                    setHeight(fieldSize * fields.size() + headerSize + 20);
                }
            }

            if (autoWidth) {
                String maxWidth = getName();

                Common.ct = 0;

                for (Field f : fields) {
                    if (f.getName().length() > maxWidth.length()) {
                        maxWidth = f.getName();
                    }

                    if (isCrop()) {
                        if (++Common.ct == Common.maxRow) {
                            break;
                        }
                    }
                }

                fitSize = (short) g.getFontMetrics(fitFont).stringWidth("<PU> ");
                int w = g.getFontMetrics(headerFont).stringWidth(maxWidth) + LEFT_BORDER + fitSize;

                //w += g.getFontMetrics(typeFont).stringWidth(" char");
                char data[] = new char[Common.typeCharLength + 1];
                w += g.getFontMetrics(typeFont).charsWidth(data, 0, data.length);

                setWidth(w);
            }
        }
    }

    @Override
    public void drawMe(Graphics2D g) {

        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(getPx(), getPy(), getWidth(), getHeight(), 4, 4);

        g.setColor(getColor());
        g.fillRect(getPx(), getPy(), getWidth(), getHeight());

        g.setFont(headerFont);
        g.setColor(headerColor);
        g.drawString(getName(), getPx() + LEFT_BORDER, getPy() + headerSize);

        g.setColor(Color.WHITE);
        g.fillRect(getPx() + 2, getPy() + headerSize + 10, getWidth() - 4, getHeight() - 35);

        for (short i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);

            char[] arr = Field.getIndexInitial(f);

            if (arr != null) {
                g.setFont(fitFont);
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(String.format("<%c%c>", arr[0], arr[1]), getPx() + LEFT_BORDER, getPy() + fieldSize * (i + 2));
            }

            g.setFont(textFont);
            g.setColor(Color.DARK_GRAY);

            if (isCrop() && i == Common.maxRow) {
                g.setFont(textFont.deriveFont(Font.ITALIC));
                g.drawString(String.format("... %d more", fields.size() - Common.maxRow), getPx() + LEFT_BORDER, getPy() + fieldSize * (i + 2));
                break;
            }

            String s = f.getName();
            //String s = String.format("%s %s", fields.get(i).getName(), fields.get(i).getType());
            g.drawString(s, getPx() + LEFT_BORDER + fitSize, getPy() + fieldSize * (i + 2));

            g.setFont(typeFont);
            g.setColor(Color.BLUE);
            g.drawString(f.getShortType(), getAllWidth() - Common.typeCharLength * 10, getPy() + fieldSize * (i + 2));
        }
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

    private boolean isCrop() {
        return state != State.ALLWAYS_VISIBLE && Common.crop;
    }

}
