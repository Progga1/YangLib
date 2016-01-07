package yang.pc.tools.runtimeinspectors;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;

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
		mTopLevelPanel = new InspectorSubHeading(mPropPanel,false);
		mTopLevelPanel.setCollapsed(true);
		mPropPanel.setParent(this);
	}

	@Override
	protected String getFileOutputString() {
		String result = "{\r\n";
		for(InspectorItem item:mPropPanel.getItems()) {
			InspectorComponent component = item.getInspectorComponent();
			String subStr = component.getFileOutputString();
			if(subStr!=null) {
				result += component.mName+"="+subStr+"\r\n";
			}
		}
		result += "}";
		return result;
	}

	@Override
	public void loadFromStream(String value,BufferedReader reader) throws IOException {
		mPropPanel.loadFromStream(mInspectedObject,reader);
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
		mPropPanel.setValuesByObject(mInspectedObject);
	}

	@Override
	protected InspectionInterface getTargetObject() {
		return mInspectedObject;
	}

	@Override
	protected boolean isComponentsVisible() {
		return !mTopLevelPanel.isCollapsed();
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

	@Override
	protected void notifyValueUserInput() {
		mPropPanel.notifyValueUserInput();
	}

	//TODO propagate set to sub components

}
