/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.tree;

import br.com.mvbos.mymer.el.DataBaseElement;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Marcus Becker
 */
public class DataTreeNode extends DefaultMutableTreeNode {

    private final ImageIcon icon;

    public DataTreeNode(DataBaseElement userObject) {
        super(userObject);

        if (userObject == null) {
            //throw new IllegalArgumentException(null)
            icon = new ImageIcon();
        } else {
            final BufferedImage bf = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D gg = bf.createGraphics();

            gg.setColor(userObject.getColor());
            gg.fillRect(0, 0, bf.getWidth(), 2);
            gg.fillRect(0, 6, bf.getWidth(), 3);
            gg.fillRect(0, 12, bf.getWidth(), 6);
            icon = new ImageIcon(bf);
            gg.dispose();
        }
    }

    @Override
    public String toString() {
        if (userObject == null) {
            return "";
        }

        DataBaseElement obj = (DataBaseElement) userObject;
        //return String.format("%s (%d)", obj.getName(), obj.getTables().size());
        return String.format("%s (%d)", obj.getName(), getChildCount());
    }

    public DataBaseElement get() {
        return (DataBaseElement) userObject;
    }

    public Color getColor() {
        return ((DataBaseElement) userObject).getColor();
    }

    public Icon getIcon() {
        return icon;
    }

}
