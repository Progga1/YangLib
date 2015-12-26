package yang.pc.tools.runtimeproperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import yang.util.YangList;

public class InspectorPanel {

	protected JPanel mTopLevelPanel;
	protected JPanel mPropertiesPanel;
	protected InspectorFrame mFrame;
	protected InspectorManager mManager;
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
		return 160;
	}

	public int getDefaultComponentHeight() {
		return 24;
	}

	public JPanel getPanel() {
		return mTopLevelPanel;
	}

	public void setValues(InspectionInterface object) {
		for(InspectorProperty component:mComponents) {
			component.getRTPComponent().setValueByObject(object);
		}
	}

}
