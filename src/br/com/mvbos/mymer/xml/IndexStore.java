/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.xml.field.Index;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author marcuss
 */
@XmlRootElement
public class IndexStore {

    private List<Index> indices;

    public List<Index> getIndices() {
        return indices;
    }

    public void setIndices(List<Index> indices) {
        this.indices = indices;
    }

}
