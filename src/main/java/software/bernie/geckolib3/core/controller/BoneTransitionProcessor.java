package software.bernie.geckolib3.core.controller;

import com.eliotlash.molang.ast.Evaluatable;
import com.eliotlash.molang.ast.Evaluator;
import com.eliotlash.molang.variables.ExecutionContext;
import com.eliotlash.molang.variables.VariableFlavor;
import org.apache.commons.lang3.tuple.Pair;
import software.bernie.geckolib3.core.keyframe.AnimationPoint;
import software.bernie.geckolib3.core.keyframe.BoneAnimation;
import software.bernie.geckolib3.core.keyframe.BoneAnimationQueue;
import software.bernie.geckolib3.core.keyframe.KeyFrame;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;
import software.bernie.geckolib3.core.util.Axis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Captures transition poses and fills bone queues with transition points.
 */
final class BoneTransitionProcessor {
    private final Map<String, BoneSnapshot> boneSnapshots = new HashMap<>();

    // At the beginning of a new transition, save a snapshot of the model's
    // rotation, position, and scale values as the initial value to lerp from
    void captureSnapshots(Map<String, Pair<IBone, BoneSnapshot>> boneSnapshotCollection) {
        // Keep snapshots for bones omitted from this capture, as in the controller.
        for (Pair<IBone, BoneSnapshot> snapshot : boneSnapshotCollection.values()) {
            this.boneSnapshots.put(snapshot.getLeft().getName(), new BoneSnapshot(snapshot.getRight()));
        }
    }

    void process(AnimationController<?> controller, double tick, List<IBone> modelRendererList,
                 Map<String, BoneAnimationQueue> boneAnimationQueues, Evaluator evaluator,
                 ExecutionContext context) {
        for (IBone bone : modelRendererList) {
            BoneAnimationQueue boneAnimationQueue = boneAnimationQueues.get(bone.getName());
            BoneSnapshot boneSnapshot = this.boneSnapshots.get(bone.getName());

            if (boneSnapshot == null) {
                continue;
            }

            BoneSnapshot initialSnapshot = bone.getInitialSnapshot();

            // Resolve animations per bone; expression evaluation can change controller state.
            BoneAnimation boneAnimation = controller.currentAnimation.boneAnimations.get(bone.getName());
            BoneAnimation previousAnimation = controller.previousAnimation == null ? null : controller.previousAnimation.boneAnimations.get(bone.getName());

            // Adding the initial positions of the upcoming animation, so the model
            // transitions to the initial state of the new animation
            if (boneAnimation != null && !boneAnimation.rotationKeyFrames.xKeyFrames.isEmpty()) {
                AnimationPoint xPoint = getTransitionPointAtTick(context, boneAnimation.rotationKeyFrames.xKeyFrames, tick, true, Axis.X,
                        evaluator);
                AnimationPoint yPoint = getTransitionPointAtTick(context, boneAnimation.rotationKeyFrames.yKeyFrames, tick, true, Axis.Y,
                        evaluator);
                AnimationPoint zPoint = getTransitionPointAtTick(context, boneAnimation.rotationKeyFrames.zKeyFrames, tick, true, Axis.Z,
                        evaluator);
                boneAnimationQueue.rotationXQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.rotationValueX - initialSnapshot.rotationValueX,
                        xPoint.animationStartValue));
                boneAnimationQueue.rotationYQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.rotationValueY - initialSnapshot.rotationValueY,
                        yPoint.animationStartValue));
                boneAnimationQueue.rotationZQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.rotationValueZ - initialSnapshot.rotationValueZ,
                        zPoint.animationStartValue));
            } else if (previousAnimation != null && !previousAnimation.rotationKeyFrames.xKeyFrames.isEmpty()) {
                boneAnimationQueue.rotationXQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.rotationValueX,
                        initialSnapshot.rotationValueX));
                boneAnimationQueue.rotationYQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.rotationValueY,
                        initialSnapshot.rotationValueY));
                boneAnimationQueue.rotationZQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.rotationValueZ,
                        initialSnapshot.rotationValueZ));
            }

            if (boneAnimation != null && !boneAnimation.positionKeyFrames.xKeyFrames.isEmpty()) {
                AnimationPoint xPoint = getTransitionPointAtTick(context, boneAnimation.positionKeyFrames.xKeyFrames, tick, false, Axis.X,
                        evaluator);
                AnimationPoint yPoint = getTransitionPointAtTick(context, boneAnimation.positionKeyFrames.yKeyFrames, tick, false, Axis.Y,
                        evaluator);
                AnimationPoint zPoint = getTransitionPointAtTick(context, boneAnimation.positionKeyFrames.zKeyFrames, tick, false, Axis.Z,
                        evaluator);
                boneAnimationQueue.positionXQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.positionOffsetX, xPoint.animationStartValue));
                boneAnimationQueue.positionYQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.positionOffsetY, yPoint.animationStartValue));
                boneAnimationQueue.positionZQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.positionOffsetZ, zPoint.animationStartValue));
            } else if (previousAnimation != null && !previousAnimation.positionKeyFrames.xKeyFrames.isEmpty()) {
                boneAnimationQueue.positionXQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.positionOffsetX, initialSnapshot.positionOffsetX));
                boneAnimationQueue.positionYQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.positionOffsetY, initialSnapshot.positionOffsetY));
                boneAnimationQueue.positionZQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.positionOffsetZ, initialSnapshot.positionOffsetZ));
            }

            if (boneAnimation != null && !boneAnimation.scaleKeyFrames.xKeyFrames.isEmpty()) {
                AnimationPoint xPoint = getTransitionPointAtTick(context, boneAnimation.scaleKeyFrames.xKeyFrames, tick, false, Axis.X,
                        evaluator);
                AnimationPoint yPoint = getTransitionPointAtTick(context, boneAnimation.scaleKeyFrames.yKeyFrames, tick, false, Axis.Y,
                        evaluator);
                AnimationPoint zPoint = getTransitionPointAtTick(context, boneAnimation.scaleKeyFrames.zKeyFrames, tick, false, Axis.Z,
                        evaluator);
                boneAnimationQueue.scaleXQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.scaleValueX, xPoint.animationStartValue));
                boneAnimationQueue.scaleYQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.scaleValueY, yPoint.animationStartValue));
                boneAnimationQueue.scaleZQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.scaleValueZ, zPoint.animationStartValue));
            } else if (previousAnimation != null && !previousAnimation.scaleKeyFrames.xKeyFrames.isEmpty()) {
                boneAnimationQueue.scaleXQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.scaleValueX, initialSnapshot.scaleValueX));
                boneAnimationQueue.scaleYQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.scaleValueY, initialSnapshot.scaleValueY));
                boneAnimationQueue.scaleZQueue.add(new AnimationPoint(null, tick, controller.transitionLengthTicks,
                        boneSnapshot.scaleValueZ, initialSnapshot.scaleValueZ));
            }
        }
    }

    private static AnimationPoint getTransitionPointAtTick(ExecutionContext context, List<KeyFrame<Evaluatable>> frames,
                                                          double tick, boolean isRotation, Axis axis, Evaluator evaluator) {
        context.setVariable(VariableFlavor.QUERY, "anim_time", tick / 20);
        return KeyframeSampler.sample(frames, tick, isRotation, axis, evaluator);
    }
}
