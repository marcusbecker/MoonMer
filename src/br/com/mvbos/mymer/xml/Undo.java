/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.el.TableElement;
import java.util.LinkedList;

/**
 *
 * @author MarcusS
 */
public class Undo {

    private static final LinkedList<TableElement> hirtory = new LinkedList<>();

    public static void add(TableElement e) {
        hirtory.add(e);
    }

    public static TableElement get() {
        if (hirtory.isEmpty()) {
            return null;
        }

        TableElement last = hirtory.getLast();
        hirtory.removeLast();
        return last;
    }

}
