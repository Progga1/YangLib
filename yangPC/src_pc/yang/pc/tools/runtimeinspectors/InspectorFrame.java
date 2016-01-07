package yang.pc.tools.runtimeinspectors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.pc.tools.runtimeinspectors.interfaces.InspectorFrameListener;
import yang.util.YangList;

public class InspectorFrame implements ActionListener {

	//Properties
	public float mUpdateMinTime = 0;
	private boolean mAllowNoneSelection = true;

	//State
	private boolean mRefreshingLayout = false;
	private double mUpdateTimer = 0;

	//Objects
	protected InspectorManager mManager;
	private InspectorPanel mActiveInspector = null;
	private YangList<InspectedObject> mInspectedObjects;
	private JFrame mFrame;
	private JPanel mMainPanel;
	private JPanel mTopPanel;
	private JComboBox<String> mObjectSelection;
	private BorderLayout mLayout;
	private HashMap<Class<?>,InspectorPanel> mDefaultInspectors = new HashMap<Class<?>,InspectorPanel>(32);
	private InspectorFrameListener mListener = null;

	private class InspectedObject {

		String mNameOrAlias;
		String mOrigName;
		InspectionInterface mObject;
		InspectorPanel mInspector;

		@Override
		public String toString() {
			return mNameOrAlias +" ("+mOrigName+") - "+mObject;
		}

		public void saveToFile(InspectedObject inspObj,String filename) throws IOException {
			mInspector.saveToFile(inspObj.mObject,filename);
		}

		public void loadFromFile(InspectedObject inspObj,String filename) throws IOException {
			mInspector.loadFromFile(inspObj.mObject,filename);
		}

	}

	protected InspectorFrame(InspectorManager manager) {
		mLayout = new BorderLayout();
		mMainPanel = new JPanel();
		mMainPanel.setLayout(mLayout);
		mMainPanel.setPreferredSize(InspectorGUIDefinitions.INITIAL_DIMENSION);
		mInspectedObjects = new YangList<InspectedObject>();
		mManager = manager;
		mObjectSelection = new JComboBox<String>();
		mTopPanel = new JPanel();
		mTopPanel.add(mObjectSelection);
		mTopPanel.setLayout(new BoxLayout(mTopPanel,BoxLayout.PAGE_AXIS));
		mMainPanel.add(mTopPanel,BorderLayout.NORTH);
		mObjectSelection.addActionListener(this);
	}

	public void setFramed() {
		mFrame = new JFrame();
		mFrame.add(mMainPanel);
		mFrame.pack();
		mFrame.setTitle("Inspector");
	}

	public void setFrameTitle(String title) {
		mFrame.setTitle(title);
	}

	public String getTitle() {
		return mFrame.getTitle();
	}

	public void setSize(int width,int height) {
		if(mFrame!=null)
			mFrame.setSize(width,height);
	}

	public void registerDefaultInspector(Class<?> objectType,InspectorPanel inspector) {
		mDefaultInspectors.put(objectType,inspector);
	}

	public boolean nameExists(String name) {
		for(InspectedObject elem:mInspectedObjects) {
			if(name.equals(elem.mNameOrAlias))
				return true;
		}
		return false;
	}

	private void refreshName(InspectedObject elem) {
		String name = elem.mOrigName;
		int i=0;
		elem.mNameOrAlias = null;
		while(nameExists(name)) {
			i++;
			name = elem.mOrigName+i;
		}
		elem.mNameOrAlias = name;
	}

	public void setAllowNoneSelection(boolean allowNone) {
		mAllowNoneSelection = allowNone;
		refreshLayout();
	}

	public void addObjectToInspect(InspectionInterface object,InspectorPanel inspector) {
		InspectedObject elem = new InspectedObject();
		elem.mObject = object;
		elem.mInspector = inspector;

		elem.mOrigName = object.getName();
		refreshName(elem);
		mInspectedObjects.add(elem);

		refreshLayout();
	}

	public InspectorPanel findInspector(Class<?> objType) {
		InspectorPanel inspector = mDefaultInspectors.get(objType);
		while(inspector==null && objType.getSuperclass()!=null) {
			objType = objType.getSuperclass();
			inspector = mDefaultInspectors.get(objType);
		}
		return inspector;
	}

	public InspectorPanel findInspector(Object object) {
		return findInspector(object.getClass());
	}

	public void addObjectToInspect(InspectionInterface object) {
		InspectorPanel inspector = findInspector(object.getClass());
		if(inspector==null)
			throw new RuntimeException("No default inspector for type: "+object.getClass());
		addObjectToInspect(object,inspector);
	}

