/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.annotation.TableFieldAnnotation;
import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class Field implements Serializable {

    public static char[] getIndexInitial(Field f) {
        if (f.getAttribute(Field.Attribute.PRIMARY) == null && f.getAttribute(Field.Attribute.INDEX) == null) {
            return null;
        }

        char[] arr = {' ', ' '};

        arr[0] = f.getAttribute(Field.Attribute.PRIMARY) != null ? 'P' : 'I';

        if (f.getAttribute(Field.Attribute.UNIQUE) != null) {
            arr[1] = 'U';
        }

        return arr;
    }

    private boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public enum Attribute {

        PRIMARY, UNIQUE, INDEX
    }

    //public static Map<Short, Field> internalIndex = new HashMap<>(500);
    private String name;
    @TableFieldAnnotation(exportLabel = "MAX-WIDTH", tableLabel = "max width")
    private Integer size;
    private String type;
    private String format;
    private String initial;
    private Integer decimals;
    private String description;

    private String label;
    @TableFieldAnnotation(exportLabel = "COLUMN-LABEL", tableLabel = "column label")
    private String colLabel;
    private String help;
    @TableFieldAnnotation(tableLabel = "val. expression")
    private String valExp;
    @TableFieldAnnotation(tableLabel = "val. message")
    private String valMsg;

    @TableFieldAnnotation(ignore = true)
    private String orgId;

    @TableFieldAnnotation(ignore = true)
    private Attribute[] attributes;

    public Field() {
    }

    public Field(String name, String type) {
        this.name = name;
        this.orgId = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.orgId == null) {
            this.orgId = this.name != null ? this.name : name;
        }

        this.name = name;
    }

    public Integer getSize() {
        return size == null ? 0 : size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return isEmpty(type) ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return isEmpty(format) ? "" : format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public Integer getDecimals() {
        return decimals == null ? 0 : decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public String getDescription() {
        return isEmpty(description) ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return isEmpty(label) ? "" : label;
    }

    public String getDefaultLabel() {
        return isEmpty(label) ? getName() : label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColLabel() {
        return isEmpty(colLabel) ? "" : colLabel;
    }

    public String getDefaultColLabel() {
        return isEmpty(colLabel) ? getName().replaceAll("-", "!") : colLabel;
    }

    public void setColLabel(String colLabel) {
        this.colLabel = colLabel;
    }

    public String getHelp() {
        return isEmpty(help) ? "" : help;
    }

    public String getDefaultHelp() {
        return isEmpty(help) ? getName().replaceAll("-", " ") : help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getValExp() {
        return isEmpty(valExp) ? "" : valExp;
    }

    public void setValExp(String valExp) {
        this.valExp = valExp;
    }

    public String getValMsg() {
        return isEmpty(valMsg) ? "" : valMsg;
    }

    public void setValMsg(String valMsg) {
        this.valMsg = valMsg;
    }

    public String getOrgId() {
        //return orgId != null ? orgId : name;
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @XmlTransient
    public Attribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes = attributes;
    }

    public Attribute getAttribute(Attribute attribute) {
        return attributes != null ? attributes[attribute.ordinal()] : null;
    }

    public void addAttribute(Attribute attribute) {
        if (attributes == null) {
            attributes = new Attribute[Attribute.values().length];
        }

        attributes[attribute.ordinal()] = attribute;

    }

    public String getShortType() {
        if (getType() == null) {
            return "";
        }

        return getType().length() <= Common.typeCharLength ? getType() : getType().substring(0, Common.typeCharLength);
    }

    @Override
    public String toString() {
        return "Field{" + "name=" + name + ", size=" + size + ", type=" + type + ", format=" + format + ", initial=" + initial + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Field other = (Field) obj;
        return Objects.equals(this.name, other.name);
    }

}
