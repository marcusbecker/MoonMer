/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mymer;

import br.com.mvbos.jeg.element.ElementModel;
import br.com.mvbos.jeg.element.SelectorElement;
import br.com.mvbos.jeg.engine.GraphicTool;
import br.com.mvbos.jeg.window.Camera;
import br.com.mvbos.mymer.combo.Option;
import br.com.mvbos.mymer.el.DataBaseElement;
import br.com.mvbos.mymer.el.IndexElement;
import br.com.mvbos.mymer.el.RelationshipElement;
import br.com.mvbos.mymer.el.TableElement;
import br.com.mvbos.mymer.tree.DataTreeNode;
import br.com.mvbos.mymer.tree.TableTreeNode;
import br.com.mvbos.mymer.sync.ImportBases;
import br.com.mvbos.mymer.table.RowItemSelection;
import br.com.mvbos.mymer.xml.Undo;
import br.com.mvbos.mymer.xml.field.Field;
import br.com.mvbos.mymer.xml.XMLUtil;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author MarcusS
 */
public class Window extends javax.swing.JFrame {

    private JPanel canvas;
    private final Timer timer;

    private final int camSize = 9000;

    private float scale = 1;
    private DataBaseElement dataBaseSelected;

    private boolean isAltDown;
    private boolean isControlDown;

    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode filterRoot;

    private EditTool mode = EditTool.SELECTOR;

    private void removeTable(TableElement e) {
        XMLUtil.removeField(e);
        populeTreeNodes();
    }

    private void selectLast(JComboBox cb) {
        cb.setSelectedIndex(cb.getItemCount() - 1);
    }

    private static int cc = 0;

    private void contaChamadas(String name) {
        System.out.println("name " + name + " " + ++cc);
    }

    private TableElement getTableSeletected() {
        if (selectedElements[0] instanceof TableElement) {
            return (TableElement) selectedElements[0];
        }

        return null;
    }

    private void addNewTableOnTree(TableElement te) {

        for (int i = 0; i < root.getChildCount(); i++) {
            DataTreeNode dtn = (DataTreeNode) root.getChildAt(i);

            if (dtn.get().equals(te.getDataBase())) {
                dtn.add(new TableTreeNode(te));
                break;
            }

        }

        treeBases.updateUI();
    }

    private void expandTableOnTree3() {
        TreePath rootPath = new TreePath(root);
        Enumeration<TreePath> expandedPaths = treeBases.getExpandedDescendants(rootPath);

        TreePath selectedPath = treeBases.getSelectionPath();

        populeTreeNodes();

        while (expandedPaths != null && expandedPaths.hasMoreElements()) {
            TreePath path = expandedPaths.nextElement();
            System.out.println("expand " + path);
            treeBases.expandPath(path);
        }

        //if (isPathValid(selectedPath)) {
        treeBases.setSelectionPath(selectedPath);
        //}
    }

    private void expandTableOnTree2() {
        //TreePath[] selectionPaths = treeBases.getSelectionPaths();
        boolean[] sel = new boolean[root.getChildCount()];
        for (int i = 0; i < root.getChildCount(); i++) {
            treeBases.isExpanded(new TreePath(root.getChildAt(i)));
            sel[i] = treeBases.isExpanded(i);
            root.getChildAt(i);
            System.out.println("expandRow " + sel[i] + " " + root.getChildAt(i));

            //System.out.println("expandRow " + sel[i]);
        }

        populeTreeNodes();

        for (int i = 0; i < sel.length; i++) {
            if (sel[i]) {

                treeBases.expandRow(i);
            }
        }

    }

    private void cancelTablesEditions(JTable... tables) {
        for (JTable tb : tables) {
            if (tb.isEditing()) {
                tb.getCellEditor().stopCellEditing();
            }
        }
    }

    private class MyDispatcher implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {

