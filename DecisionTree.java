import java.io.*;
import java.util.*;

/**
 * This class is to implement the DecisionTree given a csv file which store the attributes in the first line
 * and the value in the following lines, the last column is the classification of the branches attributes with 
 * their values.
 * 
 * @author Jinglin Li (jxl163530, 2021323767)
 *
 */

class Pair {
	String attribute;
	int value;
	Pair(String attribute, int value) {
		this.attribute = attribute;
		this.value = value;
	}
}

/**
 * The class TreeNode has the following properties:
 * 1. the index which is used for the replace
 * 2. pair which contains the attributes and values of the current node
 * 3. if this node is leaf, it has the classification value, which is the last column of the csv file
 * 4. the heuristic Value which is used for the sub heuristic value calculation
 * 5. the left and right children. left child is the "0" branch, right is the "1" branch. 
 * 6. the following path from the current node. which is used for the next searching and heuristic evaluation.
 *
 */
class TreeNode {
	int index;
	Pair pair;
	int classIfLeaf;
	double heuristicValue;
	
	TreeNode left;
	TreeNode right;
	
	List<List<Pair>> followingPath;
	
	TreeNode() {
		index = -1;
		pair = new Pair("", -1);
		classIfLeaf = -1;
		heuristicValue = 0;
		left = null;
		right = null;
		followingPath = new ArrayList<>();
	}
}
public class DecisionTree {
	public TreeNode root;
	private int index;
	
	
	/**
	 * this function is parse the file.csv. 
	 * @param fileName
	 * @return the paired 2d list data structure
	 */
	
	public List<List<Pair>> parseFile(String fileName) {	
		List<List<Pair>> parsedData = new ArrayList<>();
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader br = new BufferedReader(reader);
			String[] attributes = br.readLine().split(",");
			
			String str = null;
			while ((str = br.readLine()) != null) {
				String[] values = str.split(",");
				List<Pair> eachRow = new ArrayList<>();
				for (int i = 0; i < values.length; i++) {
					eachRow.add(new Pair(attributes[i], Integer.parseInt(values[i])));
				}
				parsedData.add(eachRow);
			}
			br.close();
			reader.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return parsedData;
	}
	
	/**
	 * build the decision tree, according to different heuristic function
	 * @param parsedData, which is the parsed Data from the parsedFile() function
	 * @param root, which is the global root of the Decision Tree. 
	 * @param heuristic
	 */
	
	public void build(List<List<Pair>> parsedData, TreeNode root, String heuristic) {
		
		/* 	Firstly, find the heuristic Value of the root	*/
		if (heuristic.equals("Entropy"))
			root.heuristicValue = new Calculation().calcEntropy(parsedData);
		else if (heuristic.equals("Variance Impurity"))
			root.heuristicValue = new Calculation().calcVarianceImpurity(parsedData);
		
		/*	calculate the information Gain, the attribute with the greatest of which is the next attribute*/
		double max = 0.0;
		Pair nextPair = null;
		List<List<Pair>> finalLeftData = new ArrayList<>();
		List<List<Pair>> finalRightData = new ArrayList<>();
		int nextIndex = -1;			// the index of next attribute. the "following path should delete that to become 'following'"
		
		for (int i = 0; i < parsedData.get(0).size() - 1; i++) {
			List<List<Pair>> leftData = new ArrayList<>();
			List<List<Pair>> rightData = new ArrayList<>();
			for (int j = 0; j < parsedData.size(); j++) {
				List<Pair> list = new ArrayList<>(parsedData.get(j));
				if (parsedData.get(j).get(i).value == 0)
					leftData.add(list);
				else
					rightData.add(list);
			}
			
			/*	calculate the information Gain based on the subLeft and subRight tree	*/
			double infoGain = new Calculation().calcInfoGain(root.heuristicValue, leftData, rightData, 
					parsedData.size(), heuristic);
			
			/*	get the index and the pair with the greatest information Gain, which is the used to find 
			 * the next attribute for exploring*/
			
			if (infoGain > max) {
				max = infoGain;
				nextPair = parsedData.get(0).get(i);
				nextIndex = i;
				finalLeftData = leftData;
				finalRightData = rightData;
				root.pair = parsedData.get(0).get(i);	// the root's attribute and its value
			}
		}
		// if the searching goes to the end
		if (nextIndex == -1) {
			root.classIfLeaf = parsedData.get(0).get(parsedData.get(0).size() - 1).value;
			return;
		}
		
		
		else {			
			// remove the next attribute in the "following path" list
			for (List<Pair> list : finalLeftData) {
				list.remove(nextIndex);
			}
			for (List<Pair> list : finalRightData) {
				list.remove(nextIndex);
			}
			
			// assign the left and right tree to the root.
			TreeNode left = new TreeNode();
			left.followingPath = finalLeftData;
			root.left = left;
			
			TreeNode right = new TreeNode();
			right.followingPath = finalRightData;
			root.right = right;
			
			
			root.index = ++index;		// increment the root's index
			root.pair = nextPair;
			
			/** recursive find the left and right tree **/
			build(finalLeftData, left, heuristic);
			build(finalRightData, right, heuristic);
		}
		
	}
	
