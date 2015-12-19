/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml;

import br.com.mvbos.mymer.xml.field.View;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class ViewStore {

    private List<View> views;

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    public void addView(View v) {
        if (views == null) {
            views = new ArrayList<>(10);
        }

        views.add(v);
    }

    public boolean hasViews() {
        return views != null && !views.isEmpty();
    }

}
