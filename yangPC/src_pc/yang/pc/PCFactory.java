package yang.pc;

import yang.model.Factory;
import yang.model.TransformationMatrix;

public class PCFactory extends Factory {

	@Override
	protected TransformationMatrix createTransformationMatrix() {
		return new PCTransformationMatrix();
	}

}
