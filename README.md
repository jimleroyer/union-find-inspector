union-find-inspector
====================

A tree visualizer for inspecting Disjoint-Set Data (or union-find data structure).

![A disjoint-set data representation using a sunray tree visualization](/images/union-find-sunray-400.jpg "Sunray Tree")

## File Formats ##


The visualizer supports two file formats to represent a disjoint-set data structure: a percolation entries file and an array of ids file.

### Percolation File ###

The percolation file contains the dimension and union instructions for the cell joins. It follows exactly the same format as the *Algorithms, Part 1* Coursera class:

```
5
  1   2
  1   3
  3   1
  5   1
  2   3
  3   4
  4   1
  5   5
  4   5
  2   2
  3   3
  4   3
  5   3
```

The first line contains an integer that informs of the cell table dimension. In this case above, this would be a 5X5 table. 

The following lines contains two coordinates on every line that indicates a union operation on the specified cells. The first coordinate is the X axis (horizontal) and the second one the Y axis (vertical).

### IDs File ###

This file should have one line. The line should be a copy/paste of the Java debugger output of the disjoint-set array. It should look as the following:

```
[0,1,0,0,4,5,6,0,0,9,10,26,12,0,0,15,11,17,0,19,26,26,22,26,24,26,0]
```

## Examples ##

### Hyperbolic Tree ###

![A disjoint-set data representation using a hyperbolic tree visualization](/images/union-find-hypertree-25.jpg "Hyperbolic Tree")

### Treemap ###

![A disjoint-set data representation using a treemap visualization](/images/union-find-treemap-25.jpg "Treemap")

### Misc ###

Labels are popping up to show the node hierarchy in the trees:

![Labels](/images/union-find-treemap-25-label.jpg "Labels")
