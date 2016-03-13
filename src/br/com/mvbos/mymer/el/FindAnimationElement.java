/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.el;

import br.com.mvbos.jeg.element.ElementModel;
import java.awt.Graphics2D;

/**
 *
 * @author Marcus Becker
 */
public class FindAnimationElement extends ElementModel {

    private static final short DELAY = 15;
    private short size = DELAY;

    public FindAnimationElement() {
        super.setVisible(false);
    }

    @Override
    public void update() {

    }

    @Override
    public void drawMe(Graphics2D g) {
        if (!isVisible()) {
            return;
        }

        size--;

        short s = (short) (5 * size);

        g.setColor(getColor());
        g.drawOval(getPx() - s / 2, getPy() - s / 2, s, s);

        if (size < 0) {
            size = DELAY;
            setVisible(false);
        }

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        size = DELAY;
    }

}
