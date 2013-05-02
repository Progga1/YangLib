package yang.android.graphics;

import yang.model.Factory;
import yang.model.TransformationMatrix;

public class AndroidFactory extends Factory{

	@Override
	protected TransformationMatrix createTransformationMatrix() {
		return new AndroidTransformationMatrix();
	}
	
}
