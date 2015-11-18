/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.field;

import br.com.mvbos.mymer.el.TableElement;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Marcus Becker
 */
public class TableTreeNode extends DefaultMutableTreeNode {

    public TableTreeNode(TableElement userObject) {
        super(userObject);
    }

    @Override
    public String toString() {
        if (userObject == null) {
            return "";

        }

        return ((TableElement) userObject).getName();
    }

    public TableElement get() {
        return (TableElement) userObject;
    }
}
