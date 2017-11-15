/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mm;

import javax.swing.JTextField;

/**
 *
 * @author Marcus Becker
 */
public class Util {

    static int getInt(JTextField tf) {
        try {
            return Integer.parseInt(tf.getText());
        } catch (NumberFormatException e) {

        }

        return -1;
    }

    public static boolean isEmpty(String name) {
        return name == null || name.trim().isEmpty();
    }

}
