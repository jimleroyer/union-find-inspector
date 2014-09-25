package org.jlr.ufinspector;

/**
 * A placeholder class for storing the ids and weights in the union-find
 * data structure.
 *
 * @author jimleroyer
 * @since 2014/09/24
 */
public class UnionFindEntry {

    private int[] ids, weights;

    public UnionFindEntry(int[] ids, int[] weights) {
        this.ids = ids;
        this.weights = weights;
    }

    public int[] ids() {
        return ids;
    }

    public int[] weights() {
        return weights;
    }

}
