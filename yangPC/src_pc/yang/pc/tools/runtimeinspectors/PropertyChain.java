package yang.pc.tools.runtimeinspectors;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;


public abstract class PropertyChain extends InspectorComponent {

	private PropertiesPanel mPanel;
	protected InspectorComponent mComponents[];

	protected abstract InspectorComponent[] createComponents();

	@Override
	protected void postInit() {
		mPanel = new PropertiesPanel(mPropPanel);
		mPanel.setLayout(new BoxLayout(mPanel,BoxLayout.Y_AXIS));
		mComponents = createComponents();
		for(InspectorComponent component:mComponents) {
			InspectorItem item = mPanel.add(component);
//			item.setBorder(InspectorGUIDefinitions.SUB_PROPERTY_BORDER);
		}
	}

	@Override
	protected Component getComponent() {
		return mPanel;
	}

	@Override
	protected void refreshOutValue() {
		for(InspectorComponent component:mComponents) {
			component.refreshOutValue();
		}
	}

	@Override
	protected void postValueChanged() {
		for(InspectorComponent component:mComponents) {
			component.postValueChanged();
		}
	}

	@Override
	protected boolean useDefaultCaptionLayout() {
		return false;
	}

}
