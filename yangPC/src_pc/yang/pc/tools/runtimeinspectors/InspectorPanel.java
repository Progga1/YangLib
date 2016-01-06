package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;

public class InspectorPanel {

	protected JPanel mTopLevelPanel;
	protected PropertiesPanel mPropertiesPanel;
	protected InspectorFrame mFrame;
	protected InspectorManager mManager;
	protected JScrollPane mScrollPane;
	protected boolean mSaving = false;

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
		InspectorItem rtpHolder = new InspectorItem(this,inspectorComponent);
		mPropertiesPanel.add(rtpHolder);
		return inspectorComponent;
	}

	public InspectorComponent registerProperty(String name,Class<?> type) {
		InspectorComponent comp = mManager.createDefaultComponentInstance(type);
		if(comp==null)
			throw new RuntimeException("No default component for type: "+type.getName());
		return registerProperty(name,comp);
	}

	public void registerPropertyReferenced(String name,InspectorComponent inspectorComponent) {
		inspectorComponent.init(this, name, true);
		InspectorItem rtpHolder = new InspectorItem(this,inspectorComponent);
		mPropertiesPanel.add(rtpHolder);
	}

	public void registerPropertyReferenced(String name,Class<?> type) {
		InspectorComponent comp = mManager.createDefaultComponentInstance(type);
		if(comp==null)
			throw new RuntimeException("No default component for type: "+type.getName());
		registerPropertyReferenced(name,comp);
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
		mPropertiesPanel.setValuesByObject(object);
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

	public void loadFromFile(InspectionInterface object, String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		loadFromStream(object,reader);
		reader.close();
	}

	public boolean isSaving() {
		return mSaving;
	}

	public InspectorPanel createNewInspector() {
		return getManager().createInspector(getFrame());
	}

}
