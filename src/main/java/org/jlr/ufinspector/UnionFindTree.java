package org.jlr.ufinspector;

import ch.randelshofer.tree.NodeInfo;
import ch.randelshofer.tree.TreeNode;
import ch.randelshofer.tree.demo.DemoTree;

/**
 * The Union-Find implemention for TreeViz' DemoTree interface.
 *
 * @author jimleroyer
 * @since 2014/09/23
 */
public class UnionFindTree implements DemoTree {

    private UnionFindNode root;
    private UnionFindNodeInfo nodeInfo;
    private int maxSize;
    private int maxDepth;

    public UnionFindTree(UnionFindEntry entry) {
        this.root = new UnionFindNode(entry.ids(), entry.weights());
        setMaxSize(entry.weights());
        setMaxDepth(entry.ids());
        this.nodeInfo = new UnionFindNodeInfo(this.root, maxSize, maxDepth);
    }

    @Override
    public TreeNode getRoot() {
        return root;
    }

    @Override
    public NodeInfo getInfo() {
        return nodeInfo;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxSize() {
        return maxSize;
    }

    private void setMaxDepth(int[] ids) {
        int depthCandidate = 1;
        for (int i = 0; i < ids.length; i++) {
            int currentDepth = 1;
            int index = i;
            int value = ids[i];
            while (value != index) {
                currentDepth++;
                index = value;
                value = ids[value];
            }
            if (currentDepth > depthCandidate) {
                depthCandidate = currentDepth;
            }
        }
        this.maxDepth = depthCandidate;
    }

    private void setMaxSize(int[] sizes) {
        int candidate = 0;
        for (int size : sizes) {
            if (size > candidate) {
                candidate = size;
            }
        }
        this.maxSize = candidate;
    }

}
