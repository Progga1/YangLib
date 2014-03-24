package yang.graphics.particles;

import yang.graphics.defaults.DefaultGraphics;

public class Particles2D<ParticleType extends Particle> extends ParticleRingBuffer2D<ParticleType> {

	private Class<ParticleType> mParticleClass;

	public Particles2D(Class<ParticleType> particleClass) {
		mParticleClass = particleClass;
	}

	@Override
	public Particles2D<ParticleType> init(DefaultGraphics graphics, int maxParticleCount) {
		super.init(graphics, maxParticleCount);
		return this;
	}

	@Override
	protected ParticleType createParticle() {
		try {
			return mParticleClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}



}
