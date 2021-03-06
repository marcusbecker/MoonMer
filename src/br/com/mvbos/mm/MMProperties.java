/*
 * To change this license header, choose License Headers in Project MMProperties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mm;

import br.com.mvbos.mymer.Common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcuss
 */
public class MMProperties {

    public static final Properties prop = new Properties();
    private static final File dir = new File("./config");
    private static final File configFile = new File(dir, "config.properties");

    static {
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }

            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            prop.load(new FileInputStream(configFile));

        } catch (Exception ex) {
            Logger.getLogger(MMProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String get(String key, Object def) {
        String val = prop.getProperty(key, String.valueOf(def));
        prop.setProperty(key, val); /* assert all propertyes will be saved.*/

        return val;
    }

    public static boolean set(String key, String val) {
        boolean first = prop.contains(key);

        prop.setProperty(key, val);
        save();
        return first;
    }

    public static void save() {
        prop.setProperty("camWidth", String.valueOf(Common.camWidth));
        prop.setProperty("camHeight", String.valueOf(Common.camHeight));

        prop.setProperty("backgroundColor", String.valueOf(Common.backgroundColor));

        try {
            prop.store(new FileOutputStream(configFile), null);
        } catch (IOException ex) {
            Logger.getLogger(MMProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
