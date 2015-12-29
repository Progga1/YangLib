package yang.pc.tools.runtimeinspectors;

import java.awt.Component;

public abstract class InspectorComponent {

	public String mName;
	protected InspectorPanel mPropPanel;
	protected InspectorComponent mParent = null;
	protected boolean mWasChanged = false;
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

	public final void init(InspectorComponent parent, String name, Object valueReference) {
		init(parent.mPropPanel,name,valueReference!=null);
		if(valueReference!=null)
			setValueReference(valueReference);
		setParent(parent);
	}

	protected void setParent(InspectorComponent parent) {
		mParent = parent;
	}

	public String getName() {
		return mName;
	}

	protected void postValueChanged() {

	}

	protected void refreshOutValue() {

	}

	protected void setValueReference(Object reference) {

	}

	public Object getValueReference() {
		return null;
	}

	protected void update(InspectionInterface object,boolean forceUpdate) {
		if(mWasChanged) {
			refreshOutValue();
			if(!mReferenced)
				object.setProperty(mName,this);
		}else{
			if(!hasFocus() || forceUpdate) {
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
		if(mParent!=null)
			mParent.notifyValueUserInput();
	}

	public boolean hasFocus() {
		return getComponent().hasFocus();
	}

	private String notSup(String typeName) {
		return typeName+" not supported by "+getClass().getName();
	}

	public final Object getValue() {
//		refreshValue();
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
		Object val = getValue();
		if(val!=null)
			return val.toString();
		else
			return "<unknown>";
	}

	protected boolean useDefaultCaptionLayout() {
		return true;
	}

}
