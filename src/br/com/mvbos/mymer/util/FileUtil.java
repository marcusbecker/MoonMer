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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcus Becker
 */
public class FileUtil {

    public static final File CURRENT_PATH = new File(Common.currentPath);
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

    private static DataBaseStore load(File file) {
        DataBaseStore d = null;
        try {
            if (file.exists()) {
                ObjectInputStream i = new ObjectInputStream(new FileInputStream(file));
                d = (DataBaseStore) i.readObject();
                i.close();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return d;
    }

    public static Table open(String baseName, String tableName) {
        DataBaseStore dbs = load(IMPORT_DATA);

        DataBase db = EntityUtil.findBaseByName(dbs, baseName);

        if (db != null) {
            return EntityUtil.findTableByName(db, tableName);
        }

        return null;
    }

}
