package org.jlr.ufinspector;

import ch.randelshofer.gui.ProgressView;
import ch.randelshofer.text.FileSizeFormat;
import ch.randelshofer.tree.NodeInfo;
import ch.randelshofer.tree.TreeNode;
import ch.randelshofer.tree.TreePath2;
import ch.randelshofer.tree.TreeView;
import ch.randelshofer.tree.circlemap.CirclemapModel;
import ch.randelshofer.tree.circlemap.CirclemapView;
import ch.randelshofer.tree.demo.DemoTree;
import ch.randelshofer.tree.demo.JHistogram;
import ch.randelshofer.tree.demo.TreevizFileSystemXMLNode;
import ch.randelshofer.tree.demo.TreevizFileSystemXMLNodeInfo;
import ch.randelshofer.tree.hypertree.HyperTree;
import ch.randelshofer.tree.hypertree.SwingHTView;
import ch.randelshofer.tree.rectmap.RectmapView;
import ch.randelshofer.tree.sunburst.*;
import ch.randelshofer.tree.sunray.*;
import ch.randelshofer.util.Methods;
import ch.randelshofer.util.Worker;
import ch.randelshofer.util.prefs.PreferencesUtil2;
import com.coursera.algs4.WeightedQuickUnionUF;
import com.coursera.stdlib.In;
import com.google.common.io.Files;
import org.jlr.percolation.IPercolation;
import org.jlr.percolation.Percolation;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author werni
 * @version 1.1.1 2011-08-19 Shows an error dialog when the tree structure could
 * not be created. <br>1.0.1 2009-01-28 Fixed tooltip enabling. <br>1.0
 * 2007-09-16 Created.
 */
public class UnionFindVisualizer extends JFrame {

