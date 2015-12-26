package yang.pc.tools.runtimeproperties;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import yang.util.YangList;

public class InspectorPanel {

	protected JPanel mTopLevelPanel;
	protected JPanel mPropertiesPanel;
	protected InspectorFrame mFrame;
	protected InspectorManager mManager;
	protected JScrollPane mScrollPane;
	protected YangList<InspectorProperty> mComponents = new YangList<InspectorProperty>();

	protected InspectorPanel(InspectorFrame frame) {
		mManager = frame.mManager;
		mFrame = frame;
//		mLayout = new FlowLayout(FlowLayout.LEADING,5,5);
		mTopLevelPanel = new JPanel();
		mTopLevelPanel.setLayout(new BorderLayout());
		mPropertiesPanel = new JPanel();
		mPropertiesPanel.setLayout(new BoxLayout(mPropertiesPanel,BoxLayout.Y_AXIS));
		mTopLevelPanel.add(mPropertiesPanel,BorderLayout.NORTH);
		mScrollPane = new JScrollPane(mTopLevelPanel);
	}

	public void setVisible(boolean visible) {
		mTopLevelPanel.setVisible(visible);
	}

	protected void refreshLayout() {
		mPropertiesPanel.removeAll();
		for(InspectorProperty component:mComponents) {
			mPropertiesPanel.add(component);
		}
		mTopLevelPanel.setBackground(InspectorGUIDefinitions.CL_UNUSED_SPACE);
	}

	public void registerPropertyCostum(String name,Class<? extends InspectorComponent> componentType) {
		InspectorComponent rtpComponent;
		try {
			rtpComponent = componentType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		rtpComponent.init(this, name);
		InspectorProperty rtpHolder = new InspectorProperty(this,rtpComponent);
		mComponents.add(rtpHolder);
		refreshLayout();
	}

	public void registerProperty(String name,Class<?> type) {
		Class<? extends InspectorComponent> comp = mManager.getDefaultComponent(type);
		if(comp==null)
			throw new RuntimeException("No default component for type: "+type.getName());
		registerPropertyCostum(name,comp);
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

	public void setValues(InspectionInterface object) {
		for(InspectorProperty component:mComponents) {
			component.getRTPComponent().setValueByObject(object);
		}
	}

}
