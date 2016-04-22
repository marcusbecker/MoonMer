/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.xml.field.DataBase;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class DataBaseStore implements Serializable {

    private List<DataBase> bases;

    public List<DataBase> getBases() {
        return bases;
    }

    public void setBases(List<DataBase> bases) {
        this.bases = bases;
    }

    public void addBase(DataBase db) {
        if (bases == null) {
            bases = new ArrayList<>(10);
        }

        bases.add(db);
    }

    public boolean hasBases() {
        return bases != null && !bases.isEmpty();
    }

}
