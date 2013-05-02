package yang.graphics.defaults.kernels;

import yang.graphics.interfaces.KernelFunction;

public class CircleKernel extends KernelFunction {

	@Override
	protected float computeWeight(float centerDistance) {

		if(centerDistance>1)
			return 0;
		else
			return (float)Math.sqrt(1-centerDistance);
	}
	
}
