package luis.gen.svr;

import luis.gen.Solution;
import luis.gen.tsp.TspEngine;

public class GenInfo {

	private long initialTime, currentTime;
	public TspEngine engine;
	public int generationCount;
	public Solution incumbent;
	public int lastIncumbentGen;
	public long lastIncumbentWhen;

	public GenInfo(TspEngine engine) {
		this.engine = engine;
		initialTime = System.currentTimeMillis();
	}
	
	public int getGpm() {
		double minutes = (double)getElapsed() / (1000.0 * 60.0);
		return (int) (generationCount / minutes);
	}
	
	public long getElapsed() {
		return currentTime - initialTime;
	}

	public void markTime() {
		currentTime = System.currentTimeMillis();
	}

}
