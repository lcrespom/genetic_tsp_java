package luis.gen;

import java.util.Random;

import luis.gen.tsp.TspParams;


public abstract class Engine {
	
	protected Params params;
	protected boolean stop = false;
	protected Population generation;
	protected Random rand;
	protected EngineListener listener;

	public Engine(TspParams params) {
		this.params = params;
	}

	public Params getParams() {
		return params;
	}

	public void run() {
		int generationCount = 0;
		rand = new Random();
		generation = randomize();
		generation.prepareForSelection();
		while (!stop) {
			step();
			generationCount++;
			fireStepEvent(generation, generationCount);
		}
	}

	public void setListener(EngineListener listener) {
		this.listener = listener;
	}

	protected abstract Solution newSolution(Random rand);


	//-------------------------- Privates --------------------------
	
	private void step() {
		//TODO: more actions such as mutations, gene pumping (whatever that is), etc.
		Population newGen = new Population(params);
		copyElite(generation, newGen);
		generation = combine(generation, newGen);
		while (generation.size() < params.population) generation.add(newSolution(rand));
		generation.prepareForSelection();
	}

	private void copyElite(Population oldGen, Population newGen) {
		oldGen.copySolutions(newGen, params.elite);
	}

	private Population combine(Population oldGen, Population newGen) {
		int i = params.elite, trials = 0;
		while (i < params.population && trials < params.population * 2) {
			trials++;
			Solution father = generation.select(rand);
			Solution mother = generation.select(rand);
			if (father.equals(mother)) continue;
			if (rand.nextDouble() < params.invertRatio) father = father.invert();
			Solution [] children = father.combine(mother, rand);
			for (Solution child : children) {
				if (newGen.hasClone(child)) continue;
				if (i < params.population) newGen.add(child);
				i++;
			}
		}
		return newGen;
	}

	private void fireStepEvent(Population pop, int generationCount) {
		if (listener == null) return;
		listener.engineStep(pop, generationCount);
	}

	private Population randomize() {
		Population generation = new Population(params);
		//TODO: understand why the code below is not required in the c++ version
		for (int i = 0; i < params.population; i++) generation.add(newSolution(rand)); 
		return generation;
	}

}
