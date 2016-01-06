package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorPanel;
import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.pc.tools.runtimeinspectors.subcomponents.InspectorSubHeading;

public class PropertyInspectedObject extends InspectorComponent {

	protected InspectorPanel mInspPanel;
	protected InspectionInterface mInspectedObject;
	protected InspectorSubHeading mTopLevelPanel;

	public PropertyInspectedObject(InspectorPanel panel) {
		mInspPanel = panel;
	}

	@Override
	protected void postInit() {
		if(!isReferenced())
			throw new RuntimeException("Only referenced allowed for inspected object property.");
		mTopLevelPanel = new InspectorSubHeading(mInspPanel.getPropertiesPanel());
	}

	@Override
	public void setValueReference(Object reference) {
		mInspectedObject = (InspectionInterface)reference;
		mTopLevelPanel.setName(mName+": "+mInspectedObject.getName());
	}

	@Override
	public void refreshInValue() {
		mInspPanel.getPropertiesPanel().setValuesByObject(mInspectedObject);
	}

	@Override
	protected Component getComponent() {
		return mTopLevelPanel;
	}

	@Override
	protected boolean useDefaultCaptionLayout() {
		return false;
	}

}
