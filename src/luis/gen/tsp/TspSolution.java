package luis.gen.tsp;

import java.util.Arrays;
import java.util.Random;

import luis.gen.Solution;

public class TspSolution extends Solution {
	
	private int [] cities;
	private CountryMap map;
	private double eval;
	private boolean [] flags;
	// Static data will not work if same-process parallelization is used
	private static Solution [] combineResults = new Solution[2];

	public TspSolution(Random rand, CountryMap map) {
		this(map);
		permuteCities(rand);
		eval = calcTrip();
	}

	public int [] getCities() {
		return cities;
	}
	
	@Override
	public double evaluate() {
		return eval;
	}

	@Override
	public Solution [] combine(Solution other, Random rand) {
		int pos = rand.nextInt(cities.length);
		TspSolution mother = this;
		TspSolution father = (TspSolution) other;
		TspSolution child = combineLeft(pos, mother, father);
		child.eval = child.calcTrip();
		combineResults[0] = child;
		child = combineRight(pos, mother, father);
		child.eval = child.calcTrip();
		combineResults[1] = child;
		//TODO: review "cruzaPob" from the c++ version. This is a critical point.
		return combineResults;
	}

	public boolean equals(Object other) {
		TspSolution tspOther = (TspSolution) other;
		if (this.eval != tspOther.eval) return false;
		return Arrays.equals(this.cities, tspOther.cities);
	}

	@Override
	public Solution invert() {
		TspSolution inverted = new TspSolution(map);
		for (int i = 0; i < cities.length; i++)
			inverted.cities[i] = cities[cities.length - i - 1];
		inverted.eval = eval;
		return inverted;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(eval).append(" - [");
		for (int i = 0; i < cities.length; i++) {
			if (i > 0) sb.append(", ");
			sb.append(cities[i]);
		}
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return (int) eval;
	}


	
	//------------------------------ Privates ------------------------------

	private TspSolution(CountryMap map) {
		this.map = map;
		cities = new int [map.numCities];
		Arrays.fill(cities, -1);
		eval = Double.NEGATIVE_INFINITY;
		flags = new boolean [map.numCities];
	}

	private void permuteCities(Random rand) {
		int [] indexes = new int [cities.length];
		for (int i = 0; i < cities.length; i++) indexes[i] = i;
		for (int i = 0; i < cities.length; i++) {
			int idxSize = cities.length - i;
			int pos = rand.nextInt(idxSize);
			cities[i] = indexes[pos];
			indexes[pos] = indexes[idxSize - 1];
		}
	}
	
	private TspSolution combineLeft(int pos, TspSolution mother, TspSolution father) {
		TspSolution child = new TspSolution(map);
		// Copy left side of mother
		System.arraycopy(mother.cities, 0, child.cities, 0, pos);
		child.initFlags(mother.cities, 0, pos);
		// Copy not found cities from father starting from right side and wrapping
		int fatherPos = pos, i = 0;
		while (pos < child.cities.length) {
			int cityNum = father.cities[fatherPos]; 
			if (!child.found(cityNum, 0, pos))
				child.cities[pos++] = cityNum;
			fatherPos++;
			if (fatherPos >= father.cities.length)
				fatherPos = 0;
			i++;
			if (i > father.cities.length)
				throw new AssertionError("Could not combine");
		}
		return child;
	}
	
	private TspSolution combineRight(int pos, TspSolution mother, TspSolution father) {
		TspSolution child = new TspSolution(map);
		// Copy left side of mother
		System.arraycopy(mother.cities, pos, child.cities, pos, mother.cities.length - pos);
		child.initFlags(mother.cities, pos, mother.cities.length);
		// Copy not found cities from father from beginning
		int fatherPos = 0, i = 0;
		while (i < pos) {
			int cityNum = father.cities[fatherPos]; 
			if (!child.found(cityNum, pos, child.cities.length))
				child.cities[i++] = cityNum;
			fatherPos++;
			if (fatherPos > father.cities.length)
				throw new AssertionError("Could not combine");
		}
		return child;
	}

	private void initFlags(int[] cts, int pos1, int pos2) {
		for (int i = pos1; i < pos2; i++) flags[cts[i]] = true;
	}

	private boolean found(int cityNum, int pos1, int pos2) {
		return flags[cityNum];
	}

	private double calcTrip() {
		double dist = 0;
		for (int i = 0; i < cities.length - 1; i++)
			dist += map.getDistance(cities[i], cities[i + 1]);
		dist += map.getDistance(cities[cities.length - 1], cities[0]);
		return dist;
	}

}
