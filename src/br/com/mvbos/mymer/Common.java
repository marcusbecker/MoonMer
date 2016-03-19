/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.mm.MMProperties;
import java.awt.Color;
import java.awt.Graphics2D;
import java.nio.charset.Charset;

/**
 *
 * @author MarcusS
 */
public class Common {

    public static final int MINI_MAP_UPDATE = 3 * 1000;

    public static short ct; //counter
    public static boolean crop = true;
    public static boolean updateAll = true;
    public static Graphics2D graphics;

    public static String currentPath = MMProperties.get("workdir", ".");

    public static final short maxRow = Short.valueOf(MMProperties.get("maxRow", 15));

    public static byte typeCharLength = Byte.valueOf(MMProperties.get("typeCharLength", 4)); //counter

    public static int camWidth = Integer.valueOf(MMProperties.get("camWidth", 9000));
    public static int camHeight = Integer.valueOf(MMProperties.get("camHeight", 9000));
    
    public static int backgroundColor = Integer.valueOf(MMProperties.get("backgroundColor", Color.WHITE.getRGB()));

    public static final String[] comboTypes = MMProperties.get("comboTypes", "character,date,decimal,integer,logical,rowid,handle").split(",");

    public static final String importURL = MMProperties.get("importUrl", "");
    public static final Charset charset = Charset.forName(MMProperties.get("charset", "UTF-8"));
    public static final Charset importCharset = Charset.forName(MMProperties.get("importCharset", "UTF-8"));
    public static boolean enableFastUpdate;
    public static boolean autoFitCam = Boolean.valueOf(MMProperties.get("autoFitCam", true));

}
