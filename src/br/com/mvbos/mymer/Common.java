/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.mm.MMProperties;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author MarcusS
 */
public class Common {

    public static final int MINI_MAP_UPDATE = 3 * 1000;
    public static byte typeCharLength = Byte.valueOf(MMProperties.get("typeCharLength", 4)); //counter

    public static short ct; //counter
    public static short maxRow = Short.valueOf(MMProperties.get("maxRow", 15));
    public static boolean crop = true;
    public static boolean updateAll = true;
    public static Graphics2D graphics;

    public static int camSize = Integer.valueOf(MMProperties.get("camSize", 9000));
    public static int backgroundColor = Integer.valueOf(MMProperties.get("backgroundColor", Color.WHITE.getRGB()));

    public static String[] comboTypes = MMProperties.get("comboTypes", "character,date,decimal,integer,logical,rowid,handle").split(",");

}
