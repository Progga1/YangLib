package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import yang.events.Keys;
import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterfaceHolder;
import yang.pc.tools.runtimeinspectors.subcomponents.InspectorShortCut;

public class InspectorPanel {

	protected JPanel mTopLevelPanel;
	protected PropertiesPanel mPropertiesPanel;
	protected InspectorFrame mFrame;
	protected InspectorManager mManager;
	protected JScrollPane mScrollPane;
	protected boolean mSaving = false;
	protected HashMap<Integer,InspectorShortCut> mShortCuts = new HashMap<Integer,InspectorShortCut>(32);

	protected InspectorPanel(InspectorFrame frame) {
		mManager = frame.mManager;
		mFrame = frame;
//		mLayout = new FlowLayout(FlowLayout.LEADING,5,5);
		mTopLevelPanel = new JPanel();
		mTopLevelPanel.setLayout(new BorderLayout());
		mPropertiesPanel = new PropertiesPanel(this);
		mTopLevelPanel.add(mPropertiesPanel,BorderLayout.NORTH);
		mTopLevelPanel.setBackground(InspectorGUIDefinitions.CL_UNUSED_SPACE);
		mScrollPane = new JScrollPane(mTopLevelPanel);
		mScrollPane.getVerticalScrollBar().setUnitIncrement(20);
	}

	public void setVisible(boolean visible) {
		mTopLevelPanel.setVisible(visible);
	}

	public InspectorManager getManager() {
		return mManager;
	}

	public InspectorFrame getFrame() {
		return mFrame;
	}

	public PropertiesPanel getPropertiesPanel() {
		return mPropertiesPanel;
	}

	public <ComponentType extends InspectorComponent> ComponentType registerProperty(String name,ComponentType inspectorComponent) {
		inspectorComponent.init(this, name, false);
		InspectorItem rtpHolder = new InspectorItem(inspectorComponent);
		mPropertiesPanel.addItem(rtpHolder);
		return inspectorComponent;
	}

	public InspectorComponent registerProperty(String name,Class<?> type) {
		InspectorComponent comp = mManager.createDefaultComponentInstance(type);
		if(comp==null)
			throw new RuntimeException("No default component for type: "+type.getName());
		return registerProperty(name,comp);
	}

	public void moveComponent(InspectorComponent component,int index) {
		mPropertiesPanel.moveItem(component.mHolder,index);
	}

	public <ComponentType extends InspectorComponent> ComponentType registerPropertyReferenced(String name,ComponentType inspectorComponent) {
		inspectorComponent.init(this, name, true);
		InspectorItem rtpHolder = new InspectorItem(inspectorComponent);
		mPropertiesPanel.addItem(rtpHolder);
		return inspectorComponent;
	}

	public InspectorComponent registerPropertyReferenced(String name,Class<?> type) {
		InspectorComponent comp = mManager.createDefaultComponentInstance(type);
		if(comp==null)
			throw new RuntimeException("No default component for type: "+type.getName());
		return registerPropertyReferenced(name,comp);
	}

	public int getCaptionWidth() {
		return InspectorGUIDefinitions.DEFAULT_CAPTION_WIDTH;
	}

	public int getDefaultComponentHeight() {
		return InspectorGUIDefinitions.DEFAULT_COMPONENT_HEIGHT;
	}

	public Component getComponent() {
//		return mTopLevelPanel;
		return mScrollPane;
	}

	public void setValuesByObject(InspectionInterface object) {
		mPropertiesPanel.setValuesByObject(object,isSaving());
	}

	public void notifyValueUserInput() {
		mFrame.notifyValueUserInput();
	}

	public void saveToStream(InspectionInterface object,BufferedWriter writer) throws IOException {
		mSaving = true;
		for(InspectorItem item:mPropertiesPanel.mItems) {
			InspectorComponent comp = item.mInspectorComponent;
			String outString = comp.getStringOutput(object);
			if(outString!=null)
				writer.write(outString+"\r\n");
		}
		mSaving = false;
	}

	public void loadFromStream(InspectionInterface object,BufferedReader reader) throws IOException {
		mPropertiesPanel.loadFromStream(object,reader);
	}

	public void saveToFile(InspectionInterface object, String filename) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		saveToStream(object,writer);
		writer.close();
	}

	public boolean loadFromFile(InspectionInterface object, String filename) throws IOException {
		File file = new File(filename);
		if(!file.exists())
			return false;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		loadFromStream(object,reader);
		reader.close();
		return true;
	}

	public boolean isSaving() {
		return mSaving;
	}

	public InspectorPanel createNewInspector() {
		return getManager().createInspector(getFrame());
	}

	protected int toKeyCode(boolean ctrlDown,int keyCode) {
		return (ctrlDown?1024:0)+keyCode;
	}

	public InspectorComponent handleShortCut(boolean ctrlDown,int keyCode) {
		InspectorShortCut shortCut = mShortCuts.get(toKeyCode(ctrlDown,keyCode));
		if(shortCut!=null) {
			InspectorComponent comp = shortCut.mComponent;
			if(comp.isVisible() && comp.handleShortCut(shortCut.mCode)) {
				comp.mWasChanged = true;
				comp.update(comp.mCurObject,true);
				return comp;
			}
		}
		return null;
	}

	protected void addShortCut(boolean ctrlDown,int keyCode, InspectorComponent component, int shortCutCode) {
		mShortCuts.put(toKeyCode(ctrlDown,keyCode),new InspectorShortCut(keyCode,component,shortCutCode));
		String toolTip = component.mHolder.getToolTipText()==null?"Shortcut: ":component.mHolder.getToolTipText()+", ";
		if(ctrlDown)
			toolTip += "Ctrl+";
		toolTip += Keys.toString(keyCode);
		component.mHolder.setToolTipText(toolTip);
	}

	public void registerObjectSubInspectors(List<?> objects) {
		for(Object object:objects) {
			InspectionInterface inspObj = null;

			if(object instanceof InspectionInterface)
				inspObj = (InspectionInterface)object;
			else if(object instanceof InspectionInterfaceHolder) {
				inspObj = ((InspectionInterfaceHolder)object).getInspectionInterface();
			}
			if(inspObj!=null) {
				String name = inspObj.getName();

				if(!mPropertiesPanel.nameExists(name)) {
					InspectorPanel inspector = mFrame.findInspector(inspObj.getClass());
					if(inspector!=null) {
						PropertiesPanel props = inspector.mPropertiesPanel.clone(this);
						PropertyInspectedObject inspProp = new PropertyInspectedObject(props);
						registerPropertyReferenced(name,inspProp);
						inspProp.setFixedReference(inspObj);
					}
				}
			}
		}
	}

	public InspectorComponent getProperty(String name) {
		return mPropertiesPanel.getProperty(name);
	}

	public void removeProperty(String name) {
		mPropertiesPanel.removeProperty(name);
	}

}
