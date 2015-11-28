/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.mymer.xml.field.Field;
import java.util.List;

/**
 *
 * @author marcuss
 */
class Find {

    public static Field findByName(List<Field> fields, String name) {
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                return f;
            }
        }

        return null;
    }

}
