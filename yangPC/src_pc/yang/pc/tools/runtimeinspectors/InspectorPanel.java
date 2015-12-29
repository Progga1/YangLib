package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class InspectorPanel {

	protected JPanel mTopLevelPanel;
	protected PropertiesPanel mPropertiesPanel;
	protected InspectorFrame mFrame;
	protected InspectorManager mManager;
	protected JScrollPane mScrollPane;
	protected InspectionInterface mCurObject = null;

	protected InspectorPanel(InspectorFrame frame) {
		mManager = frame.mManager;
		mFrame = frame;
//		mLayout = new FlowLayout(FlowLayout.LEADING,5,5);
		mTopLevelPanel = new JPanel();
		mTopLevelPanel.setLayout(new BorderLayout());
		mPropertiesPanel = new PropertiesPanel(this);
		mTopLevelPanel.add(mPropertiesPanel,BorderLayout.NORTH);
		mTopLevelPanel.setBackground(InspectorGUIDefinitions.CL_UNUSED_SPACE);
		mScrollPane = new JScrollPane(mTopLevelPanel);
	}

	public void setVisible(boolean visible) {
		mTopLevelPanel.setVisible(visible);
	}

//	public void registerPropertyCostum(String name,Class<? extends InspectorComponent> componentType) {
//		InspectorComponent rtpComponent;
//		try {
//			rtpComponent = componentType.newInstance();
//		} catch (InstantiationException | IllegalAccessException e) {
//			throw new RuntimeException(e);
//		}
//		rtpComponent.init(this, name);
//		InspectorProperty rtpHolder = new InspectorProperty(this,rtpComponent);
//		mComponents.add(rtpHolder);
//		refreshLayout();
//	}
//
//	public void registerProperty(String name,Class<?> type) {
//		Class<? extends InspectorComponent> comp = mManager.getDefaultComponent(type);
//		if(comp==null)
//			throw new RuntimeException("No default component for type: "+type.getName());
//		registerPropertyCostum(name,comp);
//	}

	public void registerProperty(String name,InspectorComponent inspectorComponent) {
		inspectorComponent.init(this, name, false);
		InspectorItem rtpHolder = new InspectorItem(this,inspectorComponent);
		mPropertiesPanel.add(rtpHolder);
	}

	public void registerProperty(String name,Class<?> type) {
		InspectorComponent comp = mManager.createDefaultComponentInstance(type);
		if(comp==null)
			throw new RuntimeException("No default component for type: "+type.getName());
		registerProperty(name,comp);
	}

	public void registerPropertyReferenced(String name,InspectorComponent inspectorComponent) {
		inspectorComponent.init(this, name, true);
		InspectorItem rtpHolder = new InspectorItem(this,inspectorComponent);
		mPropertiesPanel.add(rtpHolder);
	}

	public void registerPropertyReferenced(String name,Class<?> type) {
		InspectorComponent comp = mManager.createDefaultComponentInstance(type);
		if(comp==null)
			throw new RuntimeException("No default component for type: "+type.getName());
		registerPropertyReferenced(name,comp);
	}

	public int getCaptionWidth() {
		return InspectorGUIDefinitions.DEFAULT_CAPTION_WIDTH;
	}

	public int getDefaultComponentHeight() {
		return InspectorGUIDefinitions.DEFAULT_COMPONENT_HEIGHT;
	}

	public Component getComponent() {
//		return mTopLevelPanel;
		return mScrollPane;
	}

	public void setValuesByObject(InspectionInterface object) {
		if(mCurObject==object) {
			for(InspectorItem component:mPropertiesPanel.getItems()) {
				if(!component.getInspectorComponent().isReferenced() || component.getInspectorComponent().mWasChanged)
					component.getInspectorComponent().update(object,false);
			}
		}else{
			for(InspectorItem component:mPropertiesPanel.getItems()) {
				component.getInspectorComponent().update(object,true);
			}
		}
		mCurObject = object;
	}

}
