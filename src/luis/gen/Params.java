package luis.gen;

/* Parameter tuning is an art. Parameters are interdependent on each other, so the same
 * parameter value may be very good or bad depending on the value of other parameters. 
 */
public class Params {
	public int population;
	// Elite should be as small as possible
	public int elite;
	// Inverting greatly improves quality
	public double invertRatio;
	// The higher the weight exponent, the biggest bias towards selecting good solutions
	public double weightExponent;
}
