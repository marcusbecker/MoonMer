/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.DataConfig;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class ConfigStore {

    private List<DataConfig> bases;

    public List<DataConfig> getBases() {
        return bases;
    }

    public void setBases(List<DataConfig> bases) {
        this.bases = bases;
    }

}
