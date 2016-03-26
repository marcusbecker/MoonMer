/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.edit;

import javax.swing.undo.UndoManager;

/**
 *
 * @author Marcus Becker
 */
public class EditControl {

    private static EditControl e;
    private final UndoManager undoManager;

    public EditControl() {
        undoManager = new UndoManager();
        undoManager.setLimit(25);
    }

    public synchronized static EditControl e() {
        if (e == null) {
            e = new EditControl();
        }

        return e;
    }

    public static UndoManager u() {
        return e().undoManager;
    }
}
