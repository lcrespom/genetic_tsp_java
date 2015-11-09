package luis.gen.tsp;

import java.util.Random;

import luis.gen.Engine;
import luis.gen.Solution;

public class TspEngine extends Engine {

	private CountryMap map;

	public TspEngine(TspParams params) {
		super(params);
		map = new CountryMap(params.numCities, new Random(314));
	}

	@Override
	protected Solution newSolution(Random rand) {
		return new TspSolution(rand, map);
	}

	public CountryMap getMap() {
		return map;
	}

}
