package software.bernie.geckolib3.core.controller;

import com.eliotlash.molang.ast.Evaluatable;
import com.eliotlash.molang.variables.ExecutionContext;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController.ICustomInstructionListener;
import software.bernie.geckolib3.core.controller.AnimationController.IParticleListener;
import software.bernie.geckolib3.core.controller.AnimationController.ISoundListener;
import software.bernie.geckolib3.core.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib3.core.event.ParticleKeyFrameEvent;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.keyframe.EventKeyFrame;
import software.bernie.geckolib3.core.keyframe.ParticleEventKeyFrame;

import java.util.HashSet;
import java.util.Set;

/**
 * Dispatches keyframe events and tracks which ones have run in the current cycle.
 * Each animation controller owns its own dispatcher.
 */
final class KeyframeEventDispatcher<T extends IAnimatable> {
    private ISoundListener<T> soundListener;
    private IParticleListener<T> particleListener;
    private ICustomInstructionListener<T> customInstructionListener;
    private final Set<EventKeyFrame<?>> executedKeyFrames = new HashSet<>();

    void registerSoundListener(ISoundListener<T> soundListener) {
        this.soundListener = soundListener;
    }

    void registerParticleListener(IParticleListener<T> particleListener) {
        this.particleListener = particleListener;
    }

    void registerCustomInstructionListener(ICustomInstructionListener<T> customInstructionListener) {
        this.customInstructionListener = customInstructionListener;
    }

    void reset() {
        executedKeyFrames.clear();
    }

    void dispatch(AnimationController<T> controller, double tick, ExecutionContext context) {
        // Read the current animation separately for each category: callbacks may
        // change it or replace listeners while dispatch is in progress.
        if (soundListener != null) {
            for (EventKeyFrame<String> soundKeyFrame : controller.currentAnimation.soundKeyFrames) {
                if (!this.executedKeyFrames.contains(soundKeyFrame) && tick >= soundKeyFrame.getStartTick()) {
                    SoundKeyframeEvent<T> event = new SoundKeyframeEvent<>(controller.animatable, tick,
                            soundKeyFrame.getEventData(), controller, context);
                    soundListener.playSound(event);

                    this.executedKeyFrames.add(soundKeyFrame);
                }
            }
        }
        if (particleListener != null) {
            for (ParticleEventKeyFrame particleEventKeyFrame : controller.currentAnimation.particleKeyFrames) {
                if (!this.executedKeyFrames.contains(
                        particleEventKeyFrame) && tick >= particleEventKeyFrame.getStartTick()) {
                    ParticleKeyFrameEvent<T> event = new ParticleKeyFrameEvent<>(controller.animatable, tick,
                            particleEventKeyFrame.effect, particleEventKeyFrame.locator, particleEventKeyFrame.script,
                            controller, context);
                    particleListener.summonParticle(event);

                    this.executedKeyFrames.add(particleEventKeyFrame);
                }
            }
        }
        if (customInstructionListener != null) {
            for (EventKeyFrame<Evaluatable> customInstructionKeyFrame : controller.currentAnimation.customInstructionKeyframes) {
                if (!this.executedKeyFrames.contains(
                        customInstructionKeyFrame) && tick >= customInstructionKeyFrame.getStartTick()) {
                    CustomInstructionKeyframeEvent<T> event = new CustomInstructionKeyframeEvent<>(controller.animatable,
                            tick, customInstructionKeyFrame.getEventData(), controller, context);
                    customInstructionListener.executeInstruction(event);

                    this.executedKeyFrames.add(customInstructionKeyFrame);
                }
            }
        }
    }
}
