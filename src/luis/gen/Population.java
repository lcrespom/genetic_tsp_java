package luis.gen;

import java.util.Arrays;
import java.util.Random;

public class Population {
	private Solution[] solutions;
	private double[] weights;
	private int idx;
	double totalWeight;
	double weightExponent;
	//private Set<Solution> cloneTrackingSet;

	public Population(Params params) {
		solutions = new Solution[params.population];
		weights = new double[params.population];
		weightExponent = params.weightExponent;
		idx = 0;
		//cloneTrackingSet = new HashSet<Solution>(params.population);
	}

	public void add(Solution sol) {
		solutions[idx++] = sol;
	}

	public void prepareForSelection() {
		Arrays.sort(solutions, 0, size());
		double max = solutions[solutions.length - 1].evaluate();
		totalWeight = 0;
		for (int i = 0; i < weights.length; i++) {
			double weight = max - solutions[i].evaluate();
			if (weightExponent != 1.0)
				weight = Math.pow(weight, weightExponent);
			totalWeight += weight;
			weights[i] = totalWeight;
		}
	}
	
	public Solution select(Random rand) {
		double r = rand.nextDouble() * totalWeight;
		for (int i = 0; i < weights.length; i++)
			if (r < weights[i]) return solutions[i];
		throw new AssertionError("Invalid population state");
	}

	public Solution getIncumbent() {
		return solutions[0];
	}
	
	public int size() {
		return idx;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Size: ").append(size()).append("\n");
		for (Solution sol : solutions)
			sb.append("   ").append(sol).append("\n");
		return sb.toString();
	}

	public boolean hasClone(Solution other) {
		// The commented code gives around a 5% GPM speed improvement
//		if (cloneTrackingSet.contains(other)) return true;
//		cloneTrackingSet.add(other);
//		return false;
		for (int i = 0; i < idx; i++)
			if (other.equals(solutions[i])) return true;
		return false;
	}

	public void copySolutions(Population newGen, int numSolutions) {
		System.arraycopy(solutions, 0, newGen.solutions, 0, numSolutions);
		newGen.idx = numSolutions;
	}

}
