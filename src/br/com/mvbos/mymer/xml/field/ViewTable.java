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
 * @author marcuss
 */
@XmlRootElement
public class ViewTable {

    private int px;
    private int py;

    private String tableName;
    private String dataBaseName;

    public ViewTable() {
    }

    public ViewTable(String dataBaseName, String tableName) {
        this.tableName = tableName;
        this.dataBaseName = dataBaseName;
    }

    public ViewTable(int px, int py, String dataBaseName, String tableName) {
        this.px = px;
        this.py = py;
        this.tableName = tableName;
        this.dataBaseName = dataBaseName;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.tableName);
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
        final ViewTable other = (ViewTable) obj;
        if (!Objects.equals(this.tableName, other.tableName)) {
            return false;
        }
        return Objects.equals(this.dataBaseName, other.dataBaseName);
    }

    @Override
    public String toString() {
        return "View{" + "px=" + px + ", py=" + py + ", tableName=" + tableName + ", dataBaseName=" + dataBaseName + '}';
    }

}