	/**
	 * this function implement the post-Pruning based on the best accuracy of the resulting tree
	 * @param L
	 * @param K
	 * @param parsedData
	 * @param root
	 * @return the new root
	 */
	
	public TreeNode postPruning(int L, int K, List<List<Pair>> parsedData, TreeNode root) {
		TreeNode DBest = copy(root);
		for (int i = 0; i < L; i++) {
			TreeNode D1 = copy(root);
			int M = new Random().nextInt(K);
		
			for (int j = 0; j < M; j++) {
				int P = new Random().nextInt(index);
				replace(D1, P);
			}
			
			if (getAccuracy(D1, parsedData) > getAccuracy(DBest, parsedData)) {
				DBest = copy(D1);
			}
		}
		return DBest;
	}
	
	/*	copy the node used in the function post-pruning*/
	
	public TreeNode copy(TreeNode node) {
		
		TreeNode copyed = new TreeNode();
		if (node == null) {
			copyed = node;
			return copyed;
		}
		
		copyed.index = node.index;
		copyed.pair = new Pair(node.pair.attribute, node.pair.value);
		copyed.heuristicValue = node.heuristicValue;
		copyed.classIfLeaf = node.classIfLeaf;
		
		for (List<Pair> list : node.followingPath) {
			List<Pair> copyedList = new ArrayList<>();
			for (Pair pair : list) {
				copyedList.add(new Pair(pair.attribute, pair.value));
			}
			copyed.followingPath.add(copyedList);
		}
		
		copyed.left = copy(node.left);	
		copyed.right = copy(node.right);
		
		return copyed;
	}
	
	/**
	 * this function is used in the function post-pruning. replace the tree with the majority value in classification
	 * @param root
	 * @param index of the node in the tree
	 */
	
	public void replace(TreeNode root, int index) {
		
		if (root.index == index) {
			if (root.left.followingPath.size() > root.right.followingPath.size()) {
				int[] count = new int[2];
				for (List<Pair> list : root.left.followingPath) {
					count[list.get(list.size() - 1).value]++;
				}
				root.left.classIfLeaf = count[0] > count[1]? 0 : 1;
				
				root.left.left = null;
				root.left.right = null;
				root.left.index = -1;
				
			}
			else {
				int[] count = new int[2];
				for (List<Pair> list : root.right.followingPath) {
					count[list.get(list.size() - 1).value]++;
				}
				root.right.classIfLeaf = count[0] > count[1]? 0 : 1;
				
				root.right.left = null;
				root.right.right = null;
				root.right.index = -1;
				
			}
		}
		else if (root.left != null || root.right != null) {
			if (root.left != null)
				replace(root.left, index);
			if (root.right != null)
				replace(root.right, index);
		}
	}
	/**
	 * this function get the accuracy of the test set data based on training data after the post pruning. 
	 * if it is less than the previous tree, this post pruning will be ignored. 
	 * @param root
	 * @param parsedData
	 * @return the accuracy
	 */
	
