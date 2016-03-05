/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.mymer.entity.EntityUtil;
import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.jeg.element.SelectorElement;
import br.com.mvbos.jeg.engine.GraphicTool;
import br.com.mvbos.jeg.window.Camera;
import br.com.mvbos.mymer.combo.Option;
import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.StageElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.entity.DataBaseEntity;
import br.com.mvbos.mymer.entity.EntityManager;
import br.com.mvbos.mymer.entity.RelationEntity;
import br.com.mvbos.mymer.entity.ViewEntity;
import br.com.mvbos.mymer.list.GenericListModel;
import br.com.mvbos.mymer.xml.field.View;
import br.com.mvbos.mymer.xml.field.ViewTable;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author MarcusS
 */
public class ViewWindow extends javax.swing.JFrame {

    private JPanel canvas;
    private final Timer timer;

    private final int camSize = 1500;

    private boolean isAltDown;
    private boolean isControlDown;

    private final List<TableElement> tables = new ArrayList<>(30);
    private final List<RelationshipElement> relations = new ArrayList<>(30);

    private final ElementModel stageEl = new StageElement();

    private EditTool mode = EditTool.SELECTOR;

    private final Camera cam = Camera.createNew();
    private List<View> views;
    private View selected;
    private boolean AUTO_SAVE = true;

    private final EntityManager em = EntityManager.e();
    final DataBaseEntity entity = em.getEntity(DataBaseEntity.class);

    private class MyDispatcher implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {

            isAltDown = e.isAltDown();
            isControlDown = e.isControlDown();

            if (getFocusOwner() == null) {
                return false;
            }