	public boolean isVisible() {
		return mFrame==null || mFrame.isVisible();
	}

	public void update(float deltaTime) {
		if((mUpdateTimer-=deltaTime)<=0) {
			mUpdateTimer = mUpdateMinTime;
		}else
			return;
		if(!isVisible())
			return;
		if(mInspectedObjects.size()==0)
			return;
		if(mRefreshingLayout)
			return;

		boolean refreshAll = false;
		for(InspectedObject elem:mInspectedObjects) {
			if(elem.mOrigName!=elem.mObject.getName()) {
				System.out.println("NAME CHANGED "+elem.mOrigName);
				refreshName(elem);
				refreshAll = true;
			}
		}
		if(refreshAll) {
			refreshLayout();
		}
		int selId = mObjectSelection.getSelectedIndex()-getSelOffset();
		boolean objSel = selId>-1;
		InspectedObject oi = objSel?mInspectedObjects.get(selId):null;
		InspectorPanel insp = objSel?oi.mInspector:null;
		boolean changedObject = insp!=mActiveInspector;
		if(changedObject) {
			if(mActiveInspector!=null)
				mMainPanel.remove(mActiveInspector.getComponent());
			if(insp!=null)
				mMainPanel.add(insp.getComponent(),BorderLayout.CENTER);
			mActiveInspector = insp;
		}

		if(mActiveInspector!=null)
			mActiveInspector.setValuesByObject(oi.mObject);

		if(changedObject) {
			mFrame.setVisible(true);
			if(mListener!=null)
				mListener.onSelectObject(selId,this);
		}
		mFrame.repaint();
	}

	public void update() {
		update(900000);
	}

	public void refreshLayout() {
		if(mRefreshingLayout)
			return;
		mRefreshingLayout = true;
		mObjectSelection.removeAllItems();
		if(mAllowNoneSelection)
			mObjectSelection.addItem("<none>");
		for(InspectedObject object:mInspectedObjects) {
			mObjectSelection.addItem(object.mNameOrAlias);
		}
		mRefreshingLayout = false;
	}

	public void setListener(InspectorFrameListener listener) {
		mListener = listener;
	}

	public InspectorPanel createInspector() {
		return new InspectorPanel(this);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		update();
	}

	public void notifyValueUserInput() {
		mUpdateTimer = -1;
	}

	public void saveObjectToFile(Object object,String filename) throws IOException {
		for(InspectedObject inspObj:mInspectedObjects) {
			if(inspObj.mObject==object) {
				inspObj.saveToFile(inspObj,filename);
				return;
			}
		}
		throw new RuntimeException("Object not inspected: "+object.toString());
	}

	public void loadObjectFromFile(Object object,String filename) throws IOException {
		for(InspectedObject inspObj:mInspectedObjects) {
			if(inspObj.mObject==object) {
				inspObj.loadFromFile(inspObj,filename);
				return;
			}
		}
		throw new RuntimeException("Object not inspected: "+object.toString());
	}

	public void handleShortCut(boolean ctrlDown,int keyCode) {
		if(mActiveInspector!=null)
			mActiveInspector.handleShortCut(ctrlDown,keyCode);
	}

	public int getObjectId(InspectionInterface object) {
		int i = 0;
		for(InspectedObject obj:mInspectedObjects) {
			if(obj.mObject==object) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private int getSelOffset() {
		return mAllowNoneSelection?1:0;
	}

	public void setSelected(int id) {
		mObjectSelection.setSelectedIndex(id+getSelOffset());
	}

	public void setSelected(InspectionInterface object) {
		int id = getObjectId(object);
		if(id>=0)
			setSelected(id);
	}

	public int getSelectionId() {
		return mObjectSelection.getSelectedIndex()-getSelOffset();
	}

	public InspectionInterface getObject(int id) {
		return mInspectedObjects.get(id).mObject;
	}

	public void show() {
		mFrame.setVisible(true);
	}

	public void hide() {
		mFrame.setVisible(false);
	}

	public void clear() {
		mInspectedObjects.clear();
		refreshLayout();
	}

	public void createInspectorsByTemplate(InspectorFrame templateFrame) {
		for(Entry<Class<?>,InspectorPanel> entry:templateFrame.mDefaultInspectors.entrySet()) {
			registerDefaultInspector(entry.getKey(),entry.getValue());
		}
	}

	@Override
	public String toString() {
		return getTitle();
	}

}
