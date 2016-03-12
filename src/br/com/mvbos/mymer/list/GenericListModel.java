/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.list;

import br.com.mvbos.mymer.combo.Option;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author MarcusS
 */
public class GenericListModel extends AbstractListModel<Option> {

    private final List<Option> list = new ArrayList<>(60);

    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Option getElementAt(int index) {
        return list.get(index);
    }

    public List<Option> getList() {
        return list;
    }

    public void add(Option option) {
        list.add(option);
        fireContentsChanged(this, 0, list.size());
    }
    
    public void remove(Option option) {
        int idx = list.indexOf(option);
        if(idx == -1)
            return;
        
        list.remove(idx);
        fireIntervalRemoved(option, idx, idx);
    }

}
