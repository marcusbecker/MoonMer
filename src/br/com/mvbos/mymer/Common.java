/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import java.awt.Graphics2D;

/**
 *
 * @author MarcusS
 */
public class Common {

    public static byte typeCharLength = 4; //counter

    public static short ct; //counter
    public static short maxRow = 15;
    public static boolean crop = true;
    public static boolean updateAll = true;
    public static Graphics2D graphics;

    public static String[] comboTypes = {"character", "date", "decimal", "integer", "logical", "rowid", "handle"};

}
