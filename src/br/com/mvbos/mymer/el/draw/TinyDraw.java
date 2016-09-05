/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el.draw;

import br.com.mvbos.mymer.el.TableElement;
import static br.com.mvbos.mymer.el.TableElement.headerFont;
import br.com.mvbos.mymer.xml.field.Field;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
 *
 * @author MarcusS
 */
public class TinyDraw extends SimpleDraw {

    public TinyDraw(TableElement e) {
        super(e);
    }

    @Override
    public void update(Graphics2D g) {
        if (g == null) {
            return;
        }

        TableElement e = getElement();

        headerColor = super.getBetterHeaderColor();
        headerSize = (short) g.getFontMetrics(headerFont).getHeight();
        e.setHeight(headerSize + 20);

        if (e.isAutoWidth()) {
            int w = g.getFontMetrics(headerFont).stringWidth(e.getName()) + LEFT_BORDER * 2;
            e.setWidth(w);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (g == null) {
            return;
        }

        TableElement e = getElement();

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

    }
}
