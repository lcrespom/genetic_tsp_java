package luis.gen;

import java.util.Random;

public abstract class Solution implements Comparable<Solution> {

	@Override
	public int compareTo(Solution other) {
		double ev1 = this.evaluate(), ev2 = other.evaluate();
		if (ev1 < ev2) return -1;
		if (ev1 > ev2) return +1;
		return 0;
	}
	
	public abstract double evaluate();

	public abstract Solution [] combine(Solution other, Random rand);

	public abstract Solution invert();

}
