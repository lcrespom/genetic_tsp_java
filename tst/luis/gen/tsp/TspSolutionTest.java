package luis.gen.tsp;

import java.util.Random;

import junit.framework.TestCase;

public class TspSolutionTest extends TestCase {
	
	public void testRandomConstructor() {
		Random rand = new Random();
		for (int numCities = 2; numCities < 30; numCities++)
			for (int i = 0; i < 100; i++)
				checkSingleSolution(numCities, rand);
	}

	private void checkSingleSolution(int numCities, Random rand) {
		TspSolution sol = new TspSolution(rand, new CountryMap(numCities, rand));
		int [] checkTable = new int [numCities];
		for (int i = 0; i < numCities; i++) checkTable[i] = 0;
		int [] cities = sol.getCities();
		for (int i = 0; i < cities.length; i++) checkTable[cities[i]]++;
		for (int i = 0; i < checkTable.length; i++)
			assertTrue(checkTable[i] == 1);
	}
	
	public void testEvaluate() {
		Random rand = new Random();
		TspSolution sol = new TspSolution(rand, new CountryMap(30, rand));
		assertTrue(sol.evaluate() > 0);
		assertTrue(sol.evaluate() < 30*2000);
	}

}
