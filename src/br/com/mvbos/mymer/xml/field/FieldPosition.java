/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.xml.field;

import java.util.Objects;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author MarcusS
 */
@XmlRootElement
public class FieldPosition {

    private int px;
    private int py;
    private int colorName;
    private String fullName;

    public FieldPosition() {
    }

    public FieldPosition(int px, int py, String fullName) {
        this.px = px;
        this.py = py;
        this.fullName = fullName;
    }

    public int getPx() {
        return px;
    }

    public void setPx(int px) {
        this.px = px;
    }

    public int getPy() {
        return py;
    }

    public void setPy(int py) {
        this.py = py;
    }

    public int getColorName() {
        return colorName;
    }

    public void setColorName(int colorName) {
        this.colorName = colorName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final FieldPosition other = (FieldPosition) obj;
        if (!Objects.equals(this.fullName, other.fullName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FieldPosition{" + "px=" + px + ", py=" + py + ", fullName=" + fullName + '}';
    }

}
