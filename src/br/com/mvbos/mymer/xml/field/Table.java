/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import br.com.mvbos.mymer.el.TableElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class Table implements Serializable{

    private String name;
    private String description;
    private List<Index> indices;
    private List<Field> fields;

    public Table() {
    }

    public Table(String name) {
        this.name = name;
    }

    public Table(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    public Table(TableElement tb) {
        this.name = tb.getName();
        this.description = tb.getDescription();
        this.fields = tb.getFields();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Index> getIndices() {
        return indices;
    }

    public void setIndices(List<Index> indices) {
        this.indices = indices;
    }

    public void addField(Field f) {
        if (fields == null) {
            fields = new ArrayList<>(10);
        }

        fields.add(f);
    }

}
