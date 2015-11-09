package luis.gen.tsp;

import java.util.Random;

public class CountryMap {

	public int numCities;
	private double[][] distances;
	public double [] cityX;
	public double [] cityY;

	public CountryMap(int numCities, Random rand) {
		this.numCities = numCities;
		cityX = new double[numCities];
		cityY = new double[numCities];
		initCities(rand, cityX, cityY);
		distances = new double[numCities][numCities];
		for (int i = 0; i < numCities; i++)
			for (int j = 0; j < numCities; j++)
				distances[i][j] = calcDistance(cityX[i], cityY[i], cityX[j], cityY[j]);
	}

	public double getDistance(int i, int j) {
		return distances[i][j];
	}

	private void initCities(Random rand, double[] cityX, double[] cityY) {
		if (numCities < 100) {
			// Avoid cities from being too close
			boolean [] visited = new boolean[150];
			int visit;
			for (int i = 0; i < numCities; i++) {
				do visit = rand.nextInt(149) + 1; while (visited[visit]);
				visited[visit] = true;
				cityX[i] = 20 + (visit % 15) * 40 + rand.nextInt(10);
				cityY[i] = 30 + (visit / 15) * 40 + rand.nextInt(10);
			}
		}
		else {
			// Just spread cities randomly
			double width = 640, height = 480;
			for (int i = 0; i < numCities; i++) {
				cityX[i] = rand.nextDouble() * width;
				cityY[i] = rand.nextDouble() * height;
			}
		}
	}

	private double calcDistance(double x1, double y1, double x2, double y2) {
		double dx = x2 - x1, dy = y2 - y1;
		return Math.sqrt(dx*dx + dy*dy);
	}

}
