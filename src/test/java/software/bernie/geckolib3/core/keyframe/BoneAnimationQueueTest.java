package software.bernie.geckolib3.core.keyframe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BoneAnimationQueueTest {
    @Test
    void keepsAllNineQueuesIndependentWithinOneInstance() {
        BoneAnimationQueue queues = new BoneAnimationQueue(null);
        List<Queue<AnimationPoint>> fields = Arrays.asList(
                queues.rotationXQueue, queues.rotationYQueue, queues.rotationZQueue,
                queues.positionXQueue, queues.positionYQueue, queues.positionZQueue,
                queues.scaleXQueue, queues.scaleYQueue, queues.scaleZQueue);
        for (int i = 0; i < fields.size(); i++) {
            for (int j = i + 1; j < fields.size(); j++) {
                assertNotSame(fields.get(i), fields.get(j), "queues " + i + " and " + j);
            }
        }
    }

    @ParameterizedTest(name = "{0} is independent across instances")
    @MethodSource("queueFields")
    void queuesAreIndependentAcrossInstances(String axis, QueueAccessor accessor) {
        BoneAnimationQueue queues = new BoneAnimationQueue(null);
        BoneAnimationQueue other = new BoneAnimationQueue(null);
        AnimationPoint point = point(1);
        Queue<AnimationPoint> first = accessor.get(queues);
        Queue<AnimationPoint> second = accessor.get(other);
        assertNotSame(first, second, axis);
        first.add(point);
        assertTrue(second.isEmpty(), axis);
    }

    @ParameterizedTest(name = "{0} preserves FIFO")
    @MethodSource("queueFields")
    void queuesPreserveFifoForAddPeekPollAndEmpty(String axis, QueueAccessor accessor) {
        BoneAnimationQueue queues = new BoneAnimationQueue(null);
        AnimationPoint first = point(1);
        AnimationPoint second = point(2);
        Queue<AnimationPoint> queue = accessor.get(queues);

        assertNotNull(queue, axis);
        assertTrue(queue.isEmpty(), axis);
        assertNull(queue.peek(), axis);
        assertNull(queue.poll(), axis);
        queue.add(first);
        queue.add(second);
        assertSame(first, queue.peek(), axis);
        assertSame(first, queue.poll(), axis);
        assertSame(second, queue.peek(), axis);
        assertSame(second, queue.poll(), axis);
        assertTrue(queue.isEmpty(), axis);
        assertNull(queue.peek(), axis);
        assertNull(queue.poll(), axis);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> queueFields() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("rotation X", (QueueAccessor) q -> q.rotationXQueue),
                org.junit.jupiter.params.provider.Arguments.of("rotation Y", (QueueAccessor) q -> q.rotationYQueue),
                org.junit.jupiter.params.provider.Arguments.of("rotation Z", (QueueAccessor) q -> q.rotationZQueue),
                org.junit.jupiter.params.provider.Arguments.of("position X", (QueueAccessor) q -> q.positionXQueue),
                org.junit.jupiter.params.provider.Arguments.of("position Y", (QueueAccessor) q -> q.positionYQueue),
                org.junit.jupiter.params.provider.Arguments.of("position Z", (QueueAccessor) q -> q.positionZQueue),
                org.junit.jupiter.params.provider.Arguments.of("scale X", (QueueAccessor) q -> q.scaleXQueue),
                org.junit.jupiter.params.provider.Arguments.of("scale Y", (QueueAccessor) q -> q.scaleYQueue),
                org.junit.jupiter.params.provider.Arguments.of("scale Z", (QueueAccessor) q -> q.scaleZQueue));
    }

    @FunctionalInterface
    interface QueueAccessor { Queue<AnimationPoint> get(BoneAnimationQueue queues); }

    private static AnimationPoint point(double value) {
        return new AnimationPoint(null, value, value + 1, value, value + 1);
    }
}