    private TreeView treeView;
    private JFileChooser fileChooser;
    private TreeNode rootNode;
    private Preferences prefs;
    private NodeInfo info;
    private DropTargetListener dropHandler = new DropTargetListener() {

        /**
         * Called when a drag operation has encountered the
         * <code>DropTarget</code>. <P>
         *
         * @param event the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dragEnter(DropTargetDragEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                event.rejectDrag();
            }
        }

        /**
         * The drag operation has departed the
         * <code>DropTarget</code> without dropping. <P>
         *
         * @param event the <code>DropTargetEvent</code>
         */
        @Override
        public void dragExit(DropTargetEvent event) {
            // Nothing to do
        }

        /**
         * Called when a drag operation is ongoing on the
         * <code>DropTarget</code>. <P>
         *
         * @param event the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dragOver(DropTargetDragEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                event.rejectDrag();
            }
        }

        /**
         * The drag operation has terminated with a drop on this
         * <code>DropTarget</code>. This method is responsible for undertaking
         * the transfer of the data associated with the gesture. The
         * <code>DropTargetDropEvent</code> provides a means to obtain a
         * <code>Transferable</code> object that represents the data object(s)
         * to be transfered.<P> From this method, the
         * <code>DropTargetListener</code> shall accept or reject the drop via
         * the acceptDrop(int dropAction) or rejectDrop() methods of the
         * <code>DropTargetDropEvent</code> parameter. <P> Subsequent to
         * acceptDrop(), but not before,
         * <code>DropTargetDropEvent</code>'s getTransferable() method may be
         * invoked, and data transfer may be performed via the returned
         * <code>Transferable</code>'s getTransferData() method. <P> At the
         * completion of a drop, an implementation of this method is required to
         * signal the success/failure of the drop by passing an appropriate
         * <code>boolean</code> to the
         * <code>DropTargetDropEvent</code>'s dropComplete(boolean success)
         * method. <P> Note: The actual processing of the data transfer is not
         * required to finish before this method returns. It may be deferred
         * until later. <P>
         *
         * @param event the <code>DropTargetDropEvent</code>
         */
        @Override
        public void drop(DropTargetDropEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY);

                try {
                    java.util.List<File> files = (java.util.List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (files.size() == 1) {
                        File file = files.get(0);
                        openFile(file);
                    }
                } catch (IOException e) {
                    JOptionPane.showConfirmDialog(
                            UnionFindVisualizer.this,
                            "Could not access the dropped data.",
                            "Visualization: Drop Failed",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                } catch (UnsupportedFlavorException e) {
                    JOptionPane.showConfirmDialog(
                            UnionFindVisualizer.this,
                            "Unsupported data flavor.",
                            "Visualization: Drop Failed",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                event.rejectDrop();
            }
        }

        /**
         * Called if the user has modified the current drop gesture. <P>
         *
         * @param event the <code>DropTargetDragEvent</code>
         */
        @Override
        public void dropActionChanged(DropTargetDragEvent event) {
            // Nothing to do
        }
    };

    /**
     * Creates new form Main.
     */
    public UnionFindVisualizer() {
        initComponents();
        setSize(400, 400);
        prefs = PreferencesUtil2.userNodeForPackage(getClass());

        String preferredView = prefs.get("viewAs", "hyperbolic");
        for (Enumeration<AbstractButton> e = viewAsButtonGroup.getElements(); e.hasMoreElements(); ) {
            AbstractButton b = e.nextElement();
            b.setSelected(preferredView.equals(b.getActionCommand()));
        }

        multilineLabelsRadio.setSelected(prefs.getBoolean("multilineLabels", false));
        toolTipEnabledRadio.setSelected(prefs.getBoolean("toolTipEnabled", true));

        updateMaxDepth();

        new DropTarget(this, dropHandler);
        new DropTarget(getContentPane(), dropHandler);
        new DropTarget(viewPanel, dropHandler);
        statusPanel.setVisible(false);
    }

    private void updateView() {
        // remove view panel so that memory can be freed
        if (rootNode == null) {
            statusPanel.setVisible(false);

        } else {
            statusPanel.setVisible(true);
            if (viewAsHypertreeRadio.isSelected()) {
                updateHTView();
            } else if (viewAsSunburstRadio.isSelected()) {
                updateSBView();
            } else if (viewAsSunrayRadio.isSelected()) {
                updateScBView();
            } else if (viewAsIcicleRadio.isSelected()) {
                updateIcView();
            } else if (viewAsIcerayRadio.isSelected()) {
                updateIdView();
            } else if (viewAsCircleMapRadio.isSelected()) {
                updateCMView();
            }
        }
        validate();
        repaint();
    }

    private void updateToolTipEnabled() {
        if (treeView != null) {
            treeView.setToolTipEnabled(prefs.getBoolean("toolTipEnabled", true));
        }
    }

    private void updateMaxDepth() {
        maxDepthPanel.setVisible((treeView instanceof RectmapView) || (treeView instanceof CirclemapView));
        maxDepthPanel.revalidate();
        if (treeView != null) {
            treeView.setMaxDepth(prefs.getInt("maxDepth", Integer.MAX_VALUE));
        }
        switch (prefs.getInt("maxDepth", Integer.MAX_VALUE)) {
            case 1:
                maxDepth1Radio.setSelected(true);
                break;
            default:
                maxDepthInfinityRadio.setSelected(true);
                break;
        }
    }

    private void updateSBView() {
        final ProgressView p = new ProgressView("Sunburst Tree", "Calculating layout...");
        p.setIndeterminate(true);
        viewPanel.removeAll();
        Worker worker = new Worker() {
            @Override
            public Object construct() {
                SunburstModel sunbursttree = new SunburstModel(rootNode, info);
                return sunbursttree;
            }

            @Override
            public void done(Object o) {
                SunburstModel model = (SunburstModel) o;
                SunburstView view = model.getView();
                treeView = view;
                //  view.setFont(new Font("Dialog", Font.PLAIN, 9));
                histogram.setWeighter(model.getInfo().getWeighter());
                histogram.setColorizer(model.getInfo().getColorizer());
                viewPanel.removeAll();
                treeView = view;
                if (info instanceof TreevizFileSystemXMLNodeInfo) {
                    JSplitPane splitPane = createSplitPane(view);
                    viewPanel.add(splitPane);
                    validate();
                    splitPane.setDividerLocation(1d);

                } else {
                    view.setToolTipEnabled(true);
                    viewPanel.add(view);
                    validate();
                }
                updateToolTipEnabled();
                updateMaxDepth();
                new DropTarget(view, dropHandler);
                repaint();
            }

            @Override
            public void finished() {
                p.close();
            }
        };
        worker.start();
    }

    private void updateScBView() {
        final ProgressView p = new ProgressView("Sunray Tree", "Calculating layout...");
        p.setIndeterminate(true);
        viewPanel.removeAll();
        Worker worker = new Worker() {
            @Override
            public Object construct() {
                SunrayModel scatterbursttree = new SunrayModel(rootNode, info);
                return scatterbursttree;
            }

            @Override
            public void done(Object o) {
                SunrayModel model = (SunrayModel) o;
                SunrayView view = model.getView();
                treeView = view;

                //  view.setFont(new Font("Dialog", Font.PLAIN, 9));
                histogram.setWeighter((model.getInfo().getWeighter()));
                histogram.setColorizer((model.getInfo().getColorizer()));
                viewPanel.removeAll();
                if (info instanceof TreevizFileSystemXMLNodeInfo) {
                    JSplitPane splitPane = createSplitPane(view);
                    viewPanel.add(splitPane);
                    validate();
                    splitPane.setDividerLocation(1d);

                } else {
                    view.setToolTipEnabled(true);
                    viewPanel.add(view);
                    validate();
                }
                updateToolTipEnabled();
                updateMaxDepth();
                new DropTarget(view, dropHandler);
                repaint();
            }

            @Override
            public void finished() {
                p.close();
            }
        };
        worker.start();
    }

    private void updateHTView() {
        final ProgressView p = new ProgressView("Hyperbolic Tree", "Calculating layout...");
        p.setIndeterminate(true);
        viewPanel.removeAll();
        Worker worker = new Worker() {
            @Override
            public Object construct() {

                HyperTree tree = new HyperTree(rootNode, info);
                return tree;
            }

            @Override
            public void done(Object o) {
                HyperTree model = (HyperTree) o;
                SwingHTView view = model.getView();
                treeView = view;
                // view.setFont(new Font("Dialog", Font.PLAIN, 9));
                histogram.setWeighter((model.getInfo().getWeighter()));
                histogram.setColorizer((model.getInfo().getColorizer()));
                viewPanel.removeAll();
                if (info instanceof TreevizFileSystemXMLNodeInfo) {
                    JSplitPane splitPane = createSplitPane(view);
                    viewPanel.add(splitPane);
                    validate();
                    splitPane.setDividerLocation(1d);

                } else {
                    view.setToolTipEnabled(true);
                    viewPanel.add(view);
                    validate();
                }
                updateToolTipEnabled();
                updateMaxDepth();
                new DropTarget(view, dropHandler);
                repaint();
            }

            @Override
            public void finished() {
                p.close();
            }
        };
        worker.start();
    }

    private void updateCMView() {
        final ProgressView p = new ProgressView("Circular Treemap", "Initializing...");
        p.setCancelable(true);
        p.setIndeterminate(true);
        viewPanel.removeAll();
        Worker worker = new Worker() {
            @Override
            public Object construct() {
                CirclemapModel model = new CirclemapModel(rootNode, info, p);
                return model;
            }

            @Override
            public void done(Object o) {
                if (p.isCanceled()) {
                    return;
                }
                CirclemapModel model = (CirclemapModel) o;
                CirclemapView view = model.getView();
                treeView = view;
                // view.setFont(new Font("Dialog", Font.PLAIN, 9));
                histogram.setWeighter(model.getInfo().getWeighter());
                histogram.setColorizer(model.getInfo().getColorizer());
                viewPanel.removeAll();

                if (info instanceof TreevizFileSystemXMLNodeInfo) {
                    JSplitPane splitPane = createSplitPane(view);
                    viewPanel.add(splitPane);
                    validate();
                    splitPane.setDividerLocation(1d);

                } else {
                    view.setToolTipEnabled(true);
                    viewPanel.add(view);
                    validate();
                }
                updateToolTipEnabled();
                updateMaxDepth();
                new DropTarget(view, dropHandler);
                repaint();
            }

            @Override
            public void finished() {
                p.close();
            }
        };
        worker.start();
    }

    private void updateIcView() {
        final boolean isMultiline = prefs.getBoolean("multilineLabels", false);
        if (treeView instanceof IcicleView) {
            IcicleView rv = (IcicleView) treeView;
            rv.setDraw(isMultiline ?//
                    new MultilineIcicleDraw(rv.getModel()) ://
                    new IcicleDraw(rv.getModel())//
            );
            rv.repaint();
            return;
        }
        final ProgressView p = new ProgressView("Icicle Tree", "Calculating layout...");
        p.setIndeterminate(true);
        viewPanel.removeAll();
        Worker worker = new Worker() {
            @Override
            public Object construct() {
                IcicleModel tree = new IcicleModel(rootNode, info);
                return tree;
            }

            @Override
            public void done(Object o) {
                IcicleModel model = (IcicleModel) o;
                IcicleView view = model.getView();
                view.setDraw(isMultiline ?//
                        new MultilineIcicleDraw(view.getModel()) ://
                        new IcicleDraw(view.getModel())//
                );
                treeView = view;
                //  view.setFont(new Font("Dialog", Font.PLAIN, 9));
                histogram.setWeighter(model.getInfo().getWeighter());
                histogram.setColorizer(model.getInfo().getColorizer());
                viewPanel.removeAll();
                if (info instanceof TreevizFileSystemXMLNodeInfo) {
                    JSplitPane splitPane = createSplitPane(view);
                    viewPanel.add(splitPane);
                    validate();
                    splitPane.setDividerLocation(1d);

                } else {
                    view.setToolTipEnabled(true);
                    viewPanel.add(view);
                    validate();
                }
                updateToolTipEnabled();
                updateMaxDepth();
                new DropTarget(view, dropHandler);
                repaint();
            }

            @Override
            public void finished() {
                p.close();
            }
        };
        worker.start();
    }

    private void updateIdView() {
        final boolean isMultiline = prefs.getBoolean("multilineLabels", false);
        if (treeView instanceof IcerayView) {
            IcerayView rv = (IcerayView) treeView;
            rv.setDraw(isMultiline ?//
                    new MultilineIcerayDraw(rv.getModel()) ://
                    new IcerayDraw(rv.getModel())//
            );
            rv.repaint();
            return;
        }
        final ProgressView p = new ProgressView("Iceray Tree", "Calculating layout...");
        p.setIndeterminate(true);
        viewPanel.removeAll();
        Worker worker = new Worker() {
            @Override
            public Object construct() {
                IcerayModel tree = new IcerayModel(rootNode, info);
                return tree;
            }

            @Override
            public void done(Object o) {
                IcerayModel model = (IcerayModel) o;
                IcerayView view = model.getView();
                view.setDraw(isMultiline ?//
                        new MultilineIcerayDraw(view.getModel()) ://
                        new IcerayDraw(view.getModel())//
                );
                treeView = view;
                //  view.setFont(new Font("Dialog", Font.PLAIN, 9));
                histogram.setWeighter(model.getInfo().getWeighter());
                histogram.setColorizer(model.getInfo().getColorizer());
                viewPanel.removeAll();
                if (info instanceof TreevizFileSystemXMLNodeInfo) {
                    JSplitPane splitPane = createSplitPane(view);
                    viewPanel.add(splitPane);
                    validate();
                    splitPane.setDividerLocation(1d);

                } else {
                    view.setToolTipEnabled(true);
                    viewPanel.add(view);
                    validate();
                }
                updateToolTipEnabled();
                updateMaxDepth();
                new DropTarget(view, dropHandler);
                repaint();
            }

            @Override
            public void finished() {
                p.close();
            }
        };
        worker.start();
    }

    private JSplitPane createSplitPane(Component view) {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOneTouchExpandable(true);
        splitPane.setLeftComponent(view);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Table", createTablePanel(view));
        tabbedPane.add("Info", createInfoPanel(view));

        splitPane.setRightComponent(tabbedPane);
        return splitPane;
    }

    private JComponent createTablePanel(Component view) {
        final JTable table = new JTable();
        // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableModel tm = ((TreevizFileSystemXMLNodeInfo) info).getUserTable();
        table.setModel(tm);
        Methods.invokeIfExists(table, "setAutoCreateRowSorter", true);


        table.setDefaultRenderer(Long.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Long) {
                    value = FileSizeFormat.getInstance().format((Long) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    HashMap<String, TreevizFileSystemXMLNode> selectedUsers = new HashMap<String, TreevizFileSystemXMLNode>();
                    TreevizFileSystemXMLNodeInfo.InfoTableModel model = (TreevizFileSystemXMLNodeInfo.InfoTableModel) table.getModel();
                    ListSelectionModel lsm = table.getSelectionModel();
                    for (int i = 0, n = model.getRowCount(); i < n; i++) {
                        if (lsm.isSelectedIndex(i)) {
                            TreevizFileSystemXMLNode user;
                            try {
                                user = model.getRowObject((Integer) Methods.invoke(Methods.invoke(table, "getRowSorter"), "convertRowIndexToModel", i));
                            } catch (NoSuchMethodException ex) {
                                user = model.getRowObject(i);
                            }
                            selectedUsers.put((String) user.getAttribute("id"), user);
                        }
                    }
                    ((TreevizFileSystemXMLNodeInfo) info).setSelectedUsers(selectedUsers);

                }
            }
        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setMinimumSize(new Dimension(0, 0));
        scrollPane.setViewportView(table);
        return scrollPane;
    }

    private JComponent createInfoPanel(Component view) {
        final JLabel infoLabel = new JLabel();
        infoLabel.setVerticalAlignment(JLabel.TOP);
        if (view instanceof TreeView) {
            final TreeView treeView = (TreeView) view;
            view.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent evt) {
                    infoLabel.setText(treeView.getInfoText(evt));
                }
            });
        }

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setMinimumSize(new Dimension(0, 0));
        scrollPane.setViewportView(infoLabel);
        return scrollPane;
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (treeView != null) {
            ((Component) treeView).setEnabled(b);

        }
        if (histogram != null) {
            histogram.setEnabled(b);
        }
        if (maxDepth1Radio != null) {
            maxDepth1Radio.setEnabled(b);
            maxDepthInfinityRadio.setEnabled(b);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        viewAsButtonGroup = new ButtonGroup();
        maxDepthButtonGroup = new ButtonGroup();
        viewPanel = new JPanel();
        statusPanel = new JPanel();
        histogram = new JHistogram();
        maxDepthPanel = new JPanel();
        maxDepthInfinityRadio = new JRadioButton();
        maxDepth1Radio = new JRadioButton();
        jMenuBar1 = new JMenuBar();
        fileMenu = new JMenu();
        openFileMenuItem = new JMenuItem();
        printMenuItem = new JMenuItem();
        viewMenu = new JMenu();
        viewAsHypertreeRadio = new JRadioButtonMenuItem();
        viewAsSunburstRadio = new JRadioButtonMenuItem();
        viewAsSunrayRadio = new JRadioButtonMenuItem();
        viewAsIcicleRadio = new JRadioButtonMenuItem();
        viewAsIcerayRadio = new JRadioButtonMenuItem();
        viewAsCircleMapRadio = new JRadioButtonMenuItem();
        jSeparator1 = new JSeparator();
        multilineLabelsRadio = new JCheckBoxMenuItem();
        toolTipEnabledRadio = new JCheckBoxMenuItem();
        helpMenu = new JMenu();
        aboutMenuItem = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tree Visualizer");

        viewPanel.setLayout(new BorderLayout());
        getContentPane().add(viewPanel, BorderLayout.CENTER);

        statusPanel.setLayout(new GridBagLayout());

        histogram.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                toggleHistogram(evt);
            }
        });
        histogram.setLayout(new FlowLayout());
        statusPanel.add(histogram, new GridBagConstraints());

        maxDepthPanel.setLayout(new GridBagLayout());

        maxDepthButtonGroup.add(maxDepthInfinityRadio);
        maxDepthInfinityRadio.setText("Show full depth");
        maxDepthInfinityRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxDepthRadioPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        maxDepthPanel.add(maxDepthInfinityRadio, gridBagConstraints);

        maxDepthButtonGroup.add(maxDepth1Radio);
        maxDepth1Radio.setText("Show current depth only");
        maxDepth1Radio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxDepthRadioPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        maxDepthPanel.add(maxDepth1Radio, gridBagConstraints);

        statusPanel.add(maxDepthPanel, new GridBagConstraints());

        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        fileMenu.setText("File");

        openFileMenuItem.setText("Open File...");
        openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFile(evt);
            }
        });
        fileMenu.add(openFileMenuItem);

        printMenuItem.setText("Print");
        printMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printPerformed(evt);
            }
        });
        fileMenu.add(printMenuItem);

        jMenuBar1.add(fileMenu);

        viewMenu.setText("View");

        viewAsButtonGroup.add(viewAsHypertreeRadio);
        viewAsHypertreeRadio.setSelected(true);
        viewAsHypertreeRadio.setText("Hyperbolic Tree");
        viewAsHypertreeRadio.setActionCommand("hyperbolic");
        viewAsHypertreeRadio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewAsItemChanged(evt);
            }
        });
        viewMenu.add(viewAsHypertreeRadio);

        viewAsButtonGroup.add(viewAsSunburstRadio);
        viewAsSunburstRadio.setText("Sunburst Tree");
        viewAsSunburstRadio.setActionCommand("sunburst");
        viewAsSunburstRadio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewAsItemChanged(evt);
            }
        });
        viewMenu.add(viewAsSunburstRadio);

        viewAsButtonGroup.add(viewAsSunrayRadio);
        viewAsSunrayRadio.setText("Sunray Tree");
        viewAsSunrayRadio.setActionCommand("sunray");
        viewAsSunrayRadio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewAsItemChanged(evt);
            }
        });
        viewMenu.add(viewAsSunrayRadio);

        viewAsButtonGroup.add(viewAsIcicleRadio);
        viewAsIcicleRadio.setText("Icicle Tree");
        viewAsIcicleRadio.setActionCommand("icicle");
        viewAsIcicleRadio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewAsItemChanged(evt);
            }
        });
        viewMenu.add(viewAsIcicleRadio);

        viewAsButtonGroup.add(viewAsIcerayRadio);
        viewAsIcerayRadio.setText("Iceray Tree");
        viewAsIcerayRadio.setActionCommand("iceray");
        viewAsIcerayRadio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewAsItemChanged(evt);
            }
        });
        viewMenu.add(viewAsIcerayRadio);

        viewAsButtonGroup.add(viewAsCircleMapRadio);
        viewAsCircleMapRadio.setText("Circular Treemap");
        viewAsCircleMapRadio.setActionCommand("circlemap");
        viewAsCircleMapRadio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                viewAsItemChanged(evt);
            }
        });
        viewMenu.add(viewAsCircleMapRadio);

        viewMenu.add(jSeparator1);

        multilineLabelsRadio.setText("Multiline Labels");
        multilineLabelsRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multilineLabelsPerformed(evt);
            }
        });
        viewMenu.add(multilineLabelsRadio);

        toolTipEnabledRadio.setSelected(true);
        toolTipEnabledRadio.setText("Show Tooltips");
        toolTipEnabledRadio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tooltipsEnabledPerformed(evt);
            }
        });
        viewMenu.add(toolTipEnabledRadio);

        jMenuBar1.add(viewMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                about(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }

    private void openFile(java.awt.event.ActionEvent evt) {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            if (prefs.get("file", null) != null) {
                fileChooser.setSelectedFile(new File(prefs.get("file", null)));
            }
        }
        if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(this)) {
            prefs.put("file", fileChooser.getSelectedFile().toString());
            openFile(fileChooser.getSelectedFile());
        }
    }

    private void openFile(File file) {
        UnionFindEntry tempEntry = null;
        String ext = Files.getFileExtension(file.getName());
        if ("perc".equalsIgnoreCase(ext)) {
            tempEntry = parsePercolationFile(file);
        } else if ("uf".equalsIgnoreCase(ext)) {
            tempEntry = parseUfFile(file);
        } else {
            JOptionPane.showMessageDialog(UnionFindVisualizer.this, "Select a file with 'perc' or 'uf' extension.", "Open File...", JOptionPane.ERROR_MESSAGE);
            return;
        }
        final UnionFindEntry entry = tempEntry;

        new Worker<DemoTree>() {
            @Override
            public DemoTree construct() throws Exception {
                DemoTree tree = new UnionFindTree(entry);
                return tree;
            }

            @Override
            public void done(DemoTree result) {
                rootNode = ((DemoTree) result).getRoot();
                info = ((DemoTree) result).getInfo();
                setTitle("Tree Visualizer: " + info.getName(new TreePath2(rootNode)));
                treeView = null; // remove tree view
                updateView();
            }

            @Override
            public void failed(Throwable t) {
                String msg = t.getMessage();
                if (msg == null) {
                    msg = "";
                }
                if (t.getCause() != null && t.getCause().getMessage() != null) {

                    msg += "\n" + t.getCause().getMessage();
                }

                JOptionPane.showMessageDialog(UnionFindVisualizer.this, "Could not create tree structure.\n" + msg, "Visualizer", JOptionPane.ERROR_MESSAGE);
                t.printStackTrace();
            }
        }.start();
    }

    private UnionFindEntry parsePercolationFile(File file) {
        In in = new In(file);
        int dimension = in.readInt(); // N-by-N percolation system
        IPercolation percolation = new Percolation(dimension);

        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            percolation.open(i, j);
            System.out.println("Percolation: " + i + ", " + j);
        }
        WeightedQuickUnionUF uf = null;
        try {
            Field fPerfolated = Percolation.class.getDeclaredField("percolated");
            fPerfolated.setAccessible(true);
            uf = (WeightedQuickUnionUF) fPerfolated.get(percolation);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not get 'percolated' field.");
        }
        return new UnionFindEntry(getDisjointDataSet(uf), getSizes(uf));
    }

    /**
     * Parses an array dump as a string, right from the debug value extraction.
     *
     * "Sometimes, the elegant implementation is just a function.
     *  Not a method.  Not a class.  Not a framework.  Just a function."
     *                                              -- John Carmack
     */
    private UnionFindEntry parseUfFile(File file) {
        In in = new In(file);
        java.util.List<Integer> array = new ArrayList<>();
        Pattern ARRAY_PATTERN = Pattern.compile("\\d+");

        String line = in.readLine();
        Matcher matches = ARRAY_PATTERN.matcher(line);
        while (matches.find()) {
            int match = new Integer(matches.group());
            array.add(match);
        }
        int[] szs = array.stream().mapToInt(i -> i).toArray();
        // TODO: Determine the weights of each nodes ourselves.
        return new UnionFindEntry(szs, new int[szs.length]);
    }

    private void about(java.awt.event.ActionEvent evt) {
        FileSizeFormat byteFormat = FileSizeFormat.getInstance();

        // for copying style
        JLabel label = new JLabel();
        Font font = label.getFont();

        StringBuffer style = new StringBuffer("font-family:" + font.getFamily() + ";");
        style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
        style.append("font-size:" + font.getSize() + "pt;");

        JEditorPane editorPane = new JEditorPane("text/html",
                "<html>Disjoint Data Set Visualizer 1.0.0<br>"
                        + "by Jimmy Royer (<a href='mailto:jimleroyer@gmail.com'>jimleroyer@gmail.com</a>)<br><br>"
                        + "Powered by <a href='http://www.randelshofer.ch/treeviz/'>TreeViz</a><br>"
                        + "Copyright © 2007-2011<br>"
                        + "Werner Randelshofer<br>"
                        + "All rights reserved."
                        + "<br><br>"
                        + "Licensed with <a href='http://opensource.org/licenses/MIT'>MIT License</a>"
                        + "<br><br>"
                        + "Running on:<br>"
                        + System.getProperty("os.name") + " " + System.getProperty("os.version") + " "
                        + System.getProperty("os.arch")
                        + "<br>JavaVM "
                        + System.getProperty("java.vm.version")
                        + "<br>"
                        + "<br>Memory "
                        + "<br>  Max.: "
                        + byteFormat.format(Runtime.getRuntime().maxMemory())
                        + ", Usage: "
                        + byteFormat.format(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                        + "</html>");
        // handle link events
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException e1) {
                        System.err.println("Could not open URI in the desktop's browser: " + e.getURL());
                    }
            }
        });
        editorPane.setEditable(false);
        editorPane.setBackground(label.getBackground());
        JOptionPane.showMessageDialog(this, editorPane);
    }

    private void viewAsItemChanged(java.awt.event.ItemEvent evt) {
        String view = viewAsButtonGroup.getSelection().getActionCommand();
        if (view != null) {
            prefs.put("viewAs", view);
            updateView();
        }
    }

    private void tooltipsEnabledPerformed(java.awt.event.ActionEvent evt) {
        prefs.putBoolean("toolTipEnabled", toolTipEnabledRadio.isSelected());
        updateToolTipEnabled();
    }

    private void maxDepthRadioPerformed(java.awt.event.ActionEvent evt) {

        prefs.putInt("maxDepth", maxDepth1Radio.isSelected() ? 1 : Integer.MAX_VALUE);
        updateMaxDepth();

    }

    private void toggleHistogram(MouseEvent evt) {
        if (info != null) {
            setEnabled(false);
            new Worker() {
                @Override
                protected Object construct() throws Exception {
                    info.toggleColorWeighter();
                    return null;
                }

                @Override
                protected void finished() {
                    histogram.setColorizer(info.getColorizer());
                    histogram.setWeighter(info.getWeighter());
                    histogram.repaint();
                    if (treeView != null) {
                        treeView.repaintView();
                    }
                    setEnabled(true);

                }
            }.start();
        }
    }

    private void multilineLabelsPerformed(java.awt.event.ActionEvent evt) {
        boolean isMultiline = multilineLabelsRadio.isSelected();


        prefs.putBoolean("multilineLabels", isMultiline);
        updateView();


    }

    private void printPerformed(java.awt.event.ActionEvent evt) {
        Paper paper = new Paper();
        paper.setSize(21 / 2.54 * 72, 29.7 / 2.54 * 72);
        paper.setImageableArea(1 / 2.54 * 72, 1 / 2.54 * 72, (21 - 2) / 2.54 * 72, (29.7 - 2) / 2.54 * 72);
        final PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);

        Pageable pageable = new Pageable() {
            @Override
            public int getNumberOfPages() {
                return 1;
            }

            @Override
            public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
                return pageFormat;
            }

            @Override
            public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
                return new Printable() {
                    @Override
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                        if (pageIndex != 0) {
                            return Printable.NO_SUCH_PAGE;
                        }
                        JComponent c = (JComponent) treeView;
                        Rectangle r = c.getBounds();
                        c.setBounds((int) pageFormat.getImageableX(),
                                (int) pageFormat.getImageableY(),
                                (int) pageFormat.getImageableWidth(),
                                (int) pageFormat.getImageableHeight());
                        treeView.repaintView();
                        graphics.translate((int) pageFormat.getImageableX(),
                                (int) pageFormat.getImageableY());
                        c.print(graphics);
                        graphics.translate(-(int) pageFormat.getImageableX(),
                                -(int) pageFormat.getImageableY());
                        c.setBounds(r);

                        return Printable.PAGE_EXISTS;
                    }
                };
            }
        };


        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            // FIXME - PrintRequestAttributeSet should be retrieved from View
            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new PrinterResolution(300, 300, PrinterResolution.DPI));
            job.setPageable(pageable);
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    String message = (e.getMessage() == null) ? e.toString() : e.getMessage();
                    JOptionPane.showMessageDialog(this,
                            "<html>" + UIManager.getString("OptionPane.css")
                                    + "<b>" + "couldn't print" + "</b><br>"
                                    + ((message == null) ? "" : message));
                }
            } else {
                System.out.println("JOB ABORTED!");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Supports the following command line parameters:
     * <pre>
     * filename -weight weightAttribute -color colorAttribute
     * </pre>
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // UIManager does the right thing for us
                }

                ToolTipManager.sharedInstance().setDismissDelay(60000); // display tooltip for 10 minutes
                new UnionFindVisualizer().setVisible(true);
            }
        });
    }

    private int[] getArrayMember(Class klass, Object instance, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field idField = klass.getDeclaredField(fieldName);
        idField.setAccessible(true);
        return (int[]) idField.get(instance);
    }

    private int[] getSizes(WeightedQuickUnionUF unionFind) {
        // We need to introspect the UnionFind object to get its internal data structure,
        // it's unfortunately the only way to efficiently discover its directed tree.
        try {
            return getArrayMember(WeightedQuickUnionUF.class, unionFind, "sz");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Not the best exception management, but should work for our short-term purposes.
            throw new RuntimeException("Could not access the 'sz' field via reflection.");
        }
    }

    private int[] getDisjointDataSet(WeightedQuickUnionUF unionFind) {
        // We need to introspect the UnionFind object to get its internal data structure,
        // it's unfortunately the only way to efficiently discover its directed tree.
        try {
            return getArrayMember(WeightedQuickUnionUF.class, unionFind, "id");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Not the best exception management, but should work for our short-term purposes.
            throw new RuntimeException("Could not access the 'id' field via reflection.");
        }
    }

    // Variables declaration
    private JMenuItem aboutMenuItem;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JHistogram histogram;
    private JMenuBar jMenuBar1;
    private JSeparator jSeparator1;
    private JRadioButton maxDepth1Radio;
    private ButtonGroup maxDepthButtonGroup;
    private JRadioButton maxDepthInfinityRadio;
    private JPanel maxDepthPanel;
    private JCheckBoxMenuItem multilineLabelsRadio;
    private JMenuItem openFileMenuItem;
    private JMenuItem printMenuItem;
    private JPanel statusPanel;
    private JCheckBoxMenuItem toolTipEnabledRadio;
    private ButtonGroup viewAsButtonGroup;
    private JRadioButtonMenuItem viewAsCircleMapRadio;
    private JRadioButtonMenuItem viewAsHypertreeRadio;
    private JRadioButtonMenuItem viewAsIcerayRadio;
    private JRadioButtonMenuItem viewAsIcicleRadio;
    private JRadioButtonMenuItem viewAsSunburstRadio;
    private JRadioButtonMenuItem viewAsSunrayRadio;
    private JMenu viewMenu;
    private JPanel viewPanel;
}
