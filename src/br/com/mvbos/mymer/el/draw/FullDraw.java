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
public class FullDraw extends SimpleDraw {

    private final DrawTableModel model;

    private final int[] columnLen;

    private final int SP = 5;

    public FullDraw(TableElement e) {
        super(e);
        model = new DrawTableModel();
        columnLen = new int[model.getColumnCount()];
    }

    @Override
    public void update(Graphics2D g) {
        if (g == null) {
            return;
        }

        TableElement e = getElement();

        headerColor = super.getBetterHeaderColor();

        List<Field> fields = e.getFields();
        model.setData(fields);

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

            int sum = 0;

            for (int col = 0; col < model.getColumnCount(); col++) {
                Common.ct = 0;
                String maxWidth = model.getColumnName(col);

                for (int row = 0; row < model.getRowCount(); row++) {
                    String data = model.getValueAt(row, col).toString();
                    if (data.length() > maxWidth.length()) {
                        maxWidth = data;
                    }

                    if (e.isCrop() && ++Common.ct == Common.maxRow) {
                        break;
                    }
                }

                int w = g.getFontMetrics(textFont).stringWidth(maxWidth);
                sum += w + SP;
                columnLen[col] = w;
            }

            /*String maxWidth = "five5"; //getName();

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
             w += g.getFontMetrics(typeFont).charsWidth(data, 0, data.length);*/
            e.setWidth(sum);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (g == null) {
            return;
        }

        TableElement e = getElement();
        List<Field> fields = e.getFields();

        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(e.getPx(), e.getPy(), e.getWidth(), e.getHeight(), 4, 4);

        g.setColor(e.getColor());
        g.fillRect(e.getPx(), e.getPy(), e.getWidth(), e.getHeight());

        g.setFont(headerFont);
        g.setColor(headerColor);
        g.drawString(e.getName(), e.getPx() + LEFT_BORDER, e.getPy() + headerSize);

        g.setColor(Color.WHITE);
        g.fillRect(e.getPx() + 2, e.getPy() + headerSize + 10, e.getWidth() - 4, e.getHeight() - 35);

        g.setFont(textFont);
        g.setColor(Color.DARK_GRAY);

        int old = 0;
        for (int i = 0; i < model.getColumnCount(); i++) {
            String s = model.getColumnName(i);

            g.drawString(s, e.getPx() + old + SP + LEFT_BORDER, e.getPy() + fieldSize);
            old += columnLen[i];
        }

        old = 0;

        for (int col = 0; col < model.getColumnCount(); col++) {

            for (int row = 0; row < model.getRowCount(); row++) {
                String data = model.getValueAt(row, col).toString();

                if (e.isCrop() && row == Common.maxRow) {
                    g.setFont(textFont.deriveFont(Font.ITALIC));
                    g.drawString(String.format("... %d more", fields.size() - Common.maxRow), e.getPx() + LEFT_BORDER, e.getPy() + fieldSize * (row + 2));
                    break;
                }

                g.drawString(data, e.getPx() + old + SP + LEFT_BORDER, e.getPy() + fieldSize * (row + 2));

                //g.setFont(typeFont);
                //g.setColor(Color.BLUE);
                //g.drawString(f.getShortType(), e.getAllWidth() - Common.typeCharLength * 10, e.getPy() + fieldSize * (i + 2));
            }

            old += columnLen[col];
        }

    }
}
