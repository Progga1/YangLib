package yang.pc.tools.runtimeinspectors;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.BoxLayout;

import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;


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
			mPanel.add(component);
		}
	}

//	@Override
//	protected String getStringOutput(InspectionInterface object) {
//		super.getStringOutput(object);
//		if(mSaveString==null) {
////			mSaveString = "-->"+mName+"\r\n";
//			mSaveString = "{\r\n";
//			for(InspectorComponent component:mComponents) {
//				//String subStr = component.getStringOutput(object);
//				String subStr = component.mSaveString;
//				if(subStr!=null) {
//					mSaveString += component.mName+"="+subStr+"\r\n";
//				}
//			}
//			mSaveString += "}";
//		}
//
//		return mName+"="+mSaveString;
//	}

	@Override
	protected String getFileOutputString() {
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
		mPanel.loadFromStream(null,reader);
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
	protected void updateGUI() {
		for(InspectorComponent component:mComponents) {
			component.updateGUI();
		}
	}

	@Override
	protected boolean useDefaultCaptionLayout() {
		return false;
	}

	@Override
	public boolean hasFocus() {
		for(InspectorComponent component:mComponents) {
			if(component.hasFocus())
				return true;
		}
		return false;
	}

}
