package yang.samples.statesystem.states;

import yang.events.eventtypes.YangEvent;
import yang.samples.statesystem.SampleState;

public class TailsAndPointerSample extends SampleState {

	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void step(float deltaTime) {
		
	}

	@Override
	public void draw() {
		mGraphics.clear(0, 0, 0.1f);
		
	}

}
