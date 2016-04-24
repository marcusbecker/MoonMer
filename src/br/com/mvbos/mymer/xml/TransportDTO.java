/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.TableElement;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Marcus Becker
 */
public class TransportDTO {

    private List<TableElement> tables = Collections.EMPTY_LIST;
    private List<IndexElement> indexes = Collections.EMPTY_LIST;
    private List<RelationshipElement> relations = Collections.EMPTY_LIST;

    public List<TableElement> getTables() {
        return tables;
    }

    public void setTables(List<TableElement> tables) {
        this.tables = tables;
    }

    public List<IndexElement> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<IndexElement> indexes) {
        this.indexes = indexes;
    }

    public List<RelationshipElement> getRelations() {
        return relations;
    }

    public void setRelations(List<RelationshipElement> relations) {
        this.relations = relations;
    }

}
