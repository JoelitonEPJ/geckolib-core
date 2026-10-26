package software.bernie.geckolib3.core.controller;

import com.eliotlash.molang.ast.Evaluator;
import com.eliotlash.molang.variables.ExecutionContext;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.keyframe.*;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.snapshot.BoneSnapshot;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AnimationControllerSnapshotTest {
    @Test
    void firstTransitionUsesCapturedSnapshotForEveryAxisAndChannel() {
        TestController controller = controller();
        Bone bone = new Bone("arm", 1, 2, 3, 10, 20, 30, 2, 3, 4);
        BoneSnapshot input = snapshot(bone, 11, 22, 33, 41, 52, 63, 4, 5, 6);
        Animation previous = animation("previous");
        previous.boneAnimations.put("arm", previousWithAllChannels());
        controller.previousAnimation = previous;
        controller.animationQueue.add(animation("target"));

        Evaluator evaluator = new Evaluator();
        controller.process(0, event(), Collections.singletonList(bone), collection(bone, input), evaluator, new ExecutionContext(evaluator), false);

        BoneAnimationQueue queues = controller.getBoneAnimationQueues().get("arm");
        assertNotNull(queues);
        assertSame(bone, queues.bone);
        assertEquals(Arrays.asList(11d, 22d, 33d), starts(queues.rotationXQueue, queues.rotationYQueue, queues.rotationZQueue));
        assertEquals(Arrays.asList(41d, 52d, 63d), starts(queues.positionXQueue, queues.positionYQueue, queues.positionZQueue));
        assertEquals(Arrays.asList(4d, 5d, 6d), starts(queues.scaleXQueue, queues.scaleYQueue, queues.scaleZQueue));
        assertEquals(Arrays.asList(1d, 2d, 3d), ends(queues.rotationXQueue, queues.rotationYQueue, queues.rotationZQueue));
        assertEquals(Arrays.asList(10d, 20d, 30d), ends(queues.positionXQueue, queues.positionYQueue, queues.positionZQueue));
        assertEquals(Arrays.asList(2d, 3d, 4d), ends(queues.scaleXQueue, queues.scaleYQueue, queues.scaleZQueue));
    }

    @Test
    void laterMutationOfInputSnapshotDoesNotChangeCapturedTransitionStart() {
        TestController controller = controller();
        Bone bone = new Bone("arm", 1, 2, 3, 10, 20, 30, 2, 3, 4);
        BoneSnapshot input = snapshot(bone, 11, 22, 33, 41, 52, 63, 4, 5, 6);
        Animation previous = animation("previous");
        previous.boneAnimations.put("arm", previousWithAllChannels());
        controller.previousAnimation = previous;
        controller.animationQueue.add(animation("target"));
        HashMap<String, Pair<IBone, BoneSnapshot>> snapshots = collection(bone, input);
        Evaluator evaluator = new Evaluator();
        controller.process(0, event(), Collections.singletonList(bone), snapshots, evaluator, new ExecutionContext(evaluator), false);

        input.rotationValueX = 999;
        input.positionOffsetY = 999;
        input.scaleValueZ = 999;
        controller.process(1, event(), Collections.singletonList(bone), snapshots, evaluator, new ExecutionContext(evaluator), false);

        BoneAnimationQueue queues = controller.getBoneAnimationQueues().get("arm");
        assertEquals(1d, queues.rotationXQueue.getLast().currentTick);
        assertEquals(11d, queues.rotationXQueue.getLast().animationStartValue);
        assertEquals(52d, queues.positionYQueue.getLast().animationStartValue);
        assertEquals(6d, queues.scaleZQueue.getLast().animationStartValue);
    }

    private static TestController controller() {
        TestController controller = new TestController();
        controller.initializingModel = false;
        return controller;
    }

    private static AnimationEvent<StubAnimatable> event() {
        return new AnimationEvent<>(new StubAnimatable(), 0, 0, 0, false, Collections.emptyList());
    }

    @SafeVarargs
    private static List<Double> starts(Queue<AnimationPoint>... queues) {
        List<Double> values = new ArrayList<>();
        for (Queue<AnimationPoint> queue : queues) values.add(queue.peek().animationStartValue);
        return values;
    }

    @SafeVarargs
    private static List<Double> ends(Queue<AnimationPoint>... queues) {
        List<Double> values = new ArrayList<>();
        for (Queue<AnimationPoint> queue : queues) values.add(queue.peek().animationEndValue);
        return values;
    }

    private static Animation animation(String name) {
        Animation animation = new Animation();
        animation.animationName = name;
        animation.animationLength = 20d;
        animation.boneAnimations = new HashMap<>();
        return animation;
    }

    private static BoneAnimation previousWithAllChannels() {
        BoneAnimation result = new BoneAnimation();
        result.boneName = "arm";
        result.rotationKeyFrames = new VectorKeyFrameList<>();
        result.positionKeyFrames = new VectorKeyFrameList<>();
        result.scaleKeyFrames = new VectorKeyFrameList<>();
        result.rotationKeyFrames.xKeyFrames.add(new KeyFrame<>(10d, null, null));
        result.positionKeyFrames.xKeyFrames.add(new KeyFrame<>(10d, null, null));
        result.scaleKeyFrames.xKeyFrames.add(new KeyFrame<>(10d, null, null));
        return result;
    }

    private static BoneSnapshot snapshot(IBone bone, float rx, float ry, float rz, float px, float py, float pz, float sx, float sy, float sz) {
        BoneSnapshot value = new BoneSnapshot(bone);
        value.rotationValueX = rx; value.rotationValueY = ry; value.rotationValueZ = rz;
        value.positionOffsetX = px; value.positionOffsetY = py; value.positionOffsetZ = pz;
        value.scaleValueX = sx; value.scaleValueY = sy; value.scaleValueZ = sz;
        return value;
    }

    private static HashMap<String, Pair<IBone, BoneSnapshot>> collection(IBone bone, BoneSnapshot value) {
        HashMap<String, Pair<IBone, BoneSnapshot>> result = new HashMap<>();
        result.put(bone.getName(), Pair.of(bone, value));
        return result;
    }

    private static class TestController extends AnimationController<StubAnimatable> {
        TestController() { super(new StubAnimatable(), "test", 10, event -> PlayState.CONTINUE); }
    }

    private static class StubAnimatable implements IAnimatable {
        public void registerControllers(software.bernie.geckolib3.core.manager.AnimationData data) { }
        public software.bernie.geckolib3.core.manager.AnimationFactory getFactory() { return null; }
    }

    private static class Bone implements IBone {
        final String name;
        final float[] v = new float[9];
        BoneSnapshot initialSnapshot;
        Bone(String name, float rx, float ry, float rz, float px, float py, float pz, float sx, float sy, float sz) {
            this.name = name; v[0]=rx;v[1]=ry;v[2]=rz;v[3]=px;v[4]=py;v[5]=pz;v[6]=sx;v[7]=sy;v[8]=sz;
            initialSnapshot = new BoneSnapshot(this);
        }
        float g(int i) { return v[i]; } void s(int i,float f) { v[i]=f; }
        public float getRotationX(){return g(0);} public float getRotationY(){return g(1);} public float getRotationZ(){return g(2);}
        public float getPositionX(){return g(3);} public float getPositionY(){return g(4);} public float getPositionZ(){return g(5);}
        public float getScaleX(){return g(6);} public float getScaleY(){return g(7);} public float getScaleZ(){return g(8);}
        public void setRotationX(float f){s(0,f);} public void setRotationY(float f){s(1,f);} public void setRotationZ(float f){s(2,f);}
        public void setPositionX(float f){s(3,f);} public void setPositionY(float f){s(4,f);} public void setPositionZ(float f){s(5,f);}
        public void setScaleX(float f){s(6,f);} public void setScaleY(float f){s(7,f);} public void setScaleZ(float f){s(8,f);}
        public void setPivotX(float f){} public void setPivotY(float f){} public void setPivotZ(float f){}
        public float getPivotX(){return 0;} public float getPivotY(){return 0;} public float getPivotZ(){return 0;}
        public boolean isHidden(){return false;} public void setHidden(boolean b){} public void setModelRendererName(String n){}
        public void saveInitialSnapshot(){initialSnapshot=new BoneSnapshot(this);} public BoneSnapshot getInitialSnapshot(){return initialSnapshot;} public String getName(){return name;}
    }
}
