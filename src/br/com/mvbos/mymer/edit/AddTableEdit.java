/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.edit;

import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.entity.DataBaseEntity;
import br.com.mvbos.mymer.entity.EntityManager;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Marcus Becker
 */
public class AddTableEdit extends AbstractUndoableEdit {

    private final TableElement t;
    private final EditWindowInterface source;

    public AddTableEdit(final TableElement t) {
        this(t, null);
    }

    public AddTableEdit(final TableElement t, final EditWindowInterface source) {
        this.t = t;
        this.source = source;
    }

    @Override
    public void undo() throws CannotUndoException {
        EntityManager.e().getEntity(DataBaseEntity.class).removeTable(t);
        DataBaseEntity.tableCount--;

        if (source != null) {
            source.changeSelection(null);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        EntityManager.e().getEntity(DataBaseEntity.class).addTable(t);
        DataBaseEntity.tableCount++;

        if (source != null) {
            source.changeSelection(t);
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
