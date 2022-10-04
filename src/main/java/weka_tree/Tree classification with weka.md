## Tree classification with Weka

Decision trees are a classic supervised learning algorithms, it is easy to interpret a decision tree.
Weka (Waikato Environment for Knowledge Analysis) is a Java library implementing decision trees .

### Algorithm
The main concept behind decision tree learning is the following: starting from the training data, we will build a predictive model which is mapped to a tree structure. The goal is to achieve perfect classification with minimal number of decision

The basic algorithm for learning decision trees is:

1. starting with whole training data
2. select attribute or value along dimension that gives “best” split
3. create child nodes based on split
4. recurse on each child using child data until a stopping criterion is reached: all examples have same class or the amount of data is too small or the tree is too large.


### Example
A dataset with 8 instances includes 3 income levels. Four categories, age, education, status and occupation, determines the income level.
The resulting tree below uses education and status as split variables.

    education = bachelor: medium (3.0)
    education = highSchool
    |   status = neverMarried: low (2.0)
    |   status = married: medium (1.0)
    |   status = divorced: medium (1.0)
    education = doctorate: high (1.0)

In weka there are numerous options, if one changes the minimum number of instances per leaf node to two, following tree is derived.

    age <= 24: low (2.0)
    age > 24: medium (6.0/1.0)

It has one miss classification for age > 24.

### Speed test
The indication is that it in Java takes in the order of a millisecond to construct a tree.

### Modularity
Weka library, dependency below, is compatible with the Java module system. For ex no split 
packages according to test project with two modules. One module providing data and the other doing
classification.

     <dependency>
        <groupId>nz.ac.waikato.cms.weka</groupId>
        <artifactId>weka-stable</artifactId>
        <version>3.8.3</version>
    </dependency>

### References
More about decision trees in:

http://technobium.com/decision-trees-explained-using-weka/

https://www-users.cse.umn.edu/~kumar001/dmbook/ch4.pdf

https://pages.stat.wisc.edu/~loh/treeprogs/guide/LohISI14.pdf