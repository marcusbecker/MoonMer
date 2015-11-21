/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.tree;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.mymer.el.DataBaseElement;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Marcus Becker
 */
public class DataTreeNode extends DefaultMutableTreeNode {

    public DataTreeNode(DataBaseElement userObject) {
        super(userObject);
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

}
