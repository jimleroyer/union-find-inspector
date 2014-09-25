union-find-inspector
====================

A tree visualizer for inspecting Disjoint-Set Data (or union-find data structure).

![A disjoint-set data representation using a sunray tree visualization](/images/union-find-sunray-400.jpg "Sunray Tree")

## Usage ##

You need Maven to build and execute the project. [Download, install it](http://maven.apache.org/) and ensure the Maven binaries are available on the classpath. Once done, change directory in the root of this clones project and execute:

```
mvn package
```

Then, change directory in the newly created *target* directory and execute the *union-find-inspector-1.0.jar* file (with double-click for example).

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

### Sunray Tree ###

![A disjoint-set data representation using a sunray tree visualization](/images/union-find-sunray-25.jpg "Sunray Tree")

### Misc ###

Labels are popping up to show the node hierarchy in the trees:

![Labels](/images/union-find-treemap-25-label.jpg "Labels")

## Coursera Students ##

You can plug-in your `Percolation` implementation into this visualizer, but it will need a few modifications. Those are necessary because the provided libraries for the class do not play well with standard Java classes. 

In more details (if you care), the provided libraries have class that reside in the default Java package. This practice make the classes impossible to import in other classes that are not in the default package. Hence I doctored the libraries with a modified version of [JarJar](https://code.google.com/p/jarjar/) of mine (another story...). The authors probably did that to make sure that their libraries would not end up in production. **Please be advised to not use them in such environment; responsibility and risks will be all yours as proper warning as been given here.**

1. Implement the `IPercolation` interface.
1. Change the package signature of your `Percolation` class from the default to `org.jlr.percolation`.
1. Name the `WeightedQuickUnionUF` class member to `percolated`.
 
Some fo these restrictions could be removed but as there are no requirements or demands for it at the moment... <:o)
