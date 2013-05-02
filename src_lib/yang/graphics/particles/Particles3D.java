package yang.graphics.particles;

import yang.graphics.defaults.Default3DGraphics;

public class Particles3D<ParticleType extends Particle> extends ParticleRingBuffer3D<ParticleType> {

	private Class<ParticleType> mParticleClass;
	
	public Particles3D(Class<ParticleType> particleClass) {
		mParticleClass = particleClass;
	}
	
	public Particles3D<ParticleType> init(Default3DGraphics graphics,int maxParticleCount) {
		super.init(graphics,maxParticleCount);
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
