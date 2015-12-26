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
    private static final File f = new File(dir, "config.properties");

    static {
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }

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

    public static void save() {
        prop.setProperty("camSize", String.valueOf(Common.camSize));
        prop.setProperty("backgroundColor", String.valueOf(Common.backgroundColor));

        try {
            prop.store(new FileOutputStream(f), null);
        } catch (IOException ex) {
            Logger.getLogger(MMProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
