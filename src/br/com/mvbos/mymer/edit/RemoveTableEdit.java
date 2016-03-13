/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.edit;

import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.entity.DataBaseEntity;
import br.com.mvbos.mymer.entity.EntityManager;
import br.com.mvbos.mymer.entity.RelationEntity;
import java.util.Set;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author Marcus Becker
 */
public class RemoveTableEdit extends AbstractUndoableEdit {

    private final TableElement t;

    public RemoveTableEdit(final TableElement t) {
        this.t = t;
    }

    @Override
    public void undo() throws CannotUndoException {
        EntityManager.e().getEntity(DataBaseEntity.class).addTable(t);
        DataBaseEntity.tableCount++;

        for (RelationshipElement r : EntityManager.e().getEntity(RelationEntity.class).getList()) {
            if (r.isPart(t)) {
                r.setVisible(true);
            }
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        EntityManager.e().getEntity(DataBaseEntity.class).removeTable(t);
        DataBaseEntity.tableCount--;

        for (RelationshipElement r : EntityManager.e().getEntity(RelationEntity.class).getList()) {
            if (r.isPart(t)) {
                r.setVisible(false);
            }
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
