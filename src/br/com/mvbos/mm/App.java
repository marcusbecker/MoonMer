/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mm;

import br.com.mvbos.mymer.Window;
import br.com.mvbos.mymer.entity.EntityManager;
import br.com.mvbos.mymer.entity.EntityUtil;
import br.com.mvbos.mymer.entity.IElementEntity;
import br.com.mvbos.mymer.xml.DataBaseStore;
import java.awt.EventQueue;
import javax.swing.SwingWorker;

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

        LifeCycle.addCycle(new ICycle() {

            @Override
            public void onPreStart() {
            }

            @Override
            public void onAfterLoadBases() {
            }

            @Override
            public void onAfterLoadMainWindow() {
                boolean autoSync = Boolean.parseBoolean(MMProperties.get("autoSync", Boolean.TRUE));
                if (autoSync) {
                    new SwingWorker<DataBaseStore, Object>() {

                        @Override
                        protected DataBaseStore doInBackground() throws Exception {
                            final String path = MMProperties.get("quick_import_file", "");
                            final DataBaseStore db = EntityUtil.validadeAndLoadFile(path, false, false);

                            return db;
                        }

                    }.execute();
                }
            }

            @Override
            public String getCycleName() {
                return "autoSync";
            }

            @Override
            public void recieveResult(boolean sucess, Exception e) {
            }
        });

        LifeCycle.preStart();

        final LoadWindow l = new LoadWindow();
        final EntityManager em = EntityManager.e();

        l.setLocationRelativeTo(null);
        l.setVisible(true);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                new Thread() {

                    @Override
                    public void run() {
                        while (em.hasNext()) {
                            IElementEntity iee = em.next();
                            em.start(iee);

                            float total = (float) em.getIndex() / (em.getElementsCount() + 1) * 100;
                            l.getProgressBar().setValue(Math.round(total));
                        }

                        LifeCycle.afterLoadBases();

                        final Window w = new Window();
                        l.getProgressBar().setValue(100);
                        w.setVisible(true);
                        l.dispose();

                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                LifeCycle.afterLoadMainWindow();
                            }
                        });
                    }
                }.start();
            }
        });
    }

}
