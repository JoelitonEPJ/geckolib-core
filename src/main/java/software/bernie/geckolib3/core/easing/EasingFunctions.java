package software.bernie.geckolib3.core.easing;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public final class EasingFunctions
{
	private EasingFunctions() {}


	static Function<Double, Double> in(Function<Double, Double> easing) {
		return easing;
	}

	static Function<Double, Double> out(Function<Double, Double> easing) {
		return t -> 1 - easing.apply(1 - t);
	}

	static Function<Double, Double> inOut(Function<Double, Double> easing) {
		return t ->
		{
			if (t < 0.5)
			{
				return easing.apply(t * 2) / 2;
			}
			return 1 - easing.apply((1 - t) * 2) / 2;
		};
	}

	static Function<Double, Double> step0() {
		return n -> n > 0 ? 1D : 0;
	}

	static Function<Double, Double> step1() {
		return n -> n >= 1D ? 1D : 0;
	}

	static double linear(double t)
	{
		return t;
	}

	static double quad(double t)
	{
		return t * t;
	}

	static double cubic(double t) {
		return t * t * t;
	}

	static Function<Double, Double> poly(double n) {
		return (t) -> Math.pow(t, n);
	}

	static double sin(double t) {
		return 1 - Math.cos((float) ((t * Math.PI) / 2));
	}

	static double circle(double t) {
		return 1 - Math.sqrt(1 - t * t);
	}

	static double exp(double t) {
		return Math.pow(2, 10 * (t - 1));
	}

	static Function<Double, Double> elastic(Double bounciness) {
		double p = (bounciness == null ? 1 : bounciness) * Math.PI;
		return t -> 1 - Math.pow(Math.cos((float) ((t * Math.PI) / 2)), 3) * Math.cos((float) (t * p));
	}

	static Function<Double, Double> back(Double s) {
		double p = s == null ? 1.70158 : s * 1.70158;
		return t -> t * t * ((p + 1) * t - p);
	}

	public static Function<Double, Double> bounce(Double s)
	{
		double k = s == null ? 0.5 : s;
		Function<Double, Double> q = x -> (121.0 / 16.0) * x * x;
		Function<Double, Double> w = x -> ((121.0 / 4.0) * k) * Math.pow(x - (6.0 / 11.0), 2) + 1 - k;
		Function<Double, Double> r = x -> 121 * k * k * Math.pow(x - (9.0 / 11.0), 2) + 1 - k * k;
		Function<Double, Double> t = x -> 484 * k * k * k * Math.pow(x - (10.5 / 11.0), 2) + 1 - k * k * k;
		return x -> min(q.apply(x), w.apply(x), r.apply(x), t.apply(x));
	}

	static Function<Double, Double> step(Double stepArg) {
		int steps = stepArg != null ? stepArg.intValue() : 2;
		double[] intervals = stepRange(steps);
		return t -> intervals[findIntervalBorderIndex(t, intervals, false)];
	}

	static double min(double a, double b, double c, double d)
	{
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	static int findIntervalBorderIndex(double point, double[] intervals, boolean useRightBorder) {
		//If point is beyond given intervals
		if (point < intervals[0])
			return 0;
		if (point > intervals[intervals.length - 1])
			return intervals.length - 1;
		//If point is inside interval
		//Start searching on a full range of intervals
		int indexOfNumberToCompare = 0;
		int leftBorderIndex = 0;
		int rightBorderIndex = intervals.length - 1;
		//Reduce searching range till it find an interval point belongs to using binary search
		while (rightBorderIndex - leftBorderIndex != 1) {
			indexOfNumberToCompare = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;
			if (point >= intervals[indexOfNumberToCompare]) {
				leftBorderIndex = indexOfNumberToCompare;
			} else {
				rightBorderIndex = indexOfNumberToCompare;
			}
		}
		return useRightBorder ? rightBorderIndex : leftBorderIndex;
	}

	static double[] stepRange(int steps) {
		final double stop = 1;
		if (steps < 2) throw new IllegalArgumentException("steps must be > 2, got:" + steps);
		double stepLength = stop / (double)steps;
		// There must be an easier way of doing this but I just don't care
		AtomicInteger i = new AtomicInteger();
		return DoubleStream.generate(() -> i.getAndIncrement() * stepLength)
				.limit(steps)
				.toArray();
	};
}
