package yang.pc.tools.runtimeinspectors.interfaces;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorFrame;

public interface InspectorFrameListener {

	public void onSelectObject(int id,InspectorFrame sender);
	public void onShortCut(InspectorComponent component,InspectorFrame sender);

}
