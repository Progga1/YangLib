package yang.pc.tools.runtimeproperties;

import java.awt.Component;

public abstract class RuntimePropertyComponent {

	public String mName;
	public RuntimePropertiesInspector mPropPanel;
	private boolean mEnabled = true;
	private boolean mWasChanged = false;

	protected abstract void postInit();
	protected abstract void postSetValue(Object value);
	protected abstract Object getValue();

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
		if(mWasChanged) {
			Object val = object.setProperty(mName,this.getValue());
			if(val!=null)
				postSetValue(val);
		}else{
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
		mWasChanged = false;
	}

	protected void setChanged() {
		mWasChanged = true;
	}

	protected void setEnabled(boolean enabled) {
		getComponent().setEnabled(enabled);
	}

}
