/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.xml.field.Relationship;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class RelationshipStore {

    private List<Relationship> relations;

    public RelationshipStore() {
    }

    public List<Relationship> getRelations() {
        return relations;
    }

    public void setRelations(List<Relationship> relations) {
        this.relations = relations;
    }

}
