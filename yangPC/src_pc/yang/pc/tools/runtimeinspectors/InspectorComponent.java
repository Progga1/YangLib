package yang.pc.tools.runtimeinspectors;

import java.awt.Component;

public abstract class InspectorComponent {

	public String mName;
	public InspectorPanel mPropPanel;
	protected boolean mWasChanged = false;
	protected Object mValueRef = null;
	private boolean mReferenced;
	private boolean mVisible = true;

	protected abstract void postInit();

	protected abstract Component getComponent();

	public final void init(InspectorPanel propertyPanel, String name, boolean referenced) {
		mPropPanel = propertyPanel;
		mName = name;
		mReferenced = referenced;
		postInit();
	}

	public String getName() {
		return mName;
	}

	protected void postValueChanged() {

	}

	protected void refreshValue() {

	}

	protected void setValueReference(Object reference) {

	}

	protected Object getValueReference() {
		return null;
	}

	protected void update(InspectionInterface object,boolean forceUpdate) {
		if(mWasChanged) {
			if(mReferenced) {
				refreshValue();
			}else{
				Object val = object.setProperty(mName,this);
			}
//			if(val!=null) {
//				if(mValueRef==null || mValueRef!=val)
//					setValueFrom(val);
//				refresh();
//			}
		}else{
			if(!hasFocus() || forceUpdate) {
//				Object val = object.getReferencedProperty(mName);
				if(mReferenced) {
					Object reference = object.getReferencedProperty(mName,this);
					if(reference==null) {
						setVisible(false);
						mVisible = false;
					}else{
						setValueReference(reference);
						postValueChanged();
						if(!mVisible) {
							setVisible(true);
							mVisible = true;
						}
					}
				}else{
					object.readProperty(mName,this);
					postValueChanged();
				}

//				if(val!=null) {
//					if(!mEnabled)
//						setEnabled(true);
//					postSetValue(val);
//				}else{
//					if(mEnabled)
//						setEnabled(false);
//				}
			}
		}
		mWasChanged = false;
	}

	public boolean isReferenced() {
		return mReferenced;
	}

	public void setEnabled(boolean enabled) {
		getComponent().setEnabled(enabled);
	}

	public void setVisible(boolean visible) {
		getComponent().setVisible(visible);
	}

	protected void notifyValueUserInput() {
		mWasChanged = true;
	}

	public boolean hasFocus() {
		return getComponent().hasFocus();
	}

	private String notSup(String typeName) {
		return typeName+" not supported by "+getClass().getName();
	}

	public final Object getValue() {
		refreshValue();
		Object reference = getValueReference();
		if(reference==null)
			throw new RuntimeException("No object reference in type "+getClass().getName());
		else
			return reference;
	}

	public void setValueFrom(Object value) {
		throw new RuntimeException("No value by object possible in type"+getClass().getName());
	}

	public boolean getBool() {
		throw new RuntimeException(notSup("Bool"));
	}

	public void setBool(boolean bool) {
		throw new RuntimeException(notSup("Bool"));
	}

	public float getFloat() {
		throw new RuntimeException(notSup("Float"));
	}

	public void setFloat(float value) {
		throw new RuntimeException(notSup("Float"));
	}

	public double getDouble() {
		throw new RuntimeException(notSup("Double"));
	}

	public void setDouble(double value) {
		throw new RuntimeException(notSup("Double"));
	}

	public String getString() {
		throw new RuntimeException(notSup("String"));
	}

	public void setString() {
		throw new RuntimeException(notSup("String"));
	}

	public String valueToString() {
		if(mValueRef!=null)
			return mValueRef.toString();
		else
			return "<unknown>";
	}

}
