package org.jlr.ufinspector;

import ch.randelshofer.tree.TreeNode;

import java.util.*;

/**
 * A TreeViz node implementation for the UnionFind data structure.
 *
 * This data structure should be a disjoint-set data structure represented by
 * an array of int primitives. The index of the array represents the current
 * value whereas the array value represents its parent, from which we can
 * build one or several trees out of the flattened values.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Disjoint-set_data_structure">Disjoind-set</a>
 * @see <a href="http://www.randelshofer.ch/treeviz/index.html">TreeViz</a>
 *
 * @author jlr
 * @since 2014/09/18
 */
public class UnionFindNode implements TreeNode {

    private String name;
    private int position;
    private UnionFindNode parent;
    private List<TreeNode> children = new ArrayList<>();

    public UnionFindNode(int[] ids, int[] sz) {
        this.name = "Virtual";
        this.parent = null;
        this.position = -1;
        buildNodeTree(ids, sz, this);
    }

    public UnionFindNode(int position, UnionFindNode parentNode) {
        this.name = Integer.toString(position);
        this.position = position;
        this.parent = parentNode;
        parentNode.children.add(this);
    }

    @Override
    public List<TreeNode> children() {
        return children;
    }

    @Override
    public boolean getAllowsChildren() {
        return ! children.isEmpty();
    }

    public String getName() {
        return this.name;
    }

    public UnionFindNode getParent() {
        return this.parent;
    }

    public String getPath() {
        List<String> paths = new ArrayList<String>();
        paths.add(getName());
        UnionFindNode parent = getParent();
        while (parent != null) {
            paths.add(parent.getName());
            parent = parent.getParent();
        }
        // Reverse the path from parent to leaf and remove the virtual node
        // in the path only if it's not the virtual node itself.
        Collections.reverse(paths);
        if (paths.size() != 1) {
            paths.remove(0);
        }
        return String.join(" -> ", paths);
    }

    public long getDepth() {
        int depth = 0;
        UnionFindNode parent = getParent();
        while (parent != null) {
            depth++;
            parent = parent.getParent();
        }
        return depth;
    }

    private static UnionFindNode buildNode(int id, UnionFindNode parent, Map<Integer, UnionFindNode> builtNodes) {
        UnionFindNode currentNode = new UnionFindNode(id, parent);
        builtNodes.put(id, currentNode);
        return currentNode;
    }

    private static UnionFindNode buildNodeTree(int id, int[] ids, Map<Integer, UnionFindNode> builtNodes, UnionFindNode root) {
        int parentId = ids[id];
        UnionFindNode parent = builtNodes.get(parentId);
        if (parent == null) {
            if (parentId == id) {
                return buildNode(parentId, root, builtNodes);
            } else {
                parent = buildNodeTree(parentId, ids, builtNodes, root);
            }
        } else if (parentId == id) {
            return parent;
        }

        return buildNode(id, parent, builtNodes);
    }

    private static UnionFindNode buildNodeTree(int[] ids, int[] sz, UnionFindNode root) {
        Map<Integer, UnionFindNode> nodes = new HashMap<Integer, UnionFindNode>();
        for (int position = 0; position < ids.length; position++) {
            buildNodeTree(position, ids, nodes, root);
        }
        return root;
    }

}
