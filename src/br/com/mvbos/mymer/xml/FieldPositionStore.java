/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.xml.field.FieldPosition;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class FieldPositionStore {

    private List<FieldPosition> fields;

    public FieldPositionStore() {
    }

    public List<FieldPosition> getFields() {
        return fields;
    }

    public void setFields(List<FieldPosition> fields) {
        this.fields = fields;
    }

}
