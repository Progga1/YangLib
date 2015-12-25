package yang.pc.tools.runtimeproperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import yang.util.YangList;

public class RuntimePropertiesInspector {

	protected JPanel mTopLevelPanel;
	protected JPanel mPropertiesPanel;
	protected RuntimePropertiesManager mManager;
	protected YangList<RuntimePropertyCaption> mComponents = new YangList<RuntimePropertyCaption>();

	protected RuntimePropertiesInspector(RuntimePropertiesManager manager) {
		mManager = manager;
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
		for(RuntimePropertyCaption component:mComponents) {
			mPropertiesPanel.add(component);
		}
		mTopLevelPanel.setBackground(Color.blue);
	}

	public void registerPropertyCostum(String name,Class<? extends RuntimePropertyComponent> componentType) {
		RuntimePropertyComponent rtpComponent;
		try {
			rtpComponent = componentType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		rtpComponent.init(this, name);
		RuntimePropertyCaption rtpHolder = new RuntimePropertyCaption(this,rtpComponent);
		mComponents.add(rtpHolder);
		refreshLayout();
	}

	public void registerProperty(String name,Class<?> type) {
		registerPropertyCostum(name,mManager.getDefaultComponent(type));
	}

	public int getDefaultCaptionWidth() {
		return 180;
	}

	public int getDefaultComponentHeight() {
		return 24;
	}

	public JPanel getPanel() {
		return mTopLevelPanel;
	}

	public void setValues(PropertyInterface object) {
		for(RuntimePropertyCaption component:mComponents) {
			component.getRTPComponent().setValueByObject(object);
		}
	}

}