            if (e.getID() == KeyEvent.KEY_PRESSED) {

                if (KeyEvent.VK_PAGE_DOWN == e.getKeyCode() || KeyEvent.VK_PAGE_UP == e.getKeyCode()) {

                    if (isControlDown) {
                        cam.rollX(KeyEvent.VK_PAGE_DOWN == e.getKeyCode() ? 100 : -100);
                    } else {
                        cam.rollY(KeyEvent.VK_PAGE_DOWN == e.getKeyCode() ? 100 : -100);
                    }

                    e.consume();

                } else if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
                    mode = EditTool.SELECTOR;
                }

            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    removeSelTables();
                }

            } else if (e.getID() == KeyEvent.KEY_TYPED) {
            }

            if (isAltDown) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }

            return false;
        }

    }

    /**
     * Creates new form Window
     */
    public ViewWindow() {

        initComponents();

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        cam.config(camSize, camSize, canvas.getWidth(), canvas.getHeight());
        cam.setAllowOffset(true);
        stageEl.setSize(camSize, camSize);

        timer = new Timer(60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.repaint();
            }
        });

        timer.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dlgAddTable = new javax.swing.JDialog();
        tfTableFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstTables = new javax.swing.JList();
        btnAddTable = new javax.swing.JButton();
        split = new javax.swing.JSplitPane();
        pnTop = new javax.swing.JPanel();
        pnCanvas = createCanvas();
        pnBottom = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabStruct = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tfStruct = new javax.swing.JTextArea();
        btnBuildSctruct = new javax.swing.JButton();
        btnAddRelOneOne = new javax.swing.JButton();
        btnAddRelOneMany = new javax.swing.JButton();
        btnShowDlgTable = new javax.swing.JButton();

        dlgAddTable.setTitle("Search and add tables");

        tfTableFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfTableFilterKeyReleased(evt);
            }
        });

        jScrollPane2.setViewportView(lstTables);

        btnAddTable.setText("OK");
        btnAddTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dlgAddTableLayout = new javax.swing.GroupLayout(dlgAddTable.getContentPane());
        dlgAddTable.getContentPane().setLayout(dlgAddTableLayout);
        dlgAddTableLayout.setHorizontalGroup(
            dlgAddTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfTableFilter)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dlgAddTableLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddTable)
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        dlgAddTableLayout.setVerticalGroup(
            dlgAddTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dlgAddTableLayout.createSequentialGroup()
                .addComponent(tfTableFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddTable)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MoonMer - View");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });

        split.setDividerLocation(300);
        split.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        split.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                splitPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout pnCanvasLayout = new javax.swing.GroupLayout(pnCanvas);
        pnCanvas.setLayout(pnCanvasLayout);
        pnCanvasLayout.setHorizontalGroup(
            pnCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 923, Short.MAX_VALUE)
        );
        pnCanvasLayout.setVerticalGroup(
            pnCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 297, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnTopLayout = new javax.swing.GroupLayout(pnTop);
        pnTop.setLayout(pnTopLayout);
        pnTopLayout.setHorizontalGroup(
            pnTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTopLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnTopLayout.setVerticalGroup(
            pnTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTopLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(pnCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        split.setTopComponent(pnTop);

        tfStruct.setColumns(20);
        tfStruct.setRows(5);
        jScrollPane1.setViewportView(tfStruct);

        btnBuildSctruct.setText("Build");
        btnBuildSctruct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildSctructActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabStructLayout = new javax.swing.GroupLayout(tabStruct);
        tabStruct.setLayout(tabStructLayout);
        tabStructLayout.setHorizontalGroup(
            tabStructLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 918, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabStructLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBuildSctruct)
                .addContainerGap())
        );
        tabStructLayout.setVerticalGroup(
            tabStructLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabStructLayout.createSequentialGroup()
                .addComponent(btnBuildSctruct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Struct", tabStruct);

        btnAddRelOneOne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/one_to_one.png"))); // NOI18N
        btnAddRelOneOne.setToolTipText("Add One to One relationship");
        btnAddRelOneOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRelOneOneActionPerformed(evt);
            }
        });

        btnAddRelOneMany.setIcon(new javax.swing.ImageIcon(getClass().getResource("/one_to_many.png"))); // NOI18N
        btnAddRelOneMany.setToolTipText("Add One to Many relationship");
        btnAddRelOneMany.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRelOneManyActionPerformed(evt);
            }
        });

        btnShowDlgTable.setText("+");
        btnShowDlgTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowDlgTableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnBottomLayout = new javax.swing.GroupLayout(pnBottom);
        pnBottom.setLayout(pnBottomLayout);
        pnBottomLayout.setHorizontalGroup(
            pnBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(pnBottomLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnShowDlgTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddRelOneOne)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddRelOneMany)))
                .addContainerGap())
        );
        pnBottomLayout.setVerticalGroup(
            pnBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnBottomLayout.createSequentialGroup()
                .addGroup(pnBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAddRelOneOne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddRelOneMany, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnShowDlgTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        split.setRightComponent(pnBottom);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(split)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(split)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        timer.stop();

        //int r = JOptionPane.showConfirmDialog(this, "Save", "Save", JOptionPane.YES_NO_OPTION);
        //if (r == JOptionPane.OK_OPTION){}
        if (AUTO_SAVE) {
            for (TableElement t : tables) {
                for (ViewTable vt : selected.getTables()) {
                    if (EntityUtil.compare(t, vt)) {
                        vt.setPx(t.getPx());
                        vt.setPy(t.getPy());
                    }
                }
            }

            em.getEntity(ViewEntity.class).save(null);
        }

    }//GEN-LAST:event_formWindowClosing

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged

        cam.config(cam.getSceneWidth(), cam.getSceneHeight(), canvas.getWidth(), canvas.getHeight());

    }//GEN-LAST:event_formWindowStateChanged

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

        cam.config(cam.getSceneWidth(), cam.getSceneHeight(), canvas.getWidth(), canvas.getHeight());

    }//GEN-LAST:event_formComponentResized

    private void splitPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_splitPropertyChange

        cam.config(cam.getSceneWidth(), cam.getSceneHeight(), canvas.getWidth(), canvas.getHeight());

    }//GEN-LAST:event_splitPropertyChange

    private final StringBuilder clip = new StringBuilder(100);


    private void btnBuildSctructActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildSctructActionPerformed


    }//GEN-LAST:event_btnBuildSctructActionPerformed

    private void btnAddRelOneOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRelOneOneActionPerformed
        mode = EditTool.RELATION;
        relType = RelationshipElement.Type.ONE_TO_ONE;
    }//GEN-LAST:event_btnAddRelOneOneActionPerformed

    private void btnAddRelOneManyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRelOneManyActionPerformed
        mode = EditTool.RELATION;
        relType = RelationshipElement.Type.ONE_TO_MORE;
    }//GEN-LAST:event_btnAddRelOneManyActionPerformed

    private void tfTableFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfTableFilterKeyReleased

        String filter = tfTableFilter.getText();

        myListModel.getList().clear();

        for (DataBaseElement d : entity.getList()) {

            for (TableElement t : d.getTables()) {
                if (t.getName().toLowerCase().contains(filter.toLowerCase())) {
                    myListModel.add(new Option((short) t.getId(), t, t.getName()));
                }
            }
        }

        lstTables.setModel(myListModel);

    }//GEN-LAST:event_tfTableFilterKeyReleased

    private void btnAddTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTableActionPerformed

        for (Object obj : lstTables.getSelectedValuesList()) {
            Option o = (Option) obj;

            TableElement copy = EntityUtil.copy((TableElement) o.getValue());
            ViewTable v = new ViewTable(copy.getDataBase().getName(), copy.getName());

            addTable(copy);
            addViewTable(v);
        }

        dlgAddTable.setVisible(false);

    }//GEN-LAST:event_btnAddTableActionPerformed

    private void btnShowDlgTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowDlgTableActionPerformed
        dlgAddTable.pack();
        dlgAddTable.setLocationRelativeTo(this);
        dlgAddTable.setVisible(true);

    }//GEN-LAST:event_btnShowDlgTableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRelOneMany;
    private javax.swing.JButton btnAddRelOneOne;
    private javax.swing.JButton btnAddTable;
    private javax.swing.JButton btnBuildSctruct;
    private javax.swing.JButton btnShowDlgTable;
    private javax.swing.JDialog dlgAddTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList lstTables;
    private javax.swing.JPanel pnBottom;
    private javax.swing.JPanel pnCanvas;
    private javax.swing.JPanel pnTop;
    private javax.swing.JSplitPane split;
    private javax.swing.JPanel tabStruct;
    private javax.swing.JTextArea tfStruct;
    private javax.swing.JTextField tfTableFilter;
    // End of variables declaration//GEN-END:variables

    private enum EditTool {

        SELECTOR, HAND, RELATION;
    }

    private final GenericListModel myListModel = new GenericListModel();

    //private static final Color BACKGROUND_COLOR = new Color(153, 153, 153);
    private Point mousePos = new Point();
    private Point startDrag;

    //private ElementModel selectedElement;
    private final ElementModel[] selectedElements = new ElementModel[30];

    private final SelectorElement selector = new SelectorElement("selector");

    private final ElementModel mouseElement = new ElementModel(10, 10, "mouseElement");

    private TableElement relLeft;
    private TableElement relRight;
    private RelationshipElement.Type relType;

    private JPanel createCanvas() {
        canvas = new JPanel() {
            private final Color BACKGROUND_COLOR = new Color(235, 235, 235);

            @Override
            protected void paintComponent(Graphics gg) {
                super.paintComponent(gg);

                Graphics2D g = (Graphics2D) gg;
                Common.graphics = g;

                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                //stageEl.setColor(btnCanvasColor.getBackground());
                stageEl.setColor(Color.WHITE);
                cam.draw(g, stageEl);

                int sp = 5;
                g.setColor(Color.BLACK);

                for (ElementModel el : selectedElements) {
                    if (el == null) {
                        break;
                    }

                    g.drawRect(cam.fx(el.getPx() - sp), cam.fy(el.getPy() - sp), el.getWidth() + sp * 2, el.getHeight() + sp * 2);
                }

                if (startDrag != null) {
                    int npx = mousePos.x - startDrag.x;
                    int npy = mousePos.y - startDrag.y;
                    for (ElementModel el : selectedElements) {
                        if (el == null) {
                            break;
                        }

                        g.drawRect(cam.fx(el.getPx() + npx), cam.fy(el.getPy() + npy), el.getWidth(), el.getHeight());
                    }
                }

                if (Common.updateAll) {
                    for (ElementModel el : tables) {
                        el.update();
                    }

                    Common.updateAll = false;

                } else {

                    for (RelationshipElement rl : relations) {
                        rl.drawMe(g);
                    }

                    for (ElementModel el : tables) {
                        cam.draw(g, el);
                    }

                    selector.drawMe(g);
                    drawRelationPointer(g);
                }

            }

            private void drawRelationPointer(Graphics2D g) {
                if (mode != EditTool.RELATION || relLeft == null) {
                    return;
                }

                g.setColor(Color.DARK_GRAY);
                g.drawLine(cam.fx(relLeft.getPx() + relLeft.getHalfWidth()), cam.fy(relLeft.getPy() + relLeft.getHalfHeight()), mousePos.x, mousePos.y);
            }
        };

        canvas.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {

                    mouseElement.setPxy(e.getX() + cam.getCpx(), e.getY() + cam.getCpy());

                    switch (mode) {
                        case SELECTOR:
                            selectElementOnStage(hasColision(mouseElement));
                            return;
                        case RELATION:
                            addRelationship(hasColision(mouseElement));
                            return;
                        case HAND:
                            return;
                    }

                } else {
                    selector.setEnabled(false);
                    singleSelection(null);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {

                    mouseElement.setPxy(e.getX() + cam.getCpx(), e.getY() + cam.getCpy());

                    if (!isAltDown) {

                        if (selectedElements[0] == null || !isValidSelecion(mouseElement)) {
                            selector.setEnabled(true);
                            selector.setPxy(e.getX(), e.getY());
                        } else {
                            startDrag = e.getPoint();
                        }

                    } else /*if (EditTool.HAND == EditTool.SELECTOR)*/ {
                        //cam.move(e.getX() - mousePos.x, getY() - mousePos.y);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (selector.isEnabled()) {
                        selector.adjustInvertSelection();
                        selector.setPx(selector.getPx() + cam.getCpx());
                        selector.setPy(selector.getPy() + cam.getCpy());

                        singleSelection(null);

                        for (ElementModel el : tables) {

                            if (GraphicTool.g().bcollide(el, selector)) {
                                multiSelect(el);
                            }
                        }

                    } else if (startDrag != null) {
                        int npx = e.getPoint().x - startDrag.x;
                        int npy = e.getPoint().y - startDrag.y;
                        for (ElementModel el : selectedElements) {
                            if (el == null) {
                                break;
                            }
                            el.incPx(npx);
                            el.incPy(npy);
                        }
                    }

                    selector.setEnabled(false);
                    startDrag = null;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
                mousePos.x = -1;
            }

        });

        canvas.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                //p = e.getPoint();

                if (selector.isVisible()) {
                    selector.setWidth(e.getX());
                    selector.setHeight(e.getY());
                }

                if (isAltDown) {
                    positionCam(e);
                }

                updateMousePosition(e);

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateMousePosition(e);
            }

            private void updateMousePosition(MouseEvent e) {
                mousePos = e.getPoint();
                //lblCanvasInfo.setText(String.format("%d %d Cam: %.0f %.0f ", mousePos.x, mousePos.y, mousePos.x + cam.getCpx(), mousePos.y + cam.getCpy()));
            }
        });

        canvas.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (isControlDown) {
                    cam.rollX(e.getWheelRotation() * 5);
                } else {
                    cam.rollY(e.getWheelRotation() * 5);
                }

            }
        });

        return canvas;
    }

    private void singleSelection(ElementModel el) {
        for (int i = 1; i < selectedElements.length; i++) {
            selectedElements[i] = null;
        }

        selectedElements[0] = el;

        if (el == null) {

        } else if (el instanceof TableElement) {
            //System.out.println("list " + list);

            //TableElement e = (TableElement) el;
        }

    }

    private void addRelationship(ElementModel el) {
        if (el == null) {
            return;
        }

        if (relLeft == null) {
            relLeft = (TableElement) el;
        } else if (relRight == null) {
            relRight = (TableElement) el;
        }

        if (relLeft != null && relRight != null) {
            final RelationEntity rEnt = em.getEntity(RelationEntity.class);
            rEnt.addNewRelationship(relType, relLeft, relRight);

            relType = null;
            relRight = relLeft = null;
            mode = EditTool.SELECTOR;
        }
    }

    private void multiSelect(ElementModel el) {
        for (int i = 0; i < selectedElements.length; i++) {
            if (selectedElements[i] != null) {
                continue;
            }
            selectedElements[i] = el;
            break;
        }
    }

    private void updateSelectedProperties(ElementModel el) {
    }

    private boolean isValidSelecion(ElementModel element) {
        for (ElementModel el : selectedElements) {
            if (el == null) {
                return false;
            }

            if (GraphicTool.g().bcollide(el, element)) {
                return true;
            }
        }

        return false;
    }

    private void selectElementOnStage(ElementModel elementModel) {
        singleSelection(elementModel);
    }

    private ElementModel hasColision(ElementModel element) {
        ElementModel e = selectedElements[0];

        for (ElementModel el : tables) {
            if (EditTool.SELECTOR == mode && selectedElements[0] == el) {
                continue;
            }

            if (GraphicTool.g().bcollide(el, element)) {
                e = el;
                break;
            }
        }

        return e;
    }

    private void positionCam(ElementModel el) {
        cam.move(el.getPx() - canvas.getWidth() / 2, el.getPy() - canvas.getHeight() / 2);
    }

    private void positionCam(int px, int py) {
        cam.move(px - 15, py - 15);
    }

    private void positionCam(MouseEvent e) {
        cam.rollX(mousePos.x - e.getX());
        cam.rollY(mousePos.y - e.getY());
    }

    public void addTable(TableElement tb) {

        if (tables.contains(tb)) {
            return;
        }

        tables.add(tb);
    }

    public void addViewTable(ViewTable v) {
        selected.getTables().add(v);
    }

    private List<TableElement> getTables() {
        return tables;
    }

    public List<RelationshipElement> getRelations() {
        return relations;
    }

    private void addRelations(Set<RelationshipElement> relationship) {
        relations.clear();
        for (RelationshipElement r : relationship) {
            TableElement parent = tables.get(tables.indexOf(r.getParent()));
            TableElement child = tables.get(tables.indexOf(r.getChild()));

            RelationshipElement re = new RelationshipElement(r.getType(), parent, child);
            re.setCam(cam);

            relations.add(re);
        }
    }

    public void init(View selected, List<View> views) {
        this.selected = selected;
        this.views = views;

        RelationEntity rEntity = em.getEntity(RelationEntity.class);
        addRelations(rEntity.findRelationship(getTables()));
    }

    private void removeSelTables() {
        if (selectedElements[0] != null) {
            if (JOptionPane.showConfirmDialog(this, "Remove table " + selectedElements[0].getName() + " ?", "Do you want to remove the selected table?", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {

                TableElement t = (TableElement) selectedElements[0];

                ViewTable toRemove = null;
                for (ViewTable v : selected.getTables()) {
                    if (EntityUtil.compare(t, v)) {
                        toRemove = v;
                        break;
                    }
                }

                tables.remove(t);
                selected.getTables().remove(toRemove);
                singleSelection(null);
            }
        }
    }

}