                if (KeyEvent.VK_PAGE_DOWN == e.getKeyCode() || KeyEvent.VK_PAGE_UP == e.getKeyCode()) {

                    if (isControlDown) {
                        Camera.c().rollX(KeyEvent.VK_PAGE_DOWN == e.getKeyCode() ? 100 : -100);
                    } else {
                        Camera.c().rollY(KeyEvent.VK_PAGE_DOWN == e.getKeyCode() ? 100 : -100);
                    }

                    e.consume();

                } else if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
                    mode = EditTool.SELECTOR;
                    cancelRelationship();
                }

            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (tbFields.isEditing()) {
                    return false;
                }
                //System.out.println("e.getKeyCode() " + e.getKeyCode());
                if (107 == e.getKeyCode()) {
                    //scale += 0.1f;
                    applyZoom(10);

                } else if (109 == e.getKeyCode()) {
                    //scale -= 0.1f;
                    applyZoom(-10);

                } else if (KeyEvent.VK_EQUALS == e.getKeyCode()) {
                    //scale = 1;
                    applyZoom(0);

                } else if (KeyEvent.VK_DELETE == e.getKeyCode()) {
                    removeSelectedTables();

                } else {
                    //System.out.println(e.getKeyCode());
                }

            } else if (e.getID() == KeyEvent.KEY_TYPED) {
            }

            isAltDown = e.isAltDown();
            isControlDown = e.isControlDown();

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
    public Window() {

        initComponents();

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        //KeyStroke plus = KeyStroke.getKeyStroke("+");
        //this.getInputMap().put(KeyStroke.getKeyStroke("F2"), "doSomething");
        //this.getActionMap().put("doSomething", anAction);
        //createTest();
        populeComboBoxes();
        Camera.c().config(camSize, camSize, canvas.getWidth(), canvas.getHeight());
        //Camera.c().offSet(100, 100);
        Camera.c().setAllowOffset(true);

        WindowSerializable ws = WindowSerializable.load();
        Camera.c().move(ws.cam.x, ws.cam.y);

        timer = new Timer(60, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.repaint();

                miniMap.repaint();
            }
        });

        timer.start();
    }

    private void applyZoom(int val) {
        jsZoom.setValue(val != 0 ? jsZoom.getValue() + val : val);
    }

    private void populeComboBoxes() {

        if (cbBases.getItemCount() > 0) {
            cbBases.removeAllItems();
            cbEditDataBase.removeAllItems();
        }

        cbEditDataBase.addItem(XMLUtil.DEFAULT_DATA_BASE.getName());

        for (DataBaseElement d : XMLUtil.dataBases) {
            cbBases.addItem(d.getName());
            cbEditDataBase.addItem(d.getName());
        }
    }

    private void populeTreeNodes() {
        if (root == null) {
            root = new DefaultMutableTreeNode("Databases");
        } else {
            root.removeAllChildren();
        }

        for (DataBaseElement d : XMLUtil.dataBases) {
            DefaultMutableTreeNode db = new DataTreeNode(d);

            for (TableElement t : d.getTables()) {
                db.add(new TableTreeNode(t));
            }

            root.add(db);
        }

        treeBases.setModel(new DefaultTreeModel(root));
    }

    private void filterTreeNodes(String filter) {

        if (filter == null || filter.trim().isEmpty()) {
            treeBases.setModel(new DefaultTreeModel(root));
            return;
        }

        filterRoot = (DefaultMutableTreeNode) root.clone();

        for (DataBaseElement d : XMLUtil.dataBases) {
            DefaultMutableTreeNode db = new DataTreeNode(d);

            for (TableElement t : d.getTables()) {
                if (t.getName().toLowerCase().contains(filter.toLowerCase())) {
                    db.add(new TableTreeNode(t));
                }
            }

            filterRoot.add(db);
        }

        treeBases.setModel(new DefaultTreeModel(filterRoot));

        for (int i = 0; i < filterRoot.getChildCount(); i++) {
            treeBases.expandRow(i);
        }

        //treeBases.updateUI();
    }

    private void save() {
        if (XMLUtil.exportFields() && XMLUtil.exportFieldsPosition() && XMLUtil.exportConfig() && XMLUtil.exportRelations()) {
            XMLUtil.exportIndices();
            lblInfo.setText("Save sucess at " + Calendar.getInstance().getTime());
        }

        WindowSerializable ws = new WindowSerializable();
        ws.cam = new Point(Camera.c().getPx(), Camera.c().getPy());
        WindowSerializable.save(ws);
    }

    private void copyToClip() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        StringSelection selection = new StringSelection(clip.toString());
        clipboard.setContents(selection, selection);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dlgDataBase = new javax.swing.JDialog();
        ccDBColor = new javax.swing.JColorChooser();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfDBName = new javax.swing.JTextField();
        tfDBTablesCounter = new javax.swing.JTextField();
        cbEditDataBase = new javax.swing.JComboBox();
        btnSaveDataBase = new javax.swing.JButton();
        dlgSearchField = new javax.swing.JDialog();
        tfSearchField = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnTop = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        pnLeft = new javax.swing.JPanel();
        pnMenu = new javax.swing.JPanel();
        btnCanvasColor = new javax.swing.JButton();
        btnRemoveTable = new javax.swing.JButton();
        btnAddTable = new javax.swing.JButton();
        btnAddRelOneOne = new javax.swing.JButton();
        btnAddRelOneMany = new javax.swing.JButton();
        btnCanvasColor5 = new javax.swing.JButton();
        btnCrop = new javax.swing.JToggleButton();
        pnCanvas = createCanvas();
        pnTree = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tfFilter = new javax.swing.JTextField();
        btnClearFilter = new javax.swing.JButton();
        jsZoom = new javax.swing.JSlider();
        jScrollPane2 = new javax.swing.JScrollPane();
        treeBases = new javax.swing.JTree();
        populeTreeNodes();
        pnMiniMap = createMiniMap();
        pnBottom = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabTableField = new javax.swing.JPanel();
        spTbFields = new javax.swing.JScrollPane();
        tbFields = new javax.swing.JTable();
        tfTableName = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnAddFieldTable = new javax.swing.JButton();
        btnRemFieldTable = new javax.swing.JButton();
        cbBases = new javax.swing.JComboBox();
        btnSQLFind = new javax.swing.JButton();
        btnSQLSel = new javax.swing.JButton();
        btnSQLIns = new javax.swing.JButton();
        btnSQLColNames = new javax.swing.JButton();
        btnSQLTempTable = new javax.swing.JButton();
        btnSearchField = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        tabIndex = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbIndex = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstIndexFields = new javax.swing.JList();
        btnAddIndexFieldTable = new javax.swing.JButton();
        btnRemIndex = new javax.swing.JButton();
        cbIndexAvailField = new javax.swing.JComboBox();
        btnAddFieldIndexList = new javax.swing.JButton();
        tfInfoIndexFields = new javax.swing.JTextField();
        tabRelation = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbRelationshipRight = new javax.swing.JTable();
        cbRelationship = new javax.swing.JComboBox();
        btnRemoveRelationship = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tbRelationshipLeft = new javax.swing.JTable();
        cbRelationshipType = new javax.swing.JComboBox();
        tabStruct = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tfStruct = new javax.swing.JTextArea();
        btnBuildSctruct = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        miSave = new javax.swing.JMenuItem();
        miExit = new javax.swing.JMenuItem();
        menuEditDataBase = new javax.swing.JMenu();
        miUndo = new javax.swing.JMenuItem();
        miCloneTable = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        miNewTable = new javax.swing.JMenuItem();
        miBases = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        miImport = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        miAutoPos = new javax.swing.JMenuItem();
        miCopyXml = new javax.swing.JMenuItem();

        jLabel1.setText("Name:");

        jLabel2.setText("Tables:");

        tfDBTablesCounter.setEditable(false);

        cbEditDataBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEditDataBaseActionPerformed(evt);
            }
        });

        btnSaveDataBase.setText("Save");
        btnSaveDataBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveDataBaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dlgDataBaseLayout = new javax.swing.GroupLayout(dlgDataBase.getContentPane());
        dlgDataBase.getContentPane().setLayout(dlgDataBaseLayout);
        dlgDataBaseLayout.setHorizontalGroup(
            dlgDataBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(dlgDataBaseLayout.createSequentialGroup()
                .addGroup(dlgDataBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ccDBColor, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
                    .addGroup(dlgDataBaseLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(dlgDataBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dlgDataBaseLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfDBName, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(dlgDataBaseLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfDBTablesCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(dlgDataBaseLayout.createSequentialGroup()
                                .addComponent(cbEditDataBase, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSaveDataBase)))))
                .addContainerGap())
        );
        dlgDataBaseLayout.setVerticalGroup(
            dlgDataBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dlgDataBaseLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(dlgDataBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbEditDataBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveDataBase))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dlgDataBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDBName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dlgDataBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfDBTablesCounter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ccDBColor, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addContainerGap())
        );

        tfSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfSearchFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout dlgSearchFieldLayout = new javax.swing.GroupLayout(dlgSearchField.getContentPane());
        dlgSearchField.getContentPane().setLayout(dlgSearchFieldLayout);
        dlgSearchFieldLayout.setHorizontalGroup(
            dlgSearchFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfSearchField, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        dlgSearchFieldLayout.setVerticalGroup(
            dlgSearchFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("MoonMer - Simple for many, many tables.");
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

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane1PropertyChange(evt);
            }
        });

        jSplitPane2.setDividerLocation(600);
        jSplitPane2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSplitPane2PropertyChange(evt);
            }
        });

        pnMenu.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnCanvasColor.setBackground(new java.awt.Color(255, 255, 255));
        btnCanvasColor.setText(" ");
        btnCanvasColor.setToolTipText("Change background color");

        btnRemoveTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/del_table.png"))); // NOI18N
        btnRemoveTable.setToolTipText("Remove selected Table");
        btnRemoveTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveTableActionPerformed(evt);
            }
        });

        btnAddTable.setIcon(new javax.swing.ImageIcon(getClass().getResource("/table.png"))); // NOI18N
        btnAddTable.setToolTipText("Add new Table");
        btnAddTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTableActionPerformed(evt);
            }
        });

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

        btnCanvasColor5.setBackground(new java.awt.Color(255, 255, 255));
        btnCanvasColor5.setText(" ");

        btnCrop.setSelected(true);
        btnCrop.setText("Crop");
        btnCrop.setToolTipText("Limit Table height");
        btnCrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCropActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnMenuLayout = new javax.swing.GroupLayout(pnMenu);
        pnMenu.setLayout(pnMenuLayout);
        pnMenuLayout.setHorizontalGroup(
            pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddTable)
                    .addComponent(btnRemoveTable)
                    .addComponent(btnCanvasColor))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMenuLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddRelOneOne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddRelOneMany, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCanvasColor5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCrop))
                .addContainerGap())
        );

        pnMenuLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddRelOneMany, btnAddRelOneOne, btnAddTable, btnCanvasColor, btnCanvasColor5, btnCrop, btnRemoveTable});

        pnMenuLayout.setVerticalGroup(
            pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMenuLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(btnCanvasColor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoveTable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddRelOneOne)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddRelOneMany)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCanvasColor5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCrop)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnCanvasLayout = new javax.swing.GroupLayout(pnCanvas);
        pnCanvas.setLayout(pnCanvasLayout);
        pnCanvasLayout.setHorizontalGroup(
            pnCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 514, Short.MAX_VALUE)
        );
        pnCanvasLayout.setVerticalGroup(
            pnCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnLeftLayout = new javax.swing.GroupLayout(pnLeft);
        pnLeft.setLayout(pnLeftLayout);
        pnLeftLayout.setHorizontalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnLeftLayout.createSequentialGroup()
                .addComponent(pnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnLeftLayout.setVerticalGroup(
            pnLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane2.setLeftComponent(pnLeft);

        tfFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfFilterKeyReleased(evt);
            }
        });

        btnClearFilter.setText("C");
        btnClearFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearFilterActionPerformed(evt);
            }
        });

        jsZoom.setMajorTickSpacing(10);
        jsZoom.setMaximum(120);
        jsZoom.setMinimum(-95);
        jsZoom.setMinorTickSpacing(5);
        jsZoom.setPaintTicks(true);
        jsZoom.setSnapToTicks(true);
        jsZoom.setToolTipText("Zoom");
        jsZoom.setValue(1);
        jsZoom.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jsZoomStateChanged(evt);
            }
        });

        treeBases.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeBasesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(treeBases);

        javax.swing.GroupLayout pnMiniMapLayout = new javax.swing.GroupLayout(pnMiniMap);
        pnMiniMap.setLayout(pnMiniMapLayout);
        pnMiniMapLayout.setHorizontalGroup(
            pnMiniMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnMiniMapLayout.setVerticalGroup(
            pnMiniMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 91, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnMiniMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jsZoom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tfFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnClearFilter)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnMiniMap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jsZoom, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tfFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnClearFilter))
                    .addContainerGap(263, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout pnTreeLayout = new javax.swing.GroupLayout(pnTree);
        pnTree.setLayout(pnTreeLayout);
        pnTreeLayout.setHorizontalGroup(
            pnTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnTreeLayout.setVerticalGroup(
            pnTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jSplitPane2.setRightComponent(pnTree);

        javax.swing.GroupLayout pnTopLayout = new javax.swing.GroupLayout(pnTop);
        pnTop.setLayout(pnTopLayout);
        pnTopLayout.setHorizontalGroup(
            pnTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );
        pnTopLayout.setVerticalGroup(
            pnTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );

        jSplitPane1.setTopComponent(pnTop);

        tbFields.setModel(createFieldTableModel());
        spTbFields.setViewportView(tbFields);
        configureTable();

        tfTableName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfTableNameKeyReleased(evt);
            }
        });

        btnSave.setText("Update");
        btnSave.setToolTipText("");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnAddFieldTable.setText("+");
        btnAddFieldTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFieldTableActionPerformed(evt);
            }
        });

        btnRemFieldTable.setText("-");
        btnRemFieldTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemFieldTableActionPerformed(evt);
            }
        });

        btnSQLFind.setText("1");
        btnSQLFind.setToolTipText("Copy select first to clipboard");
        btnSQLFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSQLFindActionPerformed(evt);
            }
        });

        btnSQLSel.setText("2");
        btnSQLSel.setToolTipText("Copy select all to clipboard");
        btnSQLSel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSQLSelActionPerformed(evt);
            }
        });

        btnSQLIns.setText("3");
        btnSQLIns.setToolTipText("Copy insert to clipboard");
        btnSQLIns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSQLInsActionPerformed(evt);
            }
        });

        btnSQLColNames.setText("4");
        btnSQLColNames.setToolTipText("Copy col names to clipboard");
        btnSQLColNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSQLColNamesActionPerformed(evt);
            }
        });

        btnSQLTempTable.setText("5");
        btnSQLTempTable.setToolTipText("Copy temp table to clipboard");
        btnSQLTempTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSQLTempTableActionPerformed(evt);
            }
        });

        btnSearchField.setText("Q");
        btnSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchFieldActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout tabTableFieldLayout = new javax.swing.GroupLayout(tabTableField);
        tabTableField.setLayout(tabTableFieldLayout);
        tabTableFieldLayout.setHorizontalGroup(
            tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTableFieldLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabTableFieldLayout.createSequentialGroup()
                        .addComponent(btnAddFieldTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemFieldTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearchField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfTableName, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cbBases, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave))
                    .addGroup(tabTableFieldLayout.createSequentialGroup()
                        .addGroup(tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnSQLFind)
                                .addComponent(btnSQLSel)
                                .addComponent(btnSQLIns)
                                .addComponent(btnSQLColNames))
                            .addComponent(btnSQLTempTable))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spTbFields, javax.swing.GroupLayout.DEFAULT_SIZE, 873, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabTableFieldLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddFieldTable, btnRemFieldTable});

        tabTableFieldLayout.setVerticalGroup(
            tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTableFieldLayout.createSequentialGroup()
                .addGroup(tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tfTableName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSave)
                        .addComponent(btnAddFieldTable)
                        .addComponent(btnRemFieldTable)
                        .addComponent(cbBases, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearchField)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabTableFieldLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabTableFieldLayout.createSequentialGroup()
                        .addComponent(btnSQLFind)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSQLSel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSQLIns)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSQLColNames)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSQLTempTable)
                        .addGap(0, 151, Short.MAX_VALUE))
                    .addComponent(spTbFields, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Fields", tabTableField);

        tbIndex.setModel(createIndexTableModel());
        tbIndex.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(tbIndex);

        lstIndexFields.setModel(new DefaultListModel<String>());
        lstIndexFields.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lstIndexFieldsKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(lstIndexFields);

        btnAddIndexFieldTable.setText("+");
        btnAddIndexFieldTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddIndexFieldTableActionPerformed(evt);
            }
        });

        btnRemIndex.setText("-");
        btnRemIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemIndexActionPerformed(evt);
            }
        });

        cbIndexAvailField.setModel(new DefaultComboBoxModel());

        btnAddFieldIndexList.setText("+");
        btnAddFieldIndexList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFieldIndexListActionPerformed(evt);
            }
        });

        tfInfoIndexFields.setEditable(false);
        tfInfoIndexFields.setText("Fields");

        javax.swing.GroupLayout tabIndexLayout = new javax.swing.GroupLayout(tabIndex);
        tabIndex.setLayout(tabIndexLayout);
        tabIndexLayout.setHorizontalGroup(
            tabIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabIndexLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfInfoIndexFields)
                    .addGroup(tabIndexLayout.createSequentialGroup()
                        .addGroup(tabIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddIndexFieldTable)
                            .addComponent(btnRemIndex))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                            .addGroup(tabIndexLayout.createSequentialGroup()
                                .addComponent(cbIndexAvailField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAddFieldIndexList)))))
                .addContainerGap())
        );

        tabIndexLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAddIndexFieldTable, btnRemIndex});

        tabIndexLayout.setVerticalGroup(
            tabIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabIndexLayout.createSequentialGroup()
                .addGroup(tabIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(tabIndexLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAddIndexFieldTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemIndex)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(tabIndexLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabIndexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbIndexAvailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddFieldIndexList))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfInfoIndexFields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        tabIndexLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnAddIndexFieldTable, btnRemIndex});

        jTabbedPane1.addTab("Index", tabIndex);

        tbRelationshipRight.setModel(createRelationshipTableModel(tbRelationshipRight));
        tbRelationshipRight.setName("tbRelationshipRight"); // NOI18N
        jScrollPane5.setViewportView(tbRelationshipRight);

        cbRelationship.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbRelationshipItemStateChanged(evt);
            }
        });

        btnRemoveRelationship.setText("-");
        btnRemoveRelationship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveRelationshipActionPerformed(evt);
            }
        });

        tbRelationshipLeft.setModel(createRelationshipTableModel(tbRelationshipLeft));
        tbRelationshipLeft.setName("tbRelationshipLeft"); // NOI18N
        jScrollPane6.setViewportView(tbRelationshipLeft);

        cbRelationshipType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 .. 1", "1 .. *" }));
        cbRelationshipType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRelationshipTypeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tabRelationLayout = new javax.swing.GroupLayout(tabRelation);
        tabRelation.setLayout(tabRelationLayout);
        tabRelationLayout.setHorizontalGroup(
            tabRelationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabRelationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabRelationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabRelationLayout.createSequentialGroup()
                        .addComponent(cbRelationshipType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbRelationship, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRemoveRelationship))
                    .addGroup(tabRelationLayout.createSequentialGroup()
                        .addComponent(jScrollPane6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5)))
                .addContainerGap())
        );
        tabRelationLayout.setVerticalGroup(
            tabRelationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabRelationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabRelationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbRelationship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoveRelationship)
                    .addComponent(cbRelationshipType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabRelationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Relationship", tabRelation);

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Struct", tabStruct);

        lblInfo.setText("...");

        javax.swing.GroupLayout pnBottomLayout = new javax.swing.GroupLayout(pnBottom);
        pnBottom.setLayout(pnBottomLayout);
        pnBottomLayout.setHorizontalGroup(
            pnBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnBottomLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(pnBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1)))
        );
        pnBottomLayout.setVerticalGroup(
            pnBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo))
        );

        jSplitPane1.setRightComponent(pnBottom);

        menuFile.setText("File");

        miSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        miSave.setText("Save");
        miSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSaveActionPerformed(evt);
            }
        });
        menuFile.add(miSave);

        miExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        miExit.setText("Exit");
        miExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miExitActionPerformed(evt);
            }
        });
        menuFile.add(miExit);

        jMenuBar1.add(menuFile);

        menuEditDataBase.setText("Data");

        miUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        miUndo.setText("Undo");
        miUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miUndoActionPerformed(evt);
            }
        });
        menuEditDataBase.add(miUndo);

        miCloneTable.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        miCloneTable.setText("Clone Table");
        miCloneTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCloneTableActionPerformed(evt);
            }
        });
        menuEditDataBase.add(miCloneTable);
        menuEditDataBase.add(jSeparator3);

        miNewTable.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        miNewTable.setText("New Table");
        miNewTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miNewTableActionPerformed(evt);
            }
        });
        menuEditDataBase.add(miNewTable);

        miBases.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        miBases.setText("New Database");
        miBases.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miBasesActionPerformed(evt);
            }
        });
        menuEditDataBase.add(miBases);
        menuEditDataBase.add(jSeparator4);

        miImport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        miImport.setText("Import");
        miImport.setToolTipText("Import or Sync bases");
        miImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miImportActionPerformed(evt);
            }
        });
        menuEditDataBase.add(miImport);

        jMenuBar1.add(menuEditDataBase);

        jMenu1.setText("Tools");

        miAutoPos.setText("Order");
        miAutoPos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAutoPosActionPerformed(evt);
            }
        });
        jMenu1.add(miAutoPos);

        miCopyXml.setText("Copy xml");
        miCopyXml.setToolTipText("Copy xml to clipboard");
        miCopyXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miCopyXmlActionPerformed(evt);
            }
        });
        jMenu1.add(miCopyXml);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miSaveActionPerformed

        save();


    }//GEN-LAST:event_miSaveActionPerformed

    private void miAutoPosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAutoPosActionPerformed

        int px = 5;
        int py = 5;

        int lastWidth = 0;
        int maxHeight = 0;
        int sumHeight = 0;

        for (TableElement filter : XMLUtil.filter) {
            ElementModel e = filter;
            maxHeight = e.getHeight() > maxHeight ? e.getHeight() : maxHeight;
            if (px + e.getWidth() > camSize) {
                px = 5;
                py += maxHeight + 15;

                sumHeight += maxHeight;
                maxHeight = 0;

            } else {
                px += lastWidth + 5;
            }
            e.setPxy(px, py);
            lastWidth = e.getWidth();
        }

        if (sumHeight > canvas.getHeight()) {
            Camera.c().config(camSize, sumHeight + 60, canvas.getWidth(), canvas.getHeight());
        }

    }//GEN-LAST:event_miAutoPosActionPerformed

    private void btnRemoveTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveTableActionPerformed

        removeSelectedTables();

    }//GEN-LAST:event_btnRemoveTableActionPerformed

    private void removeSelectedTables() throws HeadlessException {
        if (selectedElements[0] != null) {
            if (JOptionPane.showConfirmDialog(this, "Remove table " + selectedElements[0].getName() + " ?", "Do you want to remove the selected table?", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                removeTable((TableElement) selectedElements[0]);
                singleSelection(null);
            }
        }
    }

    private void btnAddTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTableActionPerformed

        addNewTable();

    }//GEN-LAST:event_btnAddTableActionPerformed

    private void addNewTable() {

        DataBaseElement db = XMLUtil.DEFAULT_DATA_BASE;

        if (getTableSeletected() != null) {
            db = getTableSeletected().getDataBase();
        }

        TableElement te = new TableElement(50, 50, db, "New Table " + ++XMLUtil.tableCount);

        db.getTables().add(te);

        te.setPxy(Camera.c().getCpx(), Camera.c().getCpy());
        te.update();

        XMLUtil.filter.add(te);
        addNewTableOnTree(te);
    }

    private void btnCropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCropActionPerformed

        Common.crop = btnCrop.isSelected();
        Common.updateAll = true;

    }//GEN-LAST:event_btnCropActionPerformed

    private void btnAddFieldTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFieldTableActionPerformed

        if (selectedElements[0] != null) {
            TableElement e = (TableElement) selectedElements[0];
            e.getFields().add(new Field("New", Common.comboTypes[0]));
            e.update();
            FieldTableModel m = (FieldTableModel) tbFields.getModel();
            m.fireTableDataChanged();
        }

    }//GEN-LAST:event_btnAddFieldTableActionPerformed

    private void btnRemFieldTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemFieldTableActionPerformed

        if (selectedElements[0] != null && tbFields.getSelectedRow() != -1) {
            TableElement e = (TableElement) selectedElements[0];
            e.getFields().remove(tbFields.getSelectedRow());

            e.update();

            FieldTableModel m = (FieldTableModel) tbFields.getModel();
            m.fireTableRowsDeleted(tbFields.getSelectedRow(), tbFields.getSelectedRow());
        }

    }//GEN-LAST:event_btnRemFieldTableActionPerformed

    private void jsZoomStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jsZoomStateChanged

        JSlider source = (JSlider) evt.getSource();
        if (!source.getValueIsAdjusting()) {
            float z = 1 + source.getValue() / 100f;
            scale = z;
            jsZoom.setToolTipText(String.format("zoom %d", (int) (z * 100)) + "%");
        }

    }//GEN-LAST:event_jsZoomStateChanged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        if (selectedElements[0] != null) {
            TableElement e = (TableElement) selectedElements[0];
            e.update();

            DataBaseElement db = XMLUtil.filterBases.get(cbBases.getSelectedIndex());

            if (!e.getDataBase().equals(db)) {

                e.getDataBase().getTables().remove(e);

                e.setDataBase(db);
                e.setColor(db.getColor());

                db.getTables().add(e);

                populeTreeNodes();
            }
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void miBasesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miBasesActionPerformed

        if (getTableSeletected() == null) {
            dataBaseSelected = null;

            tfDBName.setText(null);
            tfDBTablesCounter.setText("0");
            cbEditDataBase.setSelectedIndex(0);
            ccDBColor.setColor(XMLUtil.DEFAULT_DATA_BASE.getColor());

        } else {
            dataBaseSelected = getTableSeletected().getDataBase();

            tfDBName.setText(dataBaseSelected.getName());
            tfDBTablesCounter.setText(String.valueOf(dataBaseSelected.getTables().size()));
            ccDBColor.setColor(dataBaseSelected.getColor());
            cbEditDataBase.setSelectedItem(dataBaseSelected.getName());
        }

        dlgDataBase.pack();
        dlgDataBase.setLocationRelativeTo(this);
        dlgDataBase.setVisible(true);

    }//GEN-LAST:event_miBasesActionPerformed

    private void cbEditDataBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEditDataBaseActionPerformed

        if (dlgDataBase.isVisible()) {
            JComboBox cb = (JComboBox) evt.getSource();

            if (cb.getSelectedIndex() <= 0) {
                tfDBName.setText(null);
                tfDBTablesCounter.setText("0");
                ccDBColor.setColor(XMLUtil.DEFAULT_DATA_BASE.getColor());

                dataBaseSelected = null;

            } else {
                DataBaseElement db = XMLUtil.filterBases.get(cb.getSelectedIndex() - 1);

                tfDBName.setText(db.getName());
                tfDBTablesCounter.setText(Integer.toString(db.getTables().size()));
                ccDBColor.setColor(db.getColor());

                dataBaseSelected = db;
            }

            //System.out.println("00");
        }

    }//GEN-LAST:event_cbEditDataBaseActionPerformed

    private void btnSaveDataBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveDataBaseActionPerformed

        if (tfDBName.getText().trim().isEmpty()) {
            return;
        }

        DataBaseElement db;

        if (dataBaseSelected == null) {
            db = new DataBaseElement();
            XMLUtil.addDataBase(db);

        } else {
            db = dataBaseSelected;
        }

        db.setName(tfDBName.getText());
        db.setColor(ccDBColor.getColor());

        for (TableElement el : db.getTables()) {
            el.setDataBase(db);
            el.setColor(db.getColor());
            el.update();
        }

        dlgDataBase.setVisible(false);
        populeComboBoxes();
        populeTreeNodes();

    }//GEN-LAST:event_btnSaveDataBaseActionPerformed

    private void tfFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfFilterKeyReleased

        filterTreeNodes(tfFilter.getText());

        if (!tfFilter.getText().isEmpty()) {

            /*String s = tfFilter.getText().toLowerCase();
             for (TableElement t : XMLUtil.filter) {
             if (t.getName().toLowerCase().startsWith(s)) {
             positionCam(t.getPx(), t.getPy());
             break;
             }
             }*/
        }

    }//GEN-LAST:event_tfFilterKeyReleased

    private void miExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miExitActionPerformed

        exit();

    }//GEN-LAST:event_miExitActionPerformed


    private void treeBasesValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeBasesValueChanged

        JTree tree = (JTree) evt.getSource();

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        if (node == null) {
            return;
        }

        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof TableElement) {
            TableElement t = (TableElement) nodeInfo;
            positionCam(t);

            singleSelection(t);
        }

        TreePath[] arr = tree.getSelectionPaths();
        for (TreePath tp : arr) {
            node = (DefaultMutableTreeNode) tp.getLastPathComponent();
            nodeInfo = node.getUserObject();

            if (nodeInfo instanceof TableElement) {
                TableElement t = (TableElement) nodeInfo;
                multiSelect(t);
            }

        }

        //System.out.println(t.getSelectionPath());

    }//GEN-LAST:event_treeBasesValueChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        exit();

    }//GEN-LAST:event_formWindowClosing

    private void btnClearFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearFilterActionPerformed

        tfFilter.setText(null);
        filterTreeNodes(null);

    }//GEN-LAST:event_btnClearFilterActionPerformed

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged

        Camera.c().config(Camera.c().getSceneWidth(), Camera.c().getSceneHeight(), canvas.getWidth(), canvas.getHeight());


    }//GEN-LAST:event_formWindowStateChanged

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

        Camera.c().config(Camera.c().getSceneWidth(), Camera.c().getSceneHeight(), canvas.getWidth(), canvas.getHeight());

    }//GEN-LAST:event_formComponentResized

    private void jSplitPane1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane1PropertyChange

        Camera.c().config(Camera.c().getSceneWidth(), Camera.c().getSceneHeight(), canvas.getWidth(), canvas.getHeight());

    }//GEN-LAST:event_jSplitPane1PropertyChange

    private void jSplitPane2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSplitPane2PropertyChange


    }//GEN-LAST:event_jSplitPane2PropertyChange

    private final StringBuilder clip = new StringBuilder(100);

    private void btnSQLFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSQLFindActionPerformed

        if (selectedElements[0] != null) {
            clip.delete(0, clip.length());
            TableElement t = (TableElement) selectedElements[0];

            clip.append("FIND FIRST ").append(t.getName()).append(" NO-LOCK NO-ERROR.");
            clip.append("\r\nDISP ").append(t.getName()).append(".");

            copyToClip();
        }

    }//GEN-LAST:event_btnSQLFindActionPerformed

    private void btnSQLSelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSQLSelActionPerformed

        if (selectedElements[0] != null) {
            clip.delete(0, clip.length());
            TableElement t = (TableElement) selectedElements[0];

            clip.append("FOR EACH ").append(t.getName()).append(" NO-LOCK:");
            clip.append("\r\nDISP ").append(t.getName()).append(".");
            clip.append("\r\nEND.");

            copyToClip();
        }

    }//GEN-LAST:event_btnSQLSelActionPerformed

    private void btnSQLInsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSQLInsActionPerformed

        if (selectedElements[0] != null) {
            clip.delete(0, clip.length());
            TableElement t = (TableElement) selectedElements[0];

            clip.append("CREATE ").append(t.getName()).append(".");
            clip.append("\r\nASSIGN\r\n");

            for (Field f : t.getFields()) {
                clip.append(t.getName()).append(".").append(f.getName()).append(" = ").append(f.getType()).append("\r\n");
            }

            clip.append(".");

            copyToClip();
        }


    }//GEN-LAST:event_btnSQLInsActionPerformed

    private void btnSQLColNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSQLColNamesActionPerformed

        if (selectedElements[0] != null) {
            clip.delete(0, clip.length());
            TableElement t = (TableElement) selectedElements[0];

            for (Field f : t.getFields()) {
                clip.append(f.getName()).append("\r\n");
            }

            copyToClip();
        }

    }//GEN-LAST:event_btnSQLColNamesActionPerformed

    private void btnSQLTempTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSQLTempTableActionPerformed

        if (selectedElements[0] != null) {
            clip.delete(0, clip.length());
            TableElement t = (TableElement) selectedElements[0];

            clip.append("DEF TEMP-TABLE ").append(t.getName()).append(" NO-UNDO");
            for (Field f : t.getFields()) {
                clip.append("\r\nFIELD ").append(f.getName()).append(" AS ").append(f.getType());
            }

            clip.append("\r\n.");

            copyToClip();
        }


    }//GEN-LAST:event_btnSQLTempTableActionPerformed

    private void tfSearchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfSearchFieldKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            //tfSearchField.setText("");
            dlgSearchField.setVisible(false);
            return;
        }

        if (selectedElements[0] != null && !tfSearchField.getText().isEmpty()) {
            TableElement t = (TableElement) selectedElements[0];
            for (int i = 0; i < t.getFields().size(); i++) {
                Field f = t.getFields().get(i);

                if (f.getName().contains(tfSearchField.getText())) {
                    tbFields.setRowSelectionInterval(i, i);
                    tbFields.scrollRectToVisible(tbFields.getCellRect(i, 0, true));
                    //spTbFields.r
                    break;
                }
            }
        }

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            dlgSearchField.setVisible(false);
        }


    }//GEN-LAST:event_tfSearchFieldKeyReleased

    private void btnSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchFieldActionPerformed
        dlgSearchField.pack();
        dlgSearchField.setLocationRelativeTo(this);
        dlgSearchField.setVisible(true);
    }//GEN-LAST:event_btnSearchFieldActionPerformed

    private void tfTableNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfTableNameKeyReleased

        if (selectedElements[0] != null) {
            selectedElements[0].setName(tfTableName.getText());
            selectedElements[0].update();
        }

    }//GEN-LAST:event_tfTableNameKeyReleased

    private void miUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miUndoActionPerformed

        TableElement e = Undo.get();
        if (e != null) {
            e.getDataBase().getTables().add(e);
            XMLUtil.filter.add(e);
        }

    }//GEN-LAST:event_miUndoActionPerformed

    private void miImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miImportActionPerformed

        final Window w = this;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImportBases importBases = new ImportBases();
                importBases.setLocationRelativeTo(w);
                importBases.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                importBases.setVisible(true);
            }
        });

    }//GEN-LAST:event_miImportActionPerformed

    private void btnAddRelOneOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRelOneOneActionPerformed

        mode = EditTool.RELATION;
        relType = RelationshipElement.Type.ONE_TO_ONE;

    }//GEN-LAST:event_btnAddRelOneOneActionPerformed

    private void btnAddRelOneManyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRelOneManyActionPerformed

        mode = EditTool.RELATION;
        relType = RelationshipElement.Type.ONE_TO_MORE;

    }//GEN-LAST:event_btnAddRelOneManyActionPerformed

    private void btnAddIndexFieldTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddIndexFieldTableActionPerformed

        if (selectedElements[0] != null) {
            TableElement te = (TableElement) selectedElements[0];
            GenericTableModel<IndexElement> m = (GenericTableModel<IndexElement>) tbIndex.getModel();

            String name = String.format("%s%02d", te.getName(), m.getData().size() + 1);

            IndexElement ie = new IndexElement(name, false, false, true, te);
            ie.setFields(new ArrayList<Field>(5));

            m.getData().add(ie);
            XMLUtil.indices.add(ie);

            tbIndex.updateUI();
        }

    }//GEN-LAST:event_btnAddIndexFieldTableActionPerformed

    private void btnRemIndexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemIndexActionPerformed

        if (selectedElements[0] != null && tbIndex.getSelectedRow() > -1) {
            //TableElement te = (TableElement) selectedElements[0];
            DefaultListModel lstModel = (DefaultListModel) lstIndexFields.getModel();
            DefaultComboBoxModel combModel = (DefaultComboBoxModel) cbIndexAvailField.getModel();

            GenericTableModel<IndexElement> m = (GenericTableModel<IndexElement>) tbIndex.getModel();

            IndexElement ie = m.getData().get(tbIndex.getSelectedRow());

            XMLUtil.indices.remove(ie);
            m.getData().remove(tbIndex.getSelectedRow());

            lstModel.removeAllElements();
            combModel.removeAllElements();

            tbIndex.updateUI();
        }


    }//GEN-LAST:event_btnRemIndexActionPerformed

    private void btnAddFieldIndexListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFieldIndexListActionPerformed

        DefaultListModel lstModel = (DefaultListModel) lstIndexFields.getModel();
        //DefaultComboBoxModel combModel = (DefaultComboBoxModel) cbIndexAvailField.getModel();
        GenericTableModel<IndexElement> tableModel = (GenericTableModel<IndexElement>) tbIndex.getModel();

        if (tbIndex.getSelectedRow() > -1 && cbIndexAvailField.getSelectedIndex() > -1) {
            IndexElement ind = tableModel.getData().get(tbIndex.getSelectedRow());
            Field org = Find.findByName(ind.getTable().getFields(), cbIndexAvailField.getSelectedItem().toString());
            if (org != null) {
                ind.getFields().add(org);
                lstModel.addElement(cbIndexAvailField.getSelectedItem());
                cbIndexAvailField.removeItemAt(cbIndexAvailField.getSelectedIndex());
            }

        }

    }//GEN-LAST:event_btnAddFieldIndexListActionPerformed

    private void lstIndexFieldsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstIndexFieldsKeyReleased

        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            GenericTableModel<IndexElement> tableModel = (GenericTableModel<IndexElement>) tbIndex.getModel();

            if (tbIndex.getSelectedRow() > -1 && lstIndexFields.getSelectedIndex() > -1) {
                IndexElement ind = tableModel.getData().get(tbIndex.getSelectedRow());
                Field org = Find.findByName(ind.getTable().getFields(), lstIndexFields.getSelectedValue().toString());

                if (org != null) {
                    ind.getFields().remove(org);
                    updateIndexSelection();
                }
            }
        }

    }//GEN-LAST:event_lstIndexFieldsKeyReleased

    private void btnRemoveRelationshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRelationshipActionPerformed

        Option opt = (Option) cbRelationship.getSelectedItem();
        XMLUtil.relations.remove((RelationshipElement) opt.getValue());
        cbRelationship.removeItemAt(cbRelationship.getSelectedIndex());

    }//GEN-LAST:event_btnRemoveRelationshipActionPerformed


    private void cbRelationshipTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRelationshipTypeActionPerformed

        Option opt = (Option) cbRelationship.getSelectedItem();
        RelationshipElement re = (RelationshipElement) opt.getValue();

        re.setType(RelationshipElement.Type.values()[cbRelationshipType.getSelectedIndex()]);

    }//GEN-LAST:event_cbRelationshipTypeActionPerformed

    private void cbRelationshipItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbRelationshipItemStateChanged

        if (ItemEvent.SELECTED == evt.getStateChange()) {
            updateRelationType();
            Option opt = (Option) cbRelationship.getSelectedItem();
            loadRelationshipFieldTable((RelationshipElement) opt.getValue());
        }

    }//GEN-LAST:event_cbRelationshipItemStateChanged

    private void miNewTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miNewTableActionPerformed

        addNewTable();

    }//GEN-LAST:event_miNewTableActionPerformed

    private void btnBuildSctructActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildSctructActionPerformed

        StringBuilder sb = new StringBuilder(600);
        for (ElementModel e : selectedElements) {
            if (!(e instanceof TableElement)) {
                continue;
            }

            TableElement te = (TableElement) e;

            sb.append("ADD TABLE \"").append(te.getName()).append("\"\n");
            sb.append("  AREA \"Dados\"\n");
            sb.append("  DESCRIPTION \"\"\n");
            sb.append("  DUMP-NAME \"").append(te.getName()).append("\"\n");
            sb.append("\n");

            int ct = 1;
            for (Field f : te.getFields()) {
                sb.append("ADD FIELD \"").append(f.getName()).append("\" OF \"").append(te.getName()).append("\" AS ").append(f.getType()).append("\n");
                sb.append("  DESCRIPTION \"").append(f.getDescription()).append("\"\n");
                sb.append("  FORMAT \"").append(f.getFormat()).append("\"\n");
                sb.append("  INITIAL \"\"\n");
                sb.append("  LABEL \"").append(f.getName()).append("\"\n");
                //sb.append("  POSITION ").append(ct).append("\n");
                //sb.append("  MAX-WIDTH 4").append(te.getName()).append("\"\n");
                sb.append("  COLUMN-LABEL \"").append(f.getLabel()).append("\"\n");
                sb.append("  HELP \"").append(f.getHelp()).append("\"\n");
                sb.append("  ORDER ").append(ct * 10).append("\n");

                sb.append("\n");
                ct++;
            }

            for (IndexElement ie : XMLUtil.indices) {
                if (!te.equals(ie.getTable())) {
                    continue;
                }

                sb.append("ADD INDEX \"").append(ie.getName()).append("\" ON \"").append(te.getName()).append("\"\n");
                sb.append("  AREA \"Indices\"\n");
                if (ie.getPrimary()) {
                    sb.append("  UNIQUE\n");
                }
                if (ie.getUnique()) {
                    sb.append("  PRIMARY\n");
                }

                for (Field f : ie.getFields()) {
                    sb.append("  INDEX-FIELD \"").append(f.getName()).append("\" ASCENDING\n");
                }

                sb.append("\n");
            }

        }

        tfStruct.setText(sb.toString());


    }//GEN-LAST:event_btnBuildSctructActionPerformed

    private void miCloneTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCloneTableActionPerformed
        TableElement te = getTableSeletected();
        if (te != null) {
            TableElement nte = new TableElement(te.getPx() + 5, te.getPy() + 5, te.getWidth(), te.getHeight(), te.getDataBase(), "copy_" + te.getName());
            for (Field f : te.getFields()) {
                Field ff = new Field(f.getName(), f.getType());
                nte.addFields(ff);
            }

            nte.update();
            te.getDataBase().getTables().add(nte);
            XMLUtil.filter.add(nte);
        }

    }//GEN-LAST:event_miCloneTableActionPerformed

    private void miCopyXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miCopyXmlActionPerformed

        clip.delete(0, clip.length());
        clip.append(XMLUtil.tablesToString(Arrays.asList(selectedElements)));

        copyToClip();

    }//GEN-LAST:event_miCopyXmlActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddFieldIndexList;
    private javax.swing.JButton btnAddFieldTable;
    private javax.swing.JButton btnAddIndexFieldTable;
    private javax.swing.JButton btnAddRelOneMany;
    private javax.swing.JButton btnAddRelOneOne;
    private javax.swing.JButton btnAddTable;
    private javax.swing.JButton btnBuildSctruct;
    private javax.swing.JButton btnCanvasColor;
    private javax.swing.JButton btnCanvasColor5;
    private javax.swing.JButton btnClearFilter;
    private javax.swing.JToggleButton btnCrop;
    private javax.swing.JButton btnRemFieldTable;
    private javax.swing.JButton btnRemIndex;
    private javax.swing.JButton btnRemoveRelationship;
    private javax.swing.JButton btnRemoveTable;
    private javax.swing.JButton btnSQLColNames;
    private javax.swing.JButton btnSQLFind;
    private javax.swing.JButton btnSQLIns;
    private javax.swing.JButton btnSQLSel;
    private javax.swing.JButton btnSQLTempTable;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveDataBase;
    private javax.swing.JButton btnSearchField;
    private javax.swing.JComboBox cbBases;
    private javax.swing.JComboBox cbEditDataBase;
    private javax.swing.JComboBox cbIndexAvailField;
    private javax.swing.JComboBox cbRelationship;
    private javax.swing.JComboBox cbRelationshipType;
    private javax.swing.JColorChooser ccDBColor;
    private javax.swing.JDialog dlgDataBase;
    private javax.swing.JDialog dlgSearchField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JSlider jsZoom;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JList lstIndexFields;
    private javax.swing.JMenu menuEditDataBase;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem miAutoPos;
    private javax.swing.JMenuItem miBases;
    private javax.swing.JMenuItem miCloneTable;
    private javax.swing.JMenuItem miCopyXml;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JMenuItem miImport;
    private javax.swing.JMenuItem miNewTable;
    private javax.swing.JMenuItem miSave;
    private javax.swing.JMenuItem miUndo;
    private javax.swing.JPanel pnBottom;
    private javax.swing.JPanel pnCanvas;
    private javax.swing.JPanel pnLeft;
    private javax.swing.JPanel pnMenu;
    private javax.swing.JPanel pnMiniMap;
    private javax.swing.JPanel pnTop;
    private javax.swing.JPanel pnTree;
    private javax.swing.JScrollPane spTbFields;
    private javax.swing.JPanel tabIndex;
    private javax.swing.JPanel tabRelation;
    private javax.swing.JPanel tabStruct;
    private javax.swing.JPanel tabTableField;
    private javax.swing.JTable tbFields;
    private javax.swing.JTable tbIndex;
    private javax.swing.JTable tbRelationshipLeft;
    private javax.swing.JTable tbRelationshipRight;
    private javax.swing.JTextField tfDBName;
    private javax.swing.JTextField tfDBTablesCounter;
    private javax.swing.JTextField tfFilter;
    private javax.swing.JTextField tfInfoIndexFields;
    private javax.swing.JTextField tfSearchField;
    private javax.swing.JTextArea tfStruct;
    private javax.swing.JTextField tfTableName;
    private javax.swing.JTree treeBases;
    // End of variables declaration//GEN-END:variables

    private TableModel createFieldTableModel() {
        return new FieldTableModel();
    }

    private GenericTableModel createIndexTableModel() {
        final Set<String> ignore = new HashSet<>(Arrays.asList("fields", "table"));

        GenericTableModel<IndexElement> ind = new GenericTableModel<>(IndexElement.class, ignore);

        tbIndex.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()) {
                    return;
                }

                updateIndexSelection();
            }

        });

        return ind;
    }

    private GenericTableModel createRelationshipTableModel(final JTable tb) {
        final Set<String> ignore = new HashSet<>(Arrays.asList("value"));

        GenericTableModel<RowItemSelection> ris = new GenericTableModel<>(RowItemSelection.class, ignore);

        ris.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                changeIndexFields(tb);
            }

        });

        return ris;
    }

    private void changeIndexFields(JTable tb) {

        if (cbRelationship.getSelectedItem() == null) {
            return;
        }

        Option opt = (Option) cbRelationship.getSelectedItem();
        RelationshipElement re = (RelationshipElement) opt.getValue();

        GenericTableModel<RowItemSelection> m = (GenericTableModel<RowItemSelection>) tb.getModel();

        if (tb.equals(tbRelationshipLeft)) {
            re.getParentFields().clear();

            for (RowItemSelection r : m.getData()) {
                if (r.getSelected()) {
                    re.getParentFields().add((Field) r.getValue());
                }
            }

        } else {
            re.getChildFields().clear();

            for (RowItemSelection r : m.getData()) {
                if (r.getSelected()) {
                    re.getChildFields().add((Field) r.getValue());
                }
            }
        }

    }

    private void updateIndexSelection() {
        DefaultListModel lstModel = (DefaultListModel) lstIndexFields.getModel();
        DefaultComboBoxModel combModel = (DefaultComboBoxModel) cbIndexAvailField.getModel();
        GenericTableModel<IndexElement> tableModel = (GenericTableModel<IndexElement>) tbIndex.getModel();

        lstModel.removeAllElements();
        combModel.removeAllElements();

        if (tbIndex.getSelectedRow() == -1 || tableModel.getData().isEmpty()) {
            tfInfoIndexFields.setText("No table selected.");
            return;
        }

        IndexElement ind = tableModel.getData().get(tbIndex.getSelectedRow());

        if (!ind.getTable().getFields().isEmpty()) {

            for (Field f : ind.getTable().getFields()) {
                if (!ind.getFields().contains(f)) {
                    combModel.addElement(f.getName());
                }
            }

            if (!ind.getFields().isEmpty()) {
                StringBuilder fNames = new StringBuilder();
                fNames.append("Fields: ");
                int count = 0;
                for (Field f : ind.getFields()) {
                    lstModel.addElement(f.getName());
                    fNames.append(f.getName());

                    if (count++ < ind.getFields().size() - 1) {
                        fNames.append(", ");
                    }
                }

                fNames.append(String.format(". %d of %d.", count, ind.getTable().getFields().size()));
                tfInfoIndexFields.setText(fNames.toString());

            } else {
                tfInfoIndexFields.setText("No index fields.");
            }

        } else {
            tfInfoIndexFields.setText("No fields.");
        }
    }

    private void configureTable() {
        TableColumn comboColumn = tbFields.getColumnModel().getColumn(3);
        comboColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(Common.comboTypes)));

        tbFields.getColumnModel().getColumn(0).setMaxWidth(35);

        tbFields.setFillsViewportHeight(true);
    }

    private enum EditTool {

        SELECTOR, HAND, RELATION;
    }

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

    private JPanel miniMap;
    private BufferedImage buff;

    private JPanel createMiniMap() {
        miniMap = new JPanel() {
            @Override
            protected void paintComponent(Graphics gg) {
                super.paintComponent(gg);
                Graphics2D g = (Graphics2D) gg;

                g.setColor(Color.WHITE);
                g.fillRect(0, 0, miniMap.getWidth(), miniMap.getHeight());


                float w = ((float) camSize / miniMap.getWidth()) / 100f;
                float h = ((float) camSize / miniMap.getHeight()) / 100f;

                g.scale(w, h);
                for (ElementModel el : XMLUtil.filter) {
                    g.setColor(el.getColor());
                    g.fillRect(el.getPx(), el.getPy(), el.getWidth(), el.getHeight());
                }

            }
        };

        return miniMap;
    }

    private JPanel createCanvas() {
        buff = new BufferedImage(camSize, camSize, BufferedImage.TYPE_INT_RGB);

        canvas = new JPanel() {

            @Override
            protected void paintComponent(Graphics gg) {
                super.paintComponent(gg);

                //Common.graphics = (Graphics2D) gg;
                Graphics2D g = buff.createGraphics();
                Common.graphics = g;

                g.scale(scale, scale);
                //g.setColor(BACKGROUND_COLOR);
                g.setColor(btnCanvasColor.getBackground());
                g.fillRect(0, 0, camSize, camSize);

                int sp = 5;
                g.setColor(Color.BLACK);

                for (ElementModel el : selectedElements) {
                    if (el == null) {
                        break;
                    }

                    g.drawRect(Camera.c().fx(el.getPx() - sp), Camera.c().fy(el.getPy() - sp), el.getWidth() + sp * 2, el.getHeight() + sp * 2);
                }

                if (startDrag != null) {
                    int npx = mousePos.x - startDrag.x;
                    int npy = mousePos.y - startDrag.y;
                    for (ElementModel el : selectedElements) {
                        if (el == null) {
                            break;
                        }

                        g.drawRect(Camera.c().fx(el.getPx() + npx), Camera.c().fy(el.getPy() + npy), el.getWidth(), el.getHeight());
                    }
                }

                if (Common.updateAll) {
                    for (ElementModel el : XMLUtil.filter) {
                        el.update();
                    }

                    Common.updateAll = false;

                } else {

                    for (RelationshipElement rl : XMLUtil.relations) {
                        rl.drawMe(g);
                    }

                    for (ElementModel el : XMLUtil.filter) {
                        Camera.c().draw(g, el);
                    }

                    selector.drawMe(g);
                    drawRelationPointer(g);
                }

                gg.drawImage(buff, 0, 0, null);
            }

            private void drawRelationPointer(Graphics2D g) {
                if (mode != EditTool.RELATION || relLeft == null) {
                    return;
                }

                g.setColor(Color.DARK_GRAY);
                g.drawLine(Camera.c().fx(relLeft.getPx() + relLeft.getHalfWidth()), Camera.c().fy(relLeft.getPy() + relLeft.getHalfHeight()), mousePos.x, mousePos.y);
            }
        };

        canvas.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {

                    mouseElement.setPxy(e.getX() + Camera.c().getCpx(), e.getY() + Camera.c().getCpy());

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

                    mouseElement.setPxy(e.getX() + Camera.c().getCpx(), e.getY() + Camera.c().getCpy());

                    if (!isAltDown) {

                        if (selectedElements[0] == null || !isValidSelecion(mouseElement)) {
                            selector.setEnabled(true);
                            selector.setPxy(e.getX(), e.getY());
                        } else {
                            startDrag = e.getPoint();
                        }

                    } else /*if (EditTool.HAND == EditTool.SELECTOR)*/ {
                        //Camera.c().move(e.getX() - mousePos.x, getY() - mousePos.y);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (selector.isEnabled()) {
                        selector.adjustInvertSelection();
                        selector.setPx(selector.getPx() + Camera.c().getCpx());
                        selector.setPy(selector.getPy() + Camera.c().getCpy());

                        singleSelection(null);

                        for (ElementModel el : XMLUtil.filter) {

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
                //lblCanvasInfo.setText(String.format("%d %d Cam: %.0f %.0f ", mousePos.x, mousePos.y, mousePos.x + Camera.c().getCpx(), mousePos.y + Camera.c().getCpy()));
            }
        });

        canvas.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                if (isControlDown) {
                    Camera.c().rollX(e.getWheelRotation() * 5);
                } else {
                    Camera.c().rollY(e.getWheelRotation() * 5);
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

        cancelTablesEditions(tbFields, tbIndex, tbRelationshipLeft, tbRelationshipRight);

        FieldTableModel tbTableModel = (FieldTableModel) tbFields.getModel();
        GenericTableModel<IndexElement> tbIndexModel = (GenericTableModel<IndexElement>) tbIndex.getModel();

        if (el == null) {
            tfTableName.setText("");

            loadRelationship(null);
            tbIndexModel.setData(Collections.EMPTY_LIST);
            tbTableModel.setData(Collections.EMPTY_LIST);

        } else if (el instanceof TableElement) {
            System.out.println("el " + el);
            tfTableName.setText(el.getName());

            TableElement e = (TableElement) el;

            loadRelationship(e);
            cbBases.setSelectedItem(e.getDataBase().getName());
            tbIndexModel.setData(XMLUtil.findIndex(e));
            tbTableModel.setData(e.getFields());
        }

        updateIndexSelection();

        tbFields.updateUI();
        tbIndex.updateUI();
    }

    private void loadRelationship(TableElement e) {
        cbRelationship.removeAllItems();
        tbRelationshipLeft.removeAll();
        tbRelationshipRight.removeAll();

        if (e != null) {
            for (int i = 0; i < XMLUtil.relations.size(); i++) {
                RelationshipElement re = XMLUtil.relations.get(i);
                if (re.getParent().equals(e) || re.getChild().equals(e)) {
                    final String label = String.format("%s < %s > %s", re.getParent().getName(), re.getType().label, re.getChild().getName());
                    cbRelationship.addItem(new Option((short) i, re, label));
                }
            }

            if (cbRelationship.getItemCount() == 0) {
                updateRelationType();
                loadRelationshipFieldTable(null);
            }

        } else {
            updateRelationType();
            loadRelationshipFieldTable(null);
        }
    }

    private void loadRelationshipFieldTable(RelationshipElement re) {
        GenericTableModel<RowItemSelection> mLeft = (GenericTableModel<RowItemSelection>) tbRelationshipLeft.getModel();
        GenericTableModel<RowItemSelection> mRight = (GenericTableModel<RowItemSelection>) tbRelationshipRight.getModel();

        if (re != null) {

            List<RowItemSelection> lstLeft = new ArrayList<>(re.getParent().getFields().size());
            List<RowItemSelection> lstRight = new ArrayList<>(re.getChild().getFields().size());

            String label;

            for (Field f : re.getParent().getFields()) {
                label = String.format("%s ( %s )", f.getName(), f.getType());
                lstLeft.add(new RowItemSelection(re.getParentFields().contains(f), label, f));
            }

            for (Field f : re.getChild().getFields()) {
                label = String.format("%s ( %s )", f.getName(), f.getType());
                lstRight.add(new RowItemSelection(re.getChildFields().contains(f), label, f));
            }

            mLeft.setData(lstLeft);
            mRight.setData(lstRight);

        } else {
            mLeft.setData(Collections.EMPTY_LIST);
            mRight.setData(Collections.EMPTY_LIST);
        }

        tbRelationshipLeft.updateUI();
        tbRelationshipRight.updateUI();
    }

    private void updateRelationType() {

        if (cbRelationship.getSelectedIndex() > -1) {
            Option opt = (Option) cbRelationship.getSelectedItem();
            RelationshipElement re = (RelationshipElement) opt.getValue();
            cbRelationshipType.setSelectedIndex(re.getType().ordinal());
        }

        cbRelationship.setEnabled(cbRelationship.getSelectedIndex() > -1);
        cbRelationshipType.setEnabled(cbRelationship.isEnabled());
        btnRemoveRelationship.setEnabled(cbRelationship.isEnabled());
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
            XMLUtil.addNewRelationship(relType, relLeft, relRight);
            relType = null;
            relRight = relLeft = null;
            mode = EditTool.SELECTOR;
        }

        loadRelationship((TableElement) el);
    }

    private void cancelRelationship() {
        relType = null;
        relRight = relLeft = null;
    }

    private ElementModel hasColision(ElementModel element) {
        ElementModel e = selectedElements[0];

        for (ElementModel el : XMLUtil.filter) {
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
        Camera.c().move(el.getPx() - canvas.getWidth() / 2, el.getPy() - canvas.getHeight() / 2);
    }

    private void positionCam(int px, int py) {
        Camera.c().move(px - 15, py - 15);
    }

    private void positionCam(MouseEvent e) {
        Camera.c().rollX(mousePos.x - e.getX());
        Camera.c().rollY(mousePos.y - e.getY());
    }

    private void exit() {
        int r = JOptionPane.showConfirmDialog(this, "Save and exit?");
        if (r == JOptionPane.CANCEL_OPTION) {
            return;
        }

        if (r == JOptionPane.OK_OPTION) {
            save();
        }

        timer.stop();
        dlgDataBase.dispose();
        this.dispose();

        System.exit(0);
    }
}
