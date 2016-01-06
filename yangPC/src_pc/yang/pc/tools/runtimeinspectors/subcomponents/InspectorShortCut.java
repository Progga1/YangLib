package yang.pc.tools.runtimeinspectors.subcomponents;

import yang.pc.tools.runtimeinspectors.InspectorComponent;

public class InspectorShortCut {

	public int mKey = -1,mCode = 0;
	public InspectorComponent mComponent;

	public InspectorShortCut(int key,InspectorComponent component,int code) {
		mKey = key;
		mCode = code;
		mComponent = component;
	}

}
