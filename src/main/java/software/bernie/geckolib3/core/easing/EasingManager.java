package software.bernie.geckolib3.core.easing;

import software.bernie.geckolib3.core.util.Memoizer;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class EasingManager
{
	static class EasingFunctionArgs {
		public final EasingType easingType;
		public final Double arg0;

		public EasingFunctionArgs(EasingType easingType,  Double arg0) {
			this.easingType = easingType;
			this.arg0 = arg0;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			EasingFunctionArgs that = (EasingFunctionArgs) o;
			return easingType == that.easingType &&
					Objects.equals(arg0, that.arg0);
		}

		@Override
		public int hashCode() {
			return Objects.hash(easingType, arg0);
		}
	}

	public static double ease(double number, EasingType easingType,  List<Double> easingArgs)
	{
		Double firstArg = easingArgs == null || easingArgs.size() < 1 ? null : easingArgs.get(0);
		return getEasingFunction.apply(new EasingFunctionArgs(easingType, firstArg)).apply(number);
	}

	// Memoize easing functions so we don't need to create new ones from HOFs every frame
	static Function<EasingFunctionArgs, Function<Double, Double>> getEasingFunction =
			Memoizer.memoize(EasingManager::getEasingFuncImpl);

	// Don't call this, use getEasingFunction instead as that function is the memoized version
	static Function<Double, Double> getEasingFuncImpl(EasingFunctionArgs args) {
		return args.easingType.createFunction(args.arg0);
	}

}
