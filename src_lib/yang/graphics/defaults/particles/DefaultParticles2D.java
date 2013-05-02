package yang.graphics.defaults.particles;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.particles.EffectParticle;
import yang.graphics.particles.Particles2D;

public class DefaultParticles2D extends Particles2D<EffectParticle> {

	public DefaultParticles2D() {
		super(EffectParticle.class);
	}
	
	public DefaultParticles2D init(Default2DGraphics graphics, int maxParticleCount) {
		super.init(graphics, maxParticleCount);
		return this;
	}
	
}
