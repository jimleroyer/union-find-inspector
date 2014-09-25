package org.jlr.ufinspector;

import ch.randelshofer.tree.*;
import ch.randelshofer.tree.demo.RGBColorizer;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;

/**
 * The UnionFind implementation for TreeViz NodeInfo.
 *
 * @since 2014/09/23
 * @author jimleroyer
 */
public class UnionFindNodeInfo implements NodeInfo {

    private UnionFindNode root;
    private EventListenerList listenerList = new EventListenerList();
    private RGBColorizer colorizer;
    private int maxDepth;
    private int maxSize;

    public UnionFindNodeInfo(UnionFindNode root, int maxSize, int maxDepth) {
        this.root = root;
        this.colorizer = new RGBColorizer();
        this.maxDepth = maxDepth;
        this.maxSize = maxSize;
    }

    @Override
    public void init(TreeNode root) {
        this.root = (UnionFindNode) root;
    }

    @Override
    public String getName(TreePath2<TreeNode> path) {
        UnionFindNode node = (UnionFindNode) path.getLastPathComponent();
        return node.getName();
    }

    @Override
    public Color getColor(TreePath2<TreeNode> path) {
        UnionFindNode node = (UnionFindNode) path.getLastPathComponent();
        long depth = node.getDepth();
        Color color = null;
        if ((depth == 0 || depth == 1) && node.children().isEmpty()) {
            color = Color.WHITE;
        } else {
            long fraction = depth / maxDepth;
            color = getColorizer().get(fraction);
        }
        return color;
    }

    @Override
    public long getWeight(TreePath2<TreeNode> path) {
        return 0;
    }

    @Override
    public long getCumulatedWeight(TreePath2<TreeNode> path) {
        return 0;
    }

    @Override
    public String getWeightFormatted(TreePath2<TreeNode> path) {
        return "";
    }

    @Override
    public String getTooltip(TreePath2<TreeNode> path) {
        UnionFindNode node = (UnionFindNode) path.getLastPathComponent();
        return "Node @ " + node.getPath();
    }

    @Override
    public Action[] getActions(TreePath2<TreeNode> path) {
        return new Action[0];
    }

    @Override
    public Image getImage(TreePath2<TreeNode> path) {
        return null;
    }

    @Override
    public Weighter getWeighter() {
        return new Weighter() {
            private UnionFindNode root;

            @Override
            public void init(TreeNode root) {
                this.root = (UnionFindNode) root;
            }

            @Override
            public float getWeight(TreePath2 path) {
                UnionFindNode node = (UnionFindNode) path.getLastPathComponent();
                // return node.getDepth();
                return 0;
            }

            @Override
            public int[] getHistogram() {
                return new int[0];
            }

            @Override
            public String getHistogramLabel(int index) {
                return "";
            }

            @Override
            public String getMinimumWeightLabel() {
                return "";
            }

            @Override
            public String getMaximumWeightLabel() {
                return "";
            }

            @Override
            public float getMedianWeight() {
                return 0;
            }
        };
    }

    @Override
    public Colorizer getColorizer() {
        return this.colorizer;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    @Override
    public void toggleColorWeighter() {
        // no-op
    }

}
