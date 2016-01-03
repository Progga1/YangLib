package yang.pc.tools.runtimeinspectors;

import java.awt.Component;

import yang.pc.tools.runtimeinspectors.subcomponents.CheckLabel;
import yang.pc.tools.runtimeinspectors.subcomponents.CheckLabelListener;

public abstract class InspectorComponent implements CheckLabelListener {

	public String mName;
	protected InspectorPanel mPropPanel;
	protected InspectorItem mHolder;
	protected InspectorComponent mParent = null;
	protected boolean mWasChanged = false;
	private boolean mReferenced;
	private boolean mVisible = true;

	protected abstract void postInit();

	protected abstract Component getComponent();

	public final void init(InspectorPanel panel, String name, boolean referenced) {
		mPropPanel = panel;
		mName = name;
		mReferenced = referenced;
		postInit();
	}

	public final void init(InspectorComponent parent, String name, boolean referenced) {
		init(parent.mPropPanel,name,referenced);
		setParent(parent);
	}

	public void setPreferredOutputType(Class<?> type) {

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

	public void setValueReference(Object reference) {

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
			if(!isVisible())
				return;
			if(!hasFocus() || forceUpdate) {
				if(!mReferenced) {
					object.readProperty(mName,this);
				}
				postValueChanged();
			}
		}
		mWasChanged = false;
	}

	public boolean isVisible() {
		return mVisible;
	}

	public boolean isReferenced() {
		return mReferenced;
	}

	public void setEnabled(boolean enabled) {
		getComponent().setEnabled(enabled);
	}

	public void setVisible(boolean visible) {
		if(mVisible==visible)
			return;
		getComponent().setVisible(visible);
	}

	protected void notifyValueUserInput() {
		mWasChanged = true;
		if(mParent!=null)
			mParent.notifyValueUserInput();
		else
			mPropPanel.notifyValueUserInput();
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

	public void updateReference(InspectionInterface object) {
		Object reference = object.getReferencedProperty(mName,this);
		if(reference==null) {
			setVisible(false);
		}else{
			setValueReference(reference);
			setVisible(true);
		}
	}

	public boolean isLinkingSupported() {
		return false;
	}

	@Override
	public void selectionChanged(CheckLabel sender) {

	}

}
