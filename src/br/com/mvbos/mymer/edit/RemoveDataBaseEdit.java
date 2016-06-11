/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.edit;

import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.entity.DataBaseEntity;
import br.com.mvbos.mymer.entity.EntityManager;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Marcus Becker
 */
public class RemoveDataBaseEdit extends AbstractUndoableEdit {

    private final DataBaseElement d;
    private final EditWindowInterface source;

    public RemoveDataBaseEdit(final DataBaseElement d) {
        this(d, null);
    }

    public RemoveDataBaseEdit(final DataBaseElement d, final EditWindowInterface source) {
        if (d == null) {
            throw new IllegalArgumentException();
        }

        this.d = d;
        this.source = source;
    }

    @Override
    public void undo() throws CannotUndoException {
        EntityManager.e().getEntity(DataBaseEntity.class).add(d);

        if (source != null) {
            source.changeSelection(d);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        EntityManager.e().getEntity(DataBaseEntity.class).remove(d);

        if (source != null) {
            source.changeSelection(null);
        }
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return this.getClass().getSimpleName();
    }

}
