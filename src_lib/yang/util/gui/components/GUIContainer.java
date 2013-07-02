package yang.util.gui.components;

import yang.events.eventtypes.YangPointerEvent;
import yang.util.NonConcurrentList;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUIPointerEvent;

public class GUIContainer extends GUIInteractiveRectComponent {

	protected NonConcurrentList<GUIComponent> mAllComponents;
	protected NonConcurrentList<GUIInteractiveComponent> mInteractiveComponents;
	
	public GUIContainer() {
		super();
		mWidth = 1024;
		mHeight = 1024;
		mInteractiveComponents = new NonConcurrentList<GUIInteractiveComponent>();
		mAllComponents = new NonConcurrentList<GUIComponent>();
	}
	
	public GUIComponent rawPointerEvent(GUIPointerEvent pointerEvent) {

			float x = pointerEvent.mX;
			float y = pointerEvent.mY;
//				ListIterator<InteractiveGUIComponent> iter = mInteractiveComponents.listIterator(mInteractiveComponents.size()-1);
//				InteractiveGUIComponent component;
//				while((component=iter.previous())!=null) {
			for(GUIInteractiveComponent component:mInteractiveComponents) {
				if(component.mVisible && component.mEnabled && component.inArea(x, y)) {
					int poolPos = BasicGUI.mComponentPoolPos++;
					if(BasicGUI.mComponentPoolPos>=BasicGUI.mGUIEventPool.length) {
						BasicGUI.mComponentPoolPos = 0;
						poolPos = 0;
					}
					GUIPointerEvent guiEvent = BasicGUI.mGUIEventPool[poolPos];
					guiEvent.createFromPointerEvent(pointerEvent, component);
					GUIComponent result;
					if(mGUI.mPressedComponent==null || pointerEvent.mAction!=YangPointerEvent.ACTION_POINTERDRAG)
						result = component.rawPointerEvent(guiEvent);
					else
						result = null;
					
					if(pointerEvent.mAction==YangPointerEvent.ACTION_POINTERUP) {
						if(mGUI.mPressedComponent==component)
							component.guiClick(guiEvent);
					}
					
					if(pointerEvent.mAction==YangPointerEvent.ACTION_POINTERDOWN) {
						if(component.isPressable()) {
							mGUI.setPressedComponent(component);
							return component;
						}
					}
					
					return result;
				}
			}
			if(mGUI.mPressedComponent==null)
				return super.rawPointerEvent(pointerEvent);
			else
				return null;
	}
	
	@Override
	public void refreshProjections(float offsetX,float offsetY) {
		super.refreshProjections(offsetX,offsetY);
		for(GUIComponent component:mAllComponents) {
			component.refreshProjections(mPosX+offsetX, mPosY+offsetY);
		}
		
	}
	
	@Override
	public void draw(int layer) {
		super.draw(layer);
		for(GUIComponent component:mAllComponents) {
			if(component.mVisible)
				component.draw(layer);
		}
	}
	
	public <ComponentType extends GUIComponent> ComponentType addComponent(ComponentType component) {
		if(component instanceof GUIInteractiveComponent) {
			GUIInteractiveComponent interComponent = (GUIInteractiveComponent)component;
			mInteractiveComponents.add(interComponent);
			if(mActionListener!=null)
				interComponent.setActionListener(mActionListener);
			if(mPointerListener!=null)
				interComponent.setPointerListener(mPointerListener);
		}
		mAllComponents.add(component);
		component.init(mGUI);
		return component;
	}
	
	public <ComponentType extends GUIComponent> ComponentType addComponent(Class<ComponentType> componentClass) {
		 
		try {
			ComponentType instance = componentClass.newInstance();
			return addComponent(instance);
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
