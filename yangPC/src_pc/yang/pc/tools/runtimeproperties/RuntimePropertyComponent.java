package yang.pc.tools.runtimeproperties;

import java.awt.Component;

public abstract class RuntimePropertyComponent {

	public String mName;
	public RuntimePropertiesInspector mPropPanel;
	private boolean mEnabled = true;

	protected abstract void postInit();
	protected abstract void postSetValue(Object value);
	protected abstract Object getValue();
	protected abstract void setEnabled(boolean enabled);
	public abstract Component getComponent();

	public final void init(RuntimePropertiesInspector propertyPanel, String name) {
		mPropPanel = propertyPanel;
		mName = name;
		postInit();
	}

	public String getName() {
		return mName;
	}

	public void setValueByObject(PropertyInterface object) {
		Object val = object.getProperty(mName);
		if(val!=null) {
			if(!mEnabled)
				setEnabled(true);
			postSetValue(val);
		}else{
			if(mEnabled)
				setEnabled(false);
		}
	}

}
