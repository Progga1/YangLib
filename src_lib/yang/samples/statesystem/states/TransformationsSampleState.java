package yang.samples.statesystem.states;

import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleState;

public class TransformationsSampleState extends SampleState {

	private YangMatrix mTrafo;
	
	@Override
	public void initGraphics() {
		mTrafo = new YangMatrix();
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		
	}

}
