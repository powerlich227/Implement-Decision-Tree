

=======================================================================
Steps to compile and run the java project:

javac DecisionTree.java
javac Calculation.java

java DecisionTree 20 50 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 10 50 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 20 20 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 12 50 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 30 25 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 25 50 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 30 40 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 10 20 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 20 30 training_set.csv validation_set.csv test_set.csv False
java DecisionTree 15 50 training_set.csv validation_set.csv test_set.csv False

java DecisionTree 20 50 training_set.csv validation_set.csv test_set.csv True


=======================================================================

Explanation: 
1. When compile, the toPrint argument is "False", the output only include the accuracy of the tree before post-pruning based on "Information Gain" and "Variance Impurity" heuristic functions

2. If the toPrint argument is "True", the ouput include the trees before and after the post-pruning based on "Information Gain" and "Variance Impurity" heuristic function. 

3. Chose 10 suitable values for L and K , report the accuracy of the tree before and after post-pruning

Thank you! 
