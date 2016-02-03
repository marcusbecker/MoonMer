/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MarcusS
 */
public class WindowSerializable implements Serializable {

    public WindowSerializable() {
        this.cam = new Point();
        this.windowSize = new Dimension(800, 600);
    }

    static void save(WindowSerializable ws) {

        try (ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(FILE_CONFIG))) {
            oo.writeObject(ws);
        } catch (IOException ex) {
            Logger.getLogger(WindowSerializable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final File FILE_CONFIG = new File("moon.mer");

    static WindowSerializable load() {
        WindowSerializable ws = new WindowSerializable();

        if (FILE_CONFIG.exists()) {

            try {
                ObjectInputStream oi = new ObjectInputStream(new FileInputStream(FILE_CONFIG));
                ws = (WindowSerializable) oi.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(WindowSerializable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return ws;
    }

    public Point cam;
    public Dimension windowSize;
    public int windowState;
    public int hDivider;
    public int vDivider;
}
