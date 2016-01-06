package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorPanel;
import yang.pc.tools.runtimeinspectors.PropertiesPanel;
import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.pc.tools.runtimeinspectors.subcomponents.InspectorSubHeading;

public class PropertyInspectedObject extends InspectorComponent {

	protected PropertiesPanel mOrigPropPanel;
	protected PropertiesPanel mPropPanel;
	protected InspectionInterface mInspectedObject;
	protected InspectorSubHeading mTopLevelPanel;

	public PropertyInspectedObject(PropertiesPanel panel) {
		mOrigPropPanel = panel;
	}

	public PropertyInspectedObject(InspectorPanel panel) {
		this(panel.getPropertiesPanel());
	}

	@Override
	protected void postInit() {
		if(!isReferenced())
			throw new RuntimeException("Only referenced allowed for inspected object property.");
		mPropPanel = mOrigPropPanel.clone();
		mTopLevelPanel = new InspectorSubHeading(mPropPanel);
		mTopLevelPanel.setCollapsed(true);
	}

	@Override
	public void setValueReference(Object reference) {
		mInspectedObject = (InspectionInterface)reference;
		if(mName.equals(mInspectedObject.getName()))
			mTopLevelPanel.setName(mName);
		else
			mTopLevelPanel.setName(mName+": "+mInspectedObject.getName());
	}

	@Override
	public void refreshInValue() {
		if(!mTopLevelPanel.isCollapsed())
			mPropPanel.setValuesByObject(mInspectedObject);
	}

	@Override
	protected Component getComponent() {
		return mTopLevelPanel;
	}

	@Override
	protected boolean useDefaultCaptionLayout() {
		return false;
	}

	@Override
	public PropertyInspectedObject clone() {
		return new PropertyInspectedObject(mOrigPropPanel);
	}

	//TODO propagate set to sub components

}
