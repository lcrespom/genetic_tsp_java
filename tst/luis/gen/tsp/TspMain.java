package luis.gen.tsp;

import luis.gen.EngineListener;
import luis.gen.Population;

public class TspMain implements EngineListener {

	private long time, initialTime;
	
	public static void main(String[] args) {
		new TspMain().run(args);
	}

	private void run(String[] args) {
		TspParams params = initParams(args);
		TspEngine eng = new TspEngine(params);
		eng.setListener(this);
		time = 0;
		initialTime = System.currentTimeMillis();
		eng.run();
	}

	private TspParams initParams(String[] args) {
		TspParams params = new TspParams();
		if (args.length > 0) params.numCities = Integer.parseInt(args[0]);
		else params.numCities = 100;
		params.population = 200;
		params.elite = 1;
		return params;
	}

	@Override
	public void engineStep(Population pop, int generationCount) {
		long newTime = System.currentTimeMillis();
		if (newTime - time > 1000) {
			time = newTime;
			reportStatus(pop, generationCount);
		}
	}

	private void reportStatus(Population pop, int generationCount) {
		System.out.println("----------");
		double minutes = (double)(time - initialTime) / (1000.0 * 60.0);
		double gpm = generationCount / minutes;
		System.out.println("Generation: " + generationCount + " - " + (int) gpm + " GPM");
		System.out.println("Incumbent eval: " + pop.getIncumbent().evaluate());
	}

}
