/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.edit;

import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.table.DataChange;
import br.com.mvbos.mymer.xml.field.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author MarcusS
 */
public class ChangeTableFieldEdit extends AbstractUndoableEdit {

    private final TableElement e;
    private final DataChange dataChange;
    private final EditWindowInterface source;

    public ChangeTableFieldEdit(final TableElement e, final DataChange dataChange, final EditWindowInterface source) {
        if (e == null || dataChange == null) {
            throw new IllegalArgumentException();
        }

        this.e = e;
        this.dataChange = dataChange;
        this.source = source;
    }

    @Override
    public void undo() throws CannotUndoException {
        setData(dataChange.getOldValue());
    }

    @Override
    public void redo() throws CannotRedoException {
        setData(dataChange.getNewValue());
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

    private void setData(Object value) {
        Field f = e.getFields().get(dataChange.getRow());

        try {
            java.lang.reflect.Field ff = Field.class.getDeclaredFields()[dataChange.getCol()];
            ff.setAccessible(true);
            ff.set(f, value);

            if (source != null) {
                source.changeSelection(e);
            }

        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ChangeTableFieldEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
