/*
 * To change this license header, choose License Headers in Project MMProperties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mm;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcuss
 */
public class MMProperties {

    public static final Properties prop = new Properties();

    static {
        try {
            File dir = new File("./config");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File f = new File(dir, "config.properties");

            if (!f.exists()) {
                f.createNewFile();
            }

            prop.load(new FileInputStream(f));

        } catch (Exception ex) {
            Logger.getLogger(MMProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String get(String key, Object def) {
        return prop.getProperty(key, String.valueOf(def));
    }

}
