package yang.graphics.defaults.particles;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.particles.EffectParticle;
import yang.graphics.particles.Particles3D;

public class DefaultParticles3D extends Particles3D<EffectParticle> {
	
	public DefaultParticles3D() {
		super(EffectParticle.class);
	}
	
	public DefaultParticles3D init(Default3DGraphics graphics,int maxParticleCount) {
		super.init(graphics,maxParticleCount);
		return this;
	}

}
