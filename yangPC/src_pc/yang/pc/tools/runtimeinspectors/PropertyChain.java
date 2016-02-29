package yang.pc.tools.runtimeinspectors;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;

import yang.pc.tools.runtimeinspectors.subcomponents.InspectorSubHeading;


public abstract class PropertyChain extends InspectorComponent {

	private PropertiesPanel mMainPanel;
	private InspectorSubHeading mTopLevelPanel;
	protected InspectorComponent mComponents[];
	public boolean mIndividualFocus = false;

	protected abstract InspectorComponent[] createComponents();

	@Override
	protected void postInit() {
		mMainPanel = new PropertiesPanel(mInspectorPanel);
		mComponents = createComponents();
		for(InspectorComponent component:mComponents) {
			mMainPanel.add(component);
		}
		mTopLevelPanel = new InspectorSubHeading(mMainPanel,mName,true);
	}

	@Override
	protected String getFileOutputString() {
		if(mCurObject==null || mExcludeFromFileIO || isReadOnly())
			return null;
		String result = "{\r\n";
		for(InspectorComponent component:mComponents) {
			//String subStr = component.getStringOutput(object);
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
		mMainPanel.loadFromStream(null,reader);
	}

	@Override
	protected Component getComponent() {
		return mTopLevelPanel;
	}

	@Override
	protected void refreshOutValue() {
		for(InspectorComponent component:mComponents) {
			component.refreshOutValue();
		}
	}

	@Override
	protected void refreshInValue() {
		for(InspectorComponent component:mComponents) {
			component.refreshInValue();
		}
	}

	@Override
	protected void updateGUI() {
		for(InspectorComponent component:mComponents) {
			if(!component.hasFocus())
				component.updateGUI();
		}
	}

	@Override
	protected boolean useDefaultCaptionLayout() {
		return false;
	}

	@Override
	public boolean hasFocus() {
		if(mIndividualFocus)
			return false;
		for(InspectorComponent component:mComponents) {
			if(component.hasFocus())
				return true;
		}
		return false;
	}

	@Override
	public boolean isCollapsed() {
		return mTopLevelPanel.isCollapsed();
	}

	@Override
	public PropertyChain setCollapsed(boolean collapsed) {
		mTopLevelPanel.setCollapsed(collapsed);
		return this;
	}

	@Override
	public InspectorComponent handleShortCut(int code) {
		for(InspectorComponent component:mComponents) {
			InspectorComponent handledBy = component.handleShortCut(code);
			if(handledBy!=null)
				return handledBy;
		}
		return null;
	}

	//TODO propagate set to sub components

}
