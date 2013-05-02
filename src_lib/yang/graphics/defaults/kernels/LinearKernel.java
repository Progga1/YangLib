package yang.graphics.defaults.kernels;

import yang.graphics.interfaces.KernelFunction;

public class LinearKernel extends KernelFunction {

	@Override
	protected float computeWeight(float centerDistance) {

		if(centerDistance>1)
			return 0;
		else
			return 1-centerDistance;
	}
	
}
