/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el.draw;

import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.el.TableElement;
import static br.com.mvbos.mymer.el.TableElement.fitFont;
import static br.com.mvbos.mymer.el.TableElement.headerFont;
import static br.com.mvbos.mymer.el.TableElement.textFont;
import static br.com.mvbos.mymer.el.TableElement.typeFont;
import br.com.mvbos.mymer.xml.field.Field;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

/**
 *
 * @author MarcusS
 */
public class SimpleDraw {

    protected final TableElement e;

    protected static final int LEFT_BORDER = 5;

    protected short headerSize = 10;
    protected short fieldSize = 10;
    //Field index type size
    protected short fitSize;

    protected Color headerColor = Color.DARK_GRAY;

    public SimpleDraw(TableElement e) {
        this.e = e;
    }

    public TableElement getElement() {
        return e;
    }

    public void update(Graphics2D g) {
        if (g == null) {
            return;
        }

        headerColor = getBetterHeaderColor();

        List<Field> fields = e.getFields();

        if (e.isAutoHeight()) {

            headerSize = (short) g.getFontMetrics(headerFont).getHeight();
            fieldSize = (short) (g.getFontMetrics(textFont).getHeight() + LEFT_BORDER);

            if (e.isCrop()) {
                e.setHeight(fieldSize * (fields.size() > Common.maxRow ? Common.maxRow + 1 : fields.size()) + headerSize + 20);
            } else {
                e.setHeight(fieldSize * fields.size() + headerSize + 20);
            }
        }

        if (e.isAutoWidth()) {
            String maxWidth = "five5"; //getName();

            Common.ct = 0;

            for (Field f : fields) {
                if (f.getName().length() > maxWidth.length()) {
                    maxWidth = f.getName();
                }

                if (e.isCrop()) {
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

            e.setWidth(w);
        }
    }

    public void draw(Graphics2D g) {
        if (g == null) {
            return;
        }

        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(e.getPx(), e.getPy(), e.getWidth(), e.getHeight(), 4, 4);

        g.setColor(e.getColor());
        g.fillRect(e.getPx(), e.getPy(), e.getWidth(), e.getHeight());

        g.setFont(headerFont);
        g.setColor(headerColor);
        g.drawString(e.getName(), e.getPx() + LEFT_BORDER, e.getPy() + headerSize);

        g.setColor(Color.WHITE);
        g.fillRect(e.getPx() + 2, e.getPy() + headerSize + 10, e.getWidth() - 4, e.getHeight() - 35);

        List<Field> fields = e.getFields();

        for (short i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);

            char[] arr = Field.getIndexInitial(f);

            if (arr != null) {
                g.setFont(fitFont);
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(String.format("<%c%c>", arr[0], arr[1]), e.getPx() + LEFT_BORDER, e.getPy() + fieldSize * (i + 2));
            }

            g.setFont(textFont);
            g.setColor(Color.DARK_GRAY);

            if (e.isCrop() && i == Common.maxRow) {
                g.setFont(textFont.deriveFont(Font.ITALIC));
                g.drawString(String.format("... %d more", fields.size() - Common.maxRow), e.getPx() + LEFT_BORDER, e.getPy() + fieldSize * (i + 2));
                break;
            }

            String s = f.getName();
            //String s = String.format("%s %s", fields.get(i).getName(), fields.get(i).getType());
            g.drawString(s, e.getPx() + LEFT_BORDER + fitSize, e.getPy() + fieldSize * (i + 2));

            g.setFont(typeFont);
            g.setColor(Color.BLUE);
            g.drawString(f.getShortType(), e.getAllWidth() - Common.typeCharLength * 10, e.getPy() + fieldSize * (i + 2));
        }
    }

    public Color getBetterHeaderColor() {
        int c = (e.getColor().getRed() + e.getColor().getGreen() + e.getColor().getBlue()) / 3;
        return c < 128 ? Color.LIGHT_GRAY : Color.DARK_GRAY;
    }
}
