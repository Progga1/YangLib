package yang.pc.tools.runtimeinspectors;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import yang.pc.tools.runtimeinspectors.interfaces.InspectionInterface;
import yang.util.YangList;

public class PropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected InspectorPanel mInspector;
	protected YangList<InspectorItem> mItems = new YangList<InspectorItem>();
	protected InspectionInterface mCurObject = null;
	protected boolean mFixedObjectReference = false;

	public PropertiesPanel(InspectorPanel inspector) {
		mInspector = inspector;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
	}

//	public void refreshLayout() {
//		this.removeAll();
//		for(InspectorProperty item:mItems) {
//			this.add(item);
//		}
//	}

	public YangList<InspectorItem> getItems() {
		return mItems;
	}

	public void addItem(InspectorItem property) {
		property.mPropertiesPanel = this;
		mItems.add(property);
		super.add(property);
//		refreshLayout();
	}

	public void removeItem(InspectorItem item) {
		mItems.remove(item);
		super.remove(item);
	}

	public void setFixedObject(InspectionInterface object) {
		mFixedObjectReference = true;
	}

	public void moveItem(InspectorItem item,int index) {
		mItems.remove(item);
		mItems.add(index,item);
		refreshLayout();
	}

	protected void refreshLayout() {
		super.removeAll();
		for(InspectorItem item:mItems) {
			super.add(item);
		}
	}

	public InspectorItem add(InspectorComponent component) {
		InspectorItem newItem = new InspectorItem(component);
		addItem(newItem);
		return newItem;
	}

	public void setValuesByObject(InspectionInterface object,boolean forceUpdate) {
		if(mCurObject==object) {
			setValuesByObject(forceUpdate);
		}else{
			if(mCurObject!=null && mFixedObjectReference)
				throw new RuntimeException("Reference is fixed");
			mCurObject = object;
			for(InspectorItem component:getItems()) {
				InspectorComponent inspComp = component.getInspectorComponent();
				if(inspComp.isReferenced())
					inspComp.updateReference(object);
				component.getInspectorComponent().update(object,true);
			}
		}
	}

	public void setValuesByObject(boolean forceUpdate) {
		for(InspectorItem component:getItems()) {
			component.getInspectorComponent().update(mCurObject,forceUpdate);
		}
	}

	public void loadFromStream(InspectionInterface object, BufferedReader reader) throws IOException {
		while(true) {
			String line = reader.readLine();
			if(line==null)
				break;
			line = line.trim();
			if(line.equals("}"))
				break;
			int eqId = line.indexOf("=");
			if(eqId<0)
				continue;
			String key = line.substring(0,eqId);
			String value = line.substring(eqId+1,line.length());
			boolean foundComp = false;
			for(InspectorItem item:mItems) {
				InspectorComponent comp = item.mInspectorComponent;
				if(comp.mName.equals(key)) {
//					if(!comp.mExcludeFromFileIO) {
						if(object!=null && comp.isReferenced() && !comp.mFixedReference) {
							Object ref = object.getReferencedProperty(comp.mName,comp);
							if(ref==null)
								continue;
							comp.setValueReference(ref);
							comp.refreshInValue();
						}
						comp.loadFromStream(value,reader);
						comp.refreshOutValue();
						if(object!=null && !comp.isReferenced()) {
							object.setProperty(comp.mName,comp);
						}
//					}
					foundComp = true;
					break;
				}
			}
			if(!foundComp) {
				if(line.endsWith("{")) {
					int bracketCount = 1;
					do {
						line = reader.readLine();
						if(line==null)
							break;
						if(line.endsWith("{"))
							bracketCount++;
						if(line.endsWith("}"))
							bracketCount--;
					}while(bracketCount>0);
					if(line==null)
						break;
				}
			}
		}
	}

	public PropertiesPanel clone(InspectorPanel inspector) {
		PropertiesPanel newPanel = new PropertiesPanel(inspector);
		for(InspectorItem item:mItems) {
			InspectorComponent newComp = item.mInspectorComponent.cloneAndInit(inspector);
			newPanel.add(newComp);
		}
		return newPanel;
	}

	@Override
	public PropertiesPanel clone() {
		return clone(mInspector);
	}

	public InspectorItem getItemByName(String name) {
		for(InspectorItem item:mItems) {
			if(name.equals(item.mInspectorComponent.getName()))
				return item;
		}
		return null;
	}

	public boolean nameExists(String name) {
		return getItemByName(name)!=null;
	}

	public void setParent(InspectorComponent parent) {
		for(InspectorItem item:mItems) {
			item.mInspectorComponent.setParent(parent);
		}
	}

	public void notifyValueUserInput() {
		mInspector.notifyValueUserInput();
	}

	public boolean isFixedReference() {
		return mFixedObjectReference;
	}

	public InspectorComponent getProperty(String name) {
		for(InspectorItem item:mItems) {
			if(item.mInspectorComponent.mName.equals(name))
				return item.mInspectorComponent;
		}
		return null;
	}

	public void removeProperty(String name) {
		InspectorItem item = getItemByName(name);
		item.setVisible(false);
		mItems.remove(item);
		super.remove(item);
	}

}
