/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.xml.field.Field;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MarcusS
 */
public class TableElement extends ElementModel {

    //public static final Font headerFont = new Font("Helvetica Neue,Helvetica,Arial,sans-serif", Font.BOLD, 14);
    public static final Font headerFont = new Font("Consolas", Font.BOLD, 14);
    public static final Font textFont = new Font("Arial", Font.PLAIN, 14);
    public static final Font typeFont = new Font("Arial", Font.PLAIN, 12);

    private short headerSize = 10;
    private short fieldSize = 10;
    private DataBaseElement dataBase;
    private List<Field> fields = new ArrayList<>(10);

    private boolean autoWidth = true;
    private boolean autoHeight = true;

    public TableElement(int width, int height, DataBaseElement dataBase, String name) {
        this(0, 0, width, height, dataBase, name);
    }

    public TableElement(float px, float py, int width, int height, DataBaseElement dataBase, String name) {
        super(px, py, width, height, name);
        this.dataBase = dataBase;
        setColor(new Color(74, 189, 218));
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

    @Override
    public void update() {
        if (Common.graphics != null) {

            Graphics2D g = Common.graphics;

            if (autoHeight) {

                headerSize = (short) g.getFontMetrics(headerFont).getHeight();
                fieldSize = (short) (g.getFontMetrics(textFont).getHeight() + 5);

                if (Common.crop) {
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

                    if (Common.crop) {
                        Common.ct++;
                        if (Common.ct == Common.maxRow) {
                            break;
                        }
                    }
                }

                int w = g.getFontMetrics(headerFont).stringWidth(maxWidth) + 10;

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
        g.setColor(Color.GRAY);
        g.drawString(getName(), getPx() + 5, getPy() + headerSize);

        g.setColor(Color.WHITE);
        g.fillRect(getPx() + 2, getPy() + headerSize + 10, getWidth() - 4, getHeight() - 35);

        for (short i = 0; i < fields.size(); i++) {

            g.setFont(textFont);
            g.setColor(Color.DARK_GRAY);

            if (Common.crop && i == Common.maxRow) {
                g.setFont(textFont.deriveFont(Font.ITALIC));
                g.drawString(String.format("... %d more", fields.size() - Common.maxRow), getPx() + 5, getPy() + fieldSize * (i + 2));
                break;
            }

            String s = fields.get(i).getName();
            //String s = String.format("%s %s", fields.get(i).getName(), fields.get(i).getType());
            g.drawString(s, getPx() + 5, getPy() + fieldSize * (i + 2));

            g.setFont(typeFont);
            g.setColor(Color.BLUE);
            g.drawString(fields.get(i).getShortType(), getAllWidth() - Common.typeCharLength * 10, getPy() + fieldSize * (i + 2));
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

}
