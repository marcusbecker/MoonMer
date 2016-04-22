/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.edit;

import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.field.Field;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author MarcusS
 */
public class AddRemoveTableFieldEdit extends AbstractUndoableEdit {

    private final TableElement table;
    private final int index;
    private final Field field;
    private final boolean add;
    private final EditWindowInterface source;

    public AddRemoveTableFieldEdit(TableElement table, Field field, boolean add) {
        this(table, field, -1, add, null);
    }

    public AddRemoveTableFieldEdit(TableElement table, Field field, boolean add, EditWindowInterface source) {
        this(table, field, -1, add, source);
    }

    public AddRemoveTableFieldEdit(TableElement table, Field field, int index, boolean add, EditWindowInterface source) {
        this.table = table;
        this.field = field;
        this.index = index;
        this.add = add;
        this.source = source;
    }

    @Override
    public void undo() throws CannotUndoException {
        if (add) {
            table.getFields().remove(field);
        } else {
            if (index == -1) {
                table.getFields().add(field);
            } else {
                table.getFields().add(index, field);
            }
        }

        table.update();

        if (source != null) {
            source.changeSelection(table);
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        if (add) {
            table.getFields().add(field);
        } else {
            table.getFields().remove(field);
        }

        table.update();

        if (source != null) {
            source.changeSelection(table);
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
