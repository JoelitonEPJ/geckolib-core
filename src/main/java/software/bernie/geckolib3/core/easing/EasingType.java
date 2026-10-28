package software.bernie.geckolib3.core.easing;

import java.util.function.Function;

public enum EasingType
{
	NONE(arg -> EasingFunctions.in(EasingFunctions::linear)),
	CUSTOM(arg -> EasingFunctions.in(EasingFunctions::linear)),

	Linear(arg -> EasingFunctions.in(EasingFunctions::linear)),
	Step(arg -> EasingFunctions.in(EasingFunctions.step(arg))),

	EaseInSine(arg -> EasingFunctions.in(EasingFunctions::sin)),
	EaseOutSine(arg -> EasingFunctions.out(EasingFunctions::sin)),
	EaseInOutSine(arg -> EasingFunctions.inOut(EasingFunctions::sin)),

	EaseInQuad(arg -> EasingFunctions.in(EasingFunctions::quad)),
	EaseOutQuad(arg -> EasingFunctions.out(EasingFunctions::quad)),
	EaseInOutQuad(arg -> EasingFunctions.inOut(EasingFunctions::quad)),

	EaseInCubic(arg -> EasingFunctions.in(EasingFunctions::cubic)),
	EaseOutCubic(arg -> EasingFunctions.out(EasingFunctions::cubic)),
	EaseInOutCubic(arg -> EasingFunctions.inOut(EasingFunctions::cubic)),

	EaseInQuart(arg -> EasingFunctions.in(EasingFunctions.poly(4))),
	EaseOutQuart(arg -> EasingFunctions.out(EasingFunctions.poly(4))),
	EaseInOutQuart(arg -> EasingFunctions.inOut(EasingFunctions.poly(4))),

	EaseInQuint(arg -> EasingFunctions.in(EasingFunctions.poly(5))),
	EaseOutQuint(arg -> EasingFunctions.out(EasingFunctions.poly(5))),
	EaseInOutQuint(arg -> EasingFunctions.inOut(EasingFunctions.poly(5))),

	EaseInExpo(arg -> EasingFunctions.in(EasingFunctions::exp)),
	EaseOutExpo(arg -> EasingFunctions.out(EasingFunctions::exp)),
	EaseInOutExpo(arg -> EasingFunctions.inOut(EasingFunctions::exp)),

	EaseInCirc(arg -> EasingFunctions.in(EasingFunctions::circle)),
	EaseOutCirc(arg -> EasingFunctions.out(EasingFunctions::circle)),
	EaseInOutCirc(arg -> EasingFunctions.inOut(EasingFunctions::circle)),

	EaseInBack(arg -> EasingFunctions.in(EasingFunctions.back(arg))),
	EaseOutBack(arg -> EasingFunctions.out(EasingFunctions.back(arg))),
	EaseInOutBack(arg -> EasingFunctions.inOut(EasingFunctions.back(arg))),

	EaseInElastic(arg -> EasingFunctions.in(EasingFunctions.elastic(arg))),
	EaseOutElastic(arg -> EasingFunctions.out(EasingFunctions.elastic(arg))),
	EaseInOutElastic(arg -> EasingFunctions.inOut(EasingFunctions.elastic(arg))),

	EaseInBounce(arg -> EasingFunctions.in(EasingFunctions.bounce(arg))),
	EaseOutBounce(arg -> EasingFunctions.out(EasingFunctions.bounce(arg))),
	EaseInOutBounce(arg -> EasingFunctions.inOut(EasingFunctions.bounce(arg)));

	private final Function<Double, Function<Double, Double>> functionFactory;

	EasingType(Function<Double, Function<Double, Double>> functionFactory) {
		this.functionFactory = functionFactory;
	}

	Function<Double, Double> createFunction(Double argument) {
		return functionFactory.apply(argument);
	}

	public static EasingType getEasingTypeFromString(String search) {
		for (EasingType each : EasingType.values()) {
			if (each.name().compareToIgnoreCase(search) == 0) {
				return each;
			}
		}
		return null;
	}
}
