package software.bernie.geckolib3.core.controller;

import com.eliotlash.molang.ast.Evaluatable;
import com.eliotlash.molang.ast.Evaluator;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.KeyFrame;
import software.bernie.geckolib3.core.keyframe.KeyFrameLocation;
import software.bernie.geckolib3.core.util.Axis;

import java.util.List;

/**
 * Samples keyframe values without managing animation playback or its context.
 */
final class KeyframeSampler {
    private KeyframeSampler() {
    }

    // Helper method to transform a KeyFrameLocation to an AnimationPoint
    static AnimationPoint sample(List<KeyFrame<Evaluatable>> frames, double tick, boolean isRotation,
                                 Axis axis, Evaluator evaluator) {
        KeyFrameLocation<KeyFrame<Evaluatable>> location = getCurrentKeyFrameLocation(frames, tick);
        KeyFrame<Evaluatable> currentFrame = location.currentFrame;
        double startValue = currentFrame.getStartValue().evaluate(evaluator);
        double endValue = currentFrame.getEndValue().evaluate(evaluator);

        if (isRotation) {
            if (!currentFrame.getStartValue().isConstant()) {
                startValue = Math.toRadians(startValue);
                if (axis == Axis.X || axis == Axis.Y) {
                    startValue *= -1;
                }
            }
            if (!currentFrame.getEndValue().isConstant()) {
                endValue = Math.toRadians(endValue);
                if (axis == Axis.X || axis == Axis.Y) {
                    endValue *= -1;
                }
            }
        }

        return new AnimationPoint(currentFrame, location.currentTick, currentFrame.getLength(), startValue, endValue);
    }

    /**
     * Locates the current keyframe and its local tick. At or beyond the total
     * duration, preserves the original fallback to the last frame and input tick.
     */
    private static KeyFrameLocation<KeyFrame<Evaluatable>> getCurrentKeyFrameLocation(List<KeyFrame<Evaluatable>> frames,
                                                                                     double ageInTicks) {
        double totalTimeTracker = 0;
        for (KeyFrame<Evaluatable> frame : frames) {
            totalTimeTracker += frame.getLength();
            if (totalTimeTracker > ageInTicks) {
                double tick = (ageInTicks - (totalTimeTracker - frame.getLength()));
                return new KeyFrameLocation<>(frame, tick);
            }
        }
        return new KeyFrameLocation<>(frames.get(frames.size() - 1), ageInTicks);
    }
}
