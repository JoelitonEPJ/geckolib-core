package software.bernie.geckolib3.core.easing;

import java.util.function.Function;

public enum EasingType
{
	NONE(arg -> EasingManager.in(EasingManager::linear)),
	CUSTOM(arg -> EasingManager.in(EasingManager::linear)),

	Linear(arg -> EasingManager.in(EasingManager::linear)),
	Step(arg -> EasingManager.in(EasingManager.step(arg))),

	EaseInSine(arg -> EasingManager.in(EasingManager::sin)),
	EaseOutSine(arg -> EasingManager.out(EasingManager::sin)),
	EaseInOutSine(arg -> EasingManager.inOut(EasingManager::sin)),

	EaseInQuad(arg -> EasingManager.in(EasingManager::quad)),
	EaseOutQuad(arg -> EasingManager.out(EasingManager::quad)),
	EaseInOutQuad(arg -> EasingManager.inOut(EasingManager::quad)),

	EaseInCubic(arg -> EasingManager.in(EasingManager::cubic)),
	EaseOutCubic(arg -> EasingManager.out(EasingManager::cubic)),
	EaseInOutCubic(arg -> EasingManager.inOut(EasingManager::cubic)),

	EaseInQuart(arg -> EasingManager.in(EasingManager.poly(4))),
	EaseOutQuart(arg -> EasingManager.out(EasingManager.poly(4))),
	EaseInOutQuart(arg -> EasingManager.inOut(EasingManager.poly(4))),

	EaseInQuint(arg -> EasingManager.in(EasingManager.poly(5))),
	EaseOutQuint(arg -> EasingManager.out(EasingManager.poly(5))),
	EaseInOutQuint(arg -> EasingManager.inOut(EasingManager.poly(5))),

	EaseInExpo(arg -> EasingManager.in(EasingManager::exp)),
	EaseOutExpo(arg -> EasingManager.out(EasingManager::exp)),
	EaseInOutExpo(arg -> EasingManager.inOut(EasingManager::exp)),

	EaseInCirc(arg -> EasingManager.in(EasingManager::circle)),
	EaseOutCirc(arg -> EasingManager.out(EasingManager::circle)),
	EaseInOutCirc(arg -> EasingManager.inOut(EasingManager::circle)),

	EaseInBack(arg -> EasingManager.in(EasingManager.back(arg))),
	EaseOutBack(arg -> EasingManager.out(EasingManager.back(arg))),
	EaseInOutBack(arg -> EasingManager.inOut(EasingManager.back(arg))),

	EaseInElastic(arg -> EasingManager.in(EasingManager.elastic(arg))),
	EaseOutElastic(arg -> EasingManager.out(EasingManager.elastic(arg))),
	EaseInOutElastic(arg -> EasingManager.inOut(EasingManager.elastic(arg))),

	EaseInBounce(arg -> EasingManager.in(EasingManager.bounce(arg))),
	EaseOutBounce(arg -> EasingManager.out(EasingManager.bounce(arg))),
	EaseInOutBounce(arg -> EasingManager.inOut(EasingManager.bounce(arg)));

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
