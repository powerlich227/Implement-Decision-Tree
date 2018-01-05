import java.util.*;

/**
 * this class Calculation is to calculate the entropy, varianceImpurity etc. for the Decision Tree
 * @author Jinglin Li (jxl163530, 2021323767)
 */

public class Calculation {
	
	/**
	 * this function calculate the total entropy, which is -p0 * lg2(p0)-p1*lg2(p2)
	 * @param parsedData
	 * @return
	 */
	
	public double calcEntropy(List<List<Pair>> parsedData) {
		int[] count = new int[2];
		for (List<Pair> list : parsedData) {
			count[list.get(list.size() - 1).value]++;
		}
		double p0 = (double)count[0] / (double)(count[0] + count[1]);
		double p1 = 1.0 - p0;
		return -p0 * Math.log(p0) / Math.log(2) - p1 * Math.log(p1) / Math.log(2);
	}
	
	/**
	 * this function calculate the Variance Impurity 
	 * @param parsedFile
	 * @return
	 */
	
	public double calcVarianceImpurity(List<List<Pair>> parsedFile) {
		int[] count = new int[2];
		for (List<Pair> list : parsedFile) {
			count[list.get(list.size() - 1).value]++;
		}
		return (double)(count[0] * count[1]) / (double) (parsedFile.size() * parsedFile.size());
	}
	
	/**
	 * this function calculate the node's information gain of the all the node giving the parsedData and subData
	 * @param entropy	the current node's Heuristic Value
	 * @param leftData	the following Data of the current node, when current node's value is 0
	 * @param rightData the following Data of the current node, which current node's value is 1
	 * @param size		the size of the total tree data
	 * @param heuristic
	 * @return
	 */
	
	public double calcInfoGain(double entropy, List<List<Pair>> leftData, List<List<Pair>> rightData, 
			int size, String heuristic) {
		List<Double> subEntropy = new ArrayList<>();
		if (heuristic.equals("Entropy")) {
			subEntropy.add(calcEntropy(leftData));
			subEntropy.add(calcEntropy(rightData));
		}
		else if (heuristic.equals("Variance Impurity")) {
			subEntropy.add(calcVarianceImpurity(leftData));
			subEntropy.add(calcVarianceImpurity(rightData));
		}
		List<Integer> subSize = new ArrayList<>();
		subSize.add(leftData.size());
		subSize.add(rightData.size());
		
		double gain = entropy;
		for (int i = 0; i < subEntropy.size(); i++) {
			gain -= ((double)subSize.get(i) / (double)size) * subEntropy.get(i);
		}
		return gain;
	}
}
