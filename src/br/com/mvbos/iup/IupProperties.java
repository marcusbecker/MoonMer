/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.iup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MarcusS
 */
public class IupProperties {

    public static final Properties prop = new Properties();
    private static final File configFile = new File(".", "config.iup");

    static {
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            prop.load(new FileInputStream(configFile));

        } catch (Exception ex) {
            Logger.getLogger(IupProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String get(String key, Object def) {
        String val = prop.getProperty(key, String.valueOf(def));
        return val;
    }
    
    public static void set(String key, String value) {
        prop.setProperty(key, value);
    }

    public static void save() {
        try {
            prop.store(new FileOutputStream(configFile), null);
        } catch (IOException ex) {
            Logger.getLogger(IupProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
