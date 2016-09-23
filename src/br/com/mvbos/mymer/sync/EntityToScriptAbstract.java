/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.sync;

import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.xml.field.Field;
import java.util.Collection;

/**
 *
 * @author Marcus Becker
 */
public abstract class EntityToScriptAbstract implements IEntityToScript {

    protected IEntityToScript.Mode mode;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void addBase(DataBaseElement db, StringBuilder sb) {
    }

    @Override
    public void addTable(TableElement tb, StringBuilder sb) {
    }

    @Override
    public void addIndex(TableElement tb, IndexElement ie, StringBuilder sb) {
    }

    @Override
    public void addRelationship(TableElement tb, RelationshipElement re, StringBuilder sb) {
    }

    @Override
    public void renameBase(DataBaseElement db, String oldName, String newName, StringBuilder sb) {
    }

    @Override
    public void renameTable(TableElement tb, String oldName, String newName, StringBuilder sb) {
    }

    @Override
    public void renameIndex(TableElement tb, IndexElement ie, String oldName, String newName, StringBuilder sb) {
    }

    @Override
    public void renameRelationship(TableElement tb, RelationshipElement re, String newName, StringBuilder sb) {
    }

    @Override
    public void updateBase(DataBaseElement db, StringBuilder sb) {
    }

    @Override
    public void updateTable(TableElement tb, StringBuilder sb) {
    }

    @Override
    public void updateIndex(TableElement tb, IndexElement newIndex, IndexElement oldIndex, StringBuilder sb) {
    }

    @Override
    public void updateRelationship(TableElement tb, RelationshipElement re, StringBuilder sb) {
    }

    @Override
    public void dropBase(DataBaseElement db, StringBuilder sb) {
    }

    @Override
    public void dropTable(TableElement tb, StringBuilder sb) {
    }

    @Override
    public void dropIndex(TableElement tb, IndexElement ie, StringBuilder sb) {
    }

    @Override
    public void dropRelationship(TableElement tb, RelationshipElement re, StringBuilder sb) {
    }

    @Override
    public void addField(TableElement tb, Field field, int index, StringBuilder sb) {
    }

    @Override
    public void renameField(TableElement tb, Field field, String oldName, String newName, StringBuilder sb) {
    }

    @Override
    public void updateField(TableElement tb, Field field, Collection<String> changes, StringBuilder sb) {
    }

    @Override
    public void dropField(TableElement tb, Field field, StringBuilder sb) {
    }

}
