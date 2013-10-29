package yang.util.gui.components;

import yang.events.eventtypes.SurfacePointerEvent;
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

	@Override
	public GUIComponent rawPointerEvent(GUIPointerEvent pointerEvent) {

			final float x = pointerEvent.mX;
			final float y = pointerEvent.mY;
//				ListIterator<InteractiveGUIComponent> iter = mInteractiveComponents.listIterator(mInteractiveComponents.size()-1);
//				InteractiveGUIComponent component;
//				while((component=iter.previous())!=null) {
			for(final GUIInteractiveComponent component:mInteractiveComponents) {
				if(component.mVisible && component.mEnabled && component.inArea(x, y)) {
					int poolPos = BasicGUI.componentPoolPos++;
					if(BasicGUI.componentPoolPos>=BasicGUI.mGUIEventPool.length) {
						BasicGUI.componentPoolPos = 0;
						poolPos = 0;
					}
					final GUIPointerEvent guiEvent = BasicGUI.mGUIEventPool[poolPos];
					guiEvent.createFromPointerEvent(pointerEvent, component);
					GUIComponent result;
					if(mGUI.mPointerData[pointerEvent.mId].mPressedComponent==null || pointerEvent.mAction!=SurfacePointerEvent.ACTION_POINTERDRAG)
						result = component.rawPointerEvent(guiEvent);
					else
						result = null;

					if(pointerEvent.mAction==SurfacePointerEvent.ACTION_POINTERUP) {
						if(mGUI.mPointerData[pointerEvent.mId].mPressedComponent==result)
							component.guiClick(guiEvent);
					}

					if(pointerEvent.mAction==SurfacePointerEvent.ACTION_POINTERDOWN) {
						if(component.isPressable()) {
							mGUI.setPressedComponent(pointerEvent.mId,component);
							return component;
						}
					}

					return result;
				}
			}
			if(mGUI.mPointerData[pointerEvent.mId].mPressedComponent==null)
				return super.rawPointerEvent(pointerEvent);
			else
				return null;
	}

	@Override
	public void refreshProjections(float offsetX,float offsetY) {
		super.refreshProjections(offsetX,offsetY);
		for(final GUIComponent component:mAllComponents) {
			component.refreshProjections(mPosX+offsetX, mPosY+offsetY);
		}

	}

	@Override
	public void draw(int layer) {
		super.draw(layer);
		for(final GUIComponent component:mAllComponents) {
			if(component.mVisible)
				component.draw(layer);
		}
	}

	public <ComponentType extends GUIComponent> ComponentType addComponent(ComponentType component) {
		if(component instanceof GUIInteractiveComponent) {
			final GUIInteractiveComponent interComponent = (GUIInteractiveComponent)component;
			mInteractiveComponents.add(interComponent);
			if(mActionListener!=null)
				interComponent.setActionListener(mActionListener);
			if(mPointerListeners!=null)
				interComponent.setPointerListener(mPointerListeners);
		}
		mAllComponents.add(component);
		component.init(mGUI);
		return component;
	}

	public <ComponentType extends GUIComponent> ComponentType addComponent(Class<ComponentType> componentClass) {

		try {
			final ComponentType instance = componentClass.newInstance();
			return addComponent(instance);
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}

}
