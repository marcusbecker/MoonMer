/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mm;

import br.com.mvbos.mymer.Window;
import br.com.mvbos.mymer.entity.EntityManager;
import java.awt.EventQueue;

/**
 *
 * @author mbecker
 */
public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {

            String lookAndFeelName = MMProperties.get("lookAndFeel", "Nimbus");

            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {

                //System.out.println("info.getName " + info.getName());
                if (lookAndFeelName.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(br.com.mvbos.mymer.Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        EntityManager.e().start();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window().setVisible(true);
            }
        });
    }

}