	public double getAccuracy(TreeNode root, List<List<Pair>> parsedData) {
		if (root == null || parsedData == null)
			return 0.0;
		
		double accuracy = 0.0;
		for (List<Pair> list : parsedData) {
			if (helper(root, list))
				accuracy++;
			
		}
		return accuracy / parsedData.size();
	}
	
	public boolean helper(TreeNode root, List<Pair> list) {
		if (root == null)
			return false;
		if (root.left == null && root.right == null)
			return list.get(list.size() - 1).value == root.classIfLeaf;
		else {
			int value = -1;
			for (Pair pair : list) {
				if (pair.attribute.equals(root.pair.attribute)) {
					value = pair.value;
					break;
				}
			}
			if (value == 0)
				if (root.left != null)
					return helper(root.left, list);
			if (value == 1)
				if (root.right != null)
					return helper(root.right, list);
		}
		return false;
	}
	
	/**
	 * print the tree
	 * @param root
	 * @param level
	 * @return
	 */
	
	public String print(TreeNode root, int level) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < level; i++) {
			sb.append("| ");
		}
		sb.append(root.pair.attribute + " = 0 : ");
		if (root.left.left != null || root.left.right != null) {	
			sb.append("\n" + print(root.left, level + 1));
		}
		else {
			sb.append(root.left.classIfLeaf + "\n");
		}
		
		for (int i = 0; i < level; i++) {
			sb.append("| ");
		}
		sb.append(root.pair.attribute + " = 1 : ");
		if (root.right.left != null || root.right.right != null) {
			sb.append("\n" + print(root.right, level + 1));
		}
		else {
			sb.append(root.right.classIfLeaf + "\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * main function
	 * @param args
	 */
	
	public static void main(String[] args) {
		DecisionTree test = new DecisionTree();
		
		int L = Integer.parseInt(args[0]);
		int K = Integer.parseInt(args[1]);
		List<List<Pair>> trainingData = test.parseFile(args[2]);
		List<List<Pair>> validationData = test.parseFile(args[3]);
		List<List<Pair>> testData = test.parseFile(args[4]);
		boolean toPrint = args[5].equals("True");
		
		TreeNode root = new TreeNode();
		System.out.println("Use the \"Information Gain\" as the Heuristic");
		test.build(trainingData, root, "Entropy");
		System.out.println("Accuracy Before Post-Pruning: " + test.getAccuracy(root, testData));
		
		TreeNode root2 = new TreeNode();
		root2 = test.postPruning(L, K, validationData, root);
		
		System.out.println("Accuracy After Post-Pruning: " + test.getAccuracy(root2, testData));
		
		/*
		System.out.println("Before Post-Pruning: \n" + test.print(root, 0));
		System.out.println("After Post-Pruning: \n" + test.print(root2, 0));
		*/
		if (toPrint) {
			
			System.out.println("Before Post-Pruning: \n" + test.print(root, 0));
			System.out.println("After Post-Pruning: \n" + test.print(root2, 0));
		}
		
		/** =============================	use the variance impurity as the heuristic	======================== **/
		
		
		System.out.println("\n" + "Use the \"Variance Impurity\" as the Heuristic");
		test.build(trainingData, root, "Variance Impurity");
		
		System.out.println("Accuracy Before Post-Pruning: " + test.getAccuracy(root, testData));
		
		TreeNode root3 = new TreeNode();
		root3 = test.postPruning(L, K, validationData, root);
		System.out.println("Accuracy After Post-Pruning: " + test.getAccuracy(root3, testData));

		/*
		System.out.println("Before Post-Pruning: \n" + test.print(root, 0));
		System.out.println("After Post-Pruning: \n" + test.print(root3, 0));
		*/

		if (toPrint) {
			System.out.println("Before Post-Pruning: \n" + test.print(root, 0));
			System.out.println("Before Post-Pruning: \n" + test.print(root3, 0));
		}
	}
}
