/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib3.core.keyframe;

import software.bernie.geckolib3.core.processor.IBone;

import java.util.LinkedList;

public class BoneAnimationQueue
{
	public final IBone bone;
	public LinkedList<AnimationPoint> rotationXQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> rotationYQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> rotationZQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> positionXQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> positionYQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> positionZQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> scaleXQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> scaleYQueue = new LinkedList<>();
	public LinkedList<AnimationPoint> scaleZQueue = new LinkedList<>();

	public BoneAnimationQueue(IBone bone)
	{
		this.bone = bone;
	}
}
