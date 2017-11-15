/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer.task;

import br.com.mvbos.iup.Iup;
import br.com.mvbos.iup.VersionInfo;
import br.com.mvbos.mm.App;
import br.com.mvbos.mymer.Window;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author MarcusS
 */
public class CheckNewVersionTask implements Runnable {

    private final JFrame window;

    public CheckNewVersionTask(JFrame window) {
        this.window = window;
    }

    @Override
    public void run() {
        final Iup iup = new Iup(String.valueOf(App.VERSION));

        if (iup.hasNewVersion()) {
            VersionInfo info = iup.getNewVerionInfo();
            final String msg = String.format("New version avaliable: %s. Do you want to update now?", info.getVersion());
            int resp = JOptionPane.showConfirmDialog(window, msg);
            if (resp == JOptionPane.OK_OPTION) {

                try {
                    Process proc = Runtime.getRuntime().exec("java -jar ./lib/InternalUpdate.jar", null, new File("."));
                    //InputStream in = proc.getInputStream();
                    //InputStream err = proc.getErrorStream();
                    System.exit(0);

                } catch (IOException ex) {
                    Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
