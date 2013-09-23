package yang.graphics.defaults;

import yang.graphics.skeletons.animations.Animation;
import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.physics.massaggregation.MassAggregation;

public class DefaultAnimationPlayer extends AnimationPlayer<Animation<?>>{

	public DefaultAnimationPlayer(MassAggregation skeleton, Animation<?> startAnimation) {
		super(skeleton, startAnimation);
	}

}
