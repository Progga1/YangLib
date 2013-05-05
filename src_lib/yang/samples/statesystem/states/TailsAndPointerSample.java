package yang.samples.statesystem.states;

import yang.events.eventtypes.YangInputEvent;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class TailsAndPointerSample extends YangProgramState<YangProgramStateSystem> {

	@Override
	public void rawEvent(YangInputEvent event) {
		
	}

	@Override
	public void step(float deltaTime) {
		
	}

	@Override
	public void draw() {
		mGraphics.clear(0, 0, 0.1f);
		
	}

}
