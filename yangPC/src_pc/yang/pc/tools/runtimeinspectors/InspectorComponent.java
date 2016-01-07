package yang.pc.tools.runtimeinspectors;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;

import yang.model.wrappers.DoubleInterface;
import yang.model.wrappers.FloatInterface;
import yang.model.wrappers.IntInterface;
import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.pc.tools.runtimeinspectors.subcomponents.CheckLabel;
import yang.pc.tools.runtimeinspectors.subcomponents.CheckLabelListener;

public abstract class InspectorComponent implements CheckLabelListener,IntInterface,FloatInterface,DoubleInterface {

	public String mName;
	protected InspectorPanel mPropPanel;
	protected InspectorItem mHolder;
	protected InspectorComponent mParent = null;
	protected boolean mWasChanged = false;
	private boolean mReferenced;
	private boolean mVisible = true;
	protected InspectionInterface mCurObject;
	protected boolean mFixedReference = false;
	protected boolean mExcludeFromFileIO = false;

	protected abstract void postInit();
	protected abstract Component getComponent();

	protected static String floatToString(float val) {
		return Float.toString(val);
	}

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

	public void setExcludeFromFileIO(boolean exclude) {
		mExcludeFromFileIO = exclude;
	}

	public boolean isExcludeFromFileIO() {
		return mExcludeFromFileIO;
	}

	public void setPreferredOutputType(Class<?> type) {

	}

	public void loadFromStream(String value, BufferedReader reader) throws IOException {

	}

	protected void setParent(InspectorComponent parent) {
		mParent = parent;
	}

	public String getName() {
		return mName;
	}

	protected boolean isSaving() {
		return mPropPanel.mSaving;
	}

	protected void refreshInValue() {

	}

	protected boolean isComponentsVisible() {
		return true;
	}

	protected void updateGUI() {

	}

	protected String getFileOutputString() {
		return null;
	}

	protected void refreshOutValue() {

	}

	public void setValueReference(Object reference) {

	}

	public void setFixedReference(Object reference) {
		mFixedReference = true;
		setValueReference(reference);
		refreshInValue();
	}

	public Object getValueReference() {
		return null;
	}

	protected void update(InspectionInterface object,boolean forceUpdate) {
		mCurObject = object;
		if(isVisible()) {
			if(mWasChanged) {
				refreshOutValue();
				if(!mReferenced)
					object.setProperty(mName,this);
			}else{
				if(!hasFocus() || forceUpdate || true) {
					if(!mReferenced) {
						object.readProperty(mName,this);
					}
					if(isComponentsVisible()) {
						refreshInValue();
						updateGUI();
					}
				}
			}
		}
		mWasChanged = false;
	}

	protected String getStringOutput(InspectionInterface object) {
		if(mExcludeFromFileIO)
			return null;
		if(mReferenced) {
			if(!mFixedReference) {
				Object ref = object.getReferencedProperty(mName,this);
				if(ref==null)
					return null;
				setValueReference(ref);
			}
		}else{
			object.readProperty(mName,this);
		}
		refreshInValue();
		String result = getFileOutputString();
		if(result==null)
			return null;
		else
			return mName+"="+result;
	}

	public boolean isReferenced() {
		return mReferenced;
	}

	public void setEnabled(boolean enabled) {
		getComponent().setEnabled(enabled);
	}

	public boolean isVisible() {
		return mVisible;
	}

	public void setVisible(boolean visible) {
		if(mVisible==visible)
			return;
		mVisible = visible;
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

	@Override
	public float getFloat() {
		throw new RuntimeException(notSup("Float"));
	}

	@Override
	public void setFloat(float value) {
		throw new RuntimeException(notSup("Float"));
	}

	@Override
	public double getDouble() {
		throw new RuntimeException(notSup("Double"));
	}

	@Override
	public int getInt() {
		throw new RuntimeException(notSup("Integer"));
	}

	@Override
	public void setInt(int value) {
		throw new RuntimeException(notSup("Integer"));
	}

	@Override
	public void setDouble(double value) {
		throw new RuntimeException(notSup("Double"));
	}

	public String getString() {
		throw new RuntimeException(notSup("String"));
	}

	public void setString(String value) {
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
		if(mFixedReference)
			return;
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

	public void setLinkable() {
		throw new RuntimeException("No linking supported by "+getClass().getName());
	}

	public boolean is(String propertyName) {
		return propertyName==mName;
	}

	public boolean isEq(String propertyName) {
		return propertyName.equals(mName);
	}

	protected void handleShortCut(int code) {

	}

	public InspectorComponent addShortCut(boolean ctrlDown,int keyCode,int shortCutCode) {
		mPropPanel.addShortCut(ctrlDown,keyCode,this,shortCutCode);
		return this;
	}

	public InspectorComponent addShortCut(boolean ctrlDown,int keyCode) {
		return addShortCut(ctrlDown,keyCode,0);
	}

	public InspectorComponent addShortCut(int keyCode,int shortCutCode) {
		return addShortCut(false,keyCode,shortCutCode);
	}

	public InspectorComponent addShortCut(int keyCode) {
		return addShortCut(keyCode,0);
	}

	@Override
	public InspectorComponent clone() {
		try {
			return this.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected void set(InspectorComponent template) {

	}

	public InspectorComponent cloneAndInit(InspectorPanel targetPanel) {
		InspectorComponent result = clone();
		result.init(targetPanel,mName,isReferenced());
		result.set(this);
		return result;
	}

}
