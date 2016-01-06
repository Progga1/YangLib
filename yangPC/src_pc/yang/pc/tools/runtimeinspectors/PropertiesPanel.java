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

	public void add(InspectorItem property) {
		mItems.add(property);
		super.add(property);
//		refreshLayout();
	}

	public InspectorItem add(InspectorComponent component) {
		InspectorItem newItem = new InspectorItem(mInspector,component);
		add(newItem);
		return newItem;
	}

	public void setValuesByObject(InspectionInterface object) {
		if(mCurObject==object) {
			for(InspectorItem component:getItems()) {
//				if(!component.getInspectorComponent().isReferenced() || component.getInspectorComponent().mWasChanged)
				component.getInspectorComponent().update(object,false);
			}
		}else{
			for(InspectorItem component:getItems()) {
				InspectorComponent inspComp = component.getInspectorComponent();
				if(inspComp.isReferenced())
					inspComp.updateReference(object);
				component.getInspectorComponent().update(object,true);
			}
		}
		mCurObject = object;
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
			for(InspectorItem item:mItems) {
				InspectorComponent comp = item.mInspectorComponent;
				if(comp.mName.equals(key)) {
					if(object!=null && comp.isReferenced()) {
						Object ref = object.getReferencedProperty(comp.mName,comp);
						if(ref==null)
							continue;
						comp.setValueReference(ref);
					}
					comp.loadFromStream(value,reader);
					comp.refreshOutValue();
					if(object!=null && !comp.isReferenced()) {
						object.setProperty(comp.mName,comp);
					}
					break;
				}
			}
		}
	}

}
