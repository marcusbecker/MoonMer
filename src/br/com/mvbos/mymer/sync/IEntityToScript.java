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
public interface IEntityToScript {

    public enum Mode {

        PLAIN, DECORED
    }

    public void addBase(DataBaseElement db, StringBuilder sb);

    public void addTable(TableElement tb, StringBuilder sb);

    public void addIndex(TableElement tb, IndexElement ie, StringBuilder sb);

    public void addRelationship(TableElement tb, RelationshipElement re, StringBuilder sb);

    public void renameBase(DataBaseElement db, String newName, StringBuilder sb);

    public void renameTable(TableElement tb, String newName, StringBuilder sb);

    public void renameIndex(TableElement tb, IndexElement ie, String newName, StringBuilder sb);

    public void renameRelationship(TableElement tb, RelationshipElement re, String newName, StringBuilder sb);

    public void updateBase(DataBaseElement db, StringBuilder sb);

    public void updateTable(TableElement tb, StringBuilder sb);

    public void updateIndex(TableElement tb, IndexElement ie, StringBuilder sb);

    public void updateRelationship(TableElement tb, RelationshipElement re, StringBuilder sb);

    public void dropBase(DataBaseElement db, StringBuilder sb);

    public void dropTable(TableElement tb, StringBuilder sb);

    public void dropIndex(TableElement tb, IndexElement ie, StringBuilder sb);

    public void dropRelationship(TableElement tb, RelationshipElement re, StringBuilder sb);

    public void addField(TableElement tb, Field field, int index, StringBuilder sb);

    public void renameField(TableElement tb, String oldName, String newName, StringBuilder sb);

    public void updateField(TableElement tb, Field field, Collection<String> changes, StringBuilder sb);

    public void dropField(TableElement tb, Field field, StringBuilder sb);

}
