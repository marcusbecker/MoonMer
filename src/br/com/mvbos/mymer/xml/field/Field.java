/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import br.com.mvbos.mymer.Common;
import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class Field {

    private String name;
    private Integer size;
    private String type;
    private String format;
    private String defualt;

    public Field() {
    }

    public Field(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDefualt() {
        return defualt;
    }

    public void setDefualt(String defualt) {
        this.defualt = defualt;
    }

    public String getShortType() {
        if (getType() == null) {
            return "";
        }

        return getType().length() <= Common.typeCharLength ? getType() : getType().substring(0, Common.typeCharLength);
    }

    @Override
    public String toString() {
        return "Field{" + "name=" + name + ", size=" + size + ", type=" + type + ", format=" + format + ", defualt=" + defualt + '}';
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

}
