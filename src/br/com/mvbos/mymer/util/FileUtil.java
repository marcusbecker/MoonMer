/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.util;

import br.com.mvbos.mymer.Common;
import br.com.mvbos.mymer.entity.EntityUtil;
import br.com.mvbos.mymer.xml.DataBaseStore;
import br.com.mvbos.mymer.xml.field.DataBase;
import br.com.mvbos.mymer.xml.field.Table;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Marcus Becker
 */
public class FileUtil {

    public static final File CURRENT_PATH = new File(Common.currentPath);
    public static final File LOG_PATH = new File(CURRENT_PATH, "log");
    public static final File IMPORT_DATA = new File(CURRENT_PATH, "import.data");

    public static void store(File dir, DataBaseStore db) {
        try {
            ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(dir));
            o.writeObject(db);
            o.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void store(File dir, final Map<String, Table> map) {
        try {
            ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(dir));
            o.writeObject(map);
            o.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Object load(File file) {
        Object o = null;

        try {
            if (file.exists()) {
                ObjectInputStream i = new ObjectInputStream(new FileInputStream(file));
                o = i.readObject();
                i.close();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return o;
    }

    public static DataBaseStore open() {
        final DataBaseStore dbs = (DataBaseStore) load(IMPORT_DATA);
        return dbs;
    }

    public static Table open(String baseName, String tableName) {
        Table tb = null;
        final DataBaseStore dbs = (DataBaseStore) load(IMPORT_DATA);

        DataBase db = EntityUtil.findBaseByName(dbs, baseName);

        if (db != null) {
            tb = EntityUtil.findTableByName(db, tableName);
        }

        return tb;
    }

    public static Table open(String key) {
        Map<String, Table> map = (Map<String, Table>) load(IMPORT_DATA);

        if (map != null) {
            return map.get(key);
        }

        return null;
    }

    public static void write(String dir, String name, StringBuilder sb) {
        File f = new File(dir, name);
        FileWriter fw;
        try {
            fw = new FileWriter(f);
            fw.write(sb.toString());

            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    public static Logger getLoggerToFile(Class aClass, String path) {
        Logger logger = Logger.getLogger(aClass.getSimpleName());
        FileHandler fh;

        try {
            if (!LOG_PATH.exists()) {
                LOG_PATH.mkdir();
            }

            File f = new File(LOG_PATH, path.concat(".log"));
            fh = new FileHandler(f.getAbsolutePath(), true);
            fh.setFormatter(new SimpleFormatter());
            
            logger.addHandler(fh);

        } catch (SecurityException | IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return logger;
    }

}
