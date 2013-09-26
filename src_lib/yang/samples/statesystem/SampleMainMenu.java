package yang.samples.statesystem;

import java.util.HashMap;

import yang.events.Keys;
import yang.events.eventtypes.YangEvent;
import yang.samples.statesystem.states.GUISampleState;
import yang.samples.statesystem.states.IcyTerrainState;
import yang.samples.statesystem.states.MatrixStackSampleState;
import yang.samples.statesystem.states.MemorySampleState;
import yang.samples.statesystem.states.NestedTextureRenderTargetsSampleState;
import yang.samples.statesystem.states.OBJSampleState;
import yang.samples.statesystem.states.ParticleSampleState;
import yang.samples.statesystem.states.PolygonSampleState;
import yang.samples.statesystem.states.Rotations3DSampleState;
import yang.samples.statesystem.states.ShaderPermutationsSampleState;
import yang.samples.statesystem.states.Skeleton3DSampleState;
import yang.samples.statesystem.states.SoundSampleState;
import yang.samples.statesystem.states.StringSampleState;
import yang.samples.statesystem.states.StrokeDrawerSampleState;
import yang.samples.statesystem.states.TailSampleState;
import yang.samples.statesystem.states.TexAtlasSampleState;
import yang.util.NonConcurrentList;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUICoordinatesMode;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.components.defaultbuttons.DefaultRectButton;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleMainMenu extends YangProgramState<YangProgramStateSystem> implements GUIActionListener {

	public static float SCALE = 1.16f;
	
	protected BasicGUI mGUI;
	protected NonConcurrentList<DefaultRectButton> mButtons;
	protected HashMap<GUIComponent,YangProgramState<?>> mProgramStates;
	
	public SampleMainMenu() {
		mButtons = new NonConcurrentList<DefaultRectButton>();
		mProgramStates = new HashMap<GUIComponent,YangProgramState<?>>(16);
	}
	
	@Override
	public void initGraphics() {
		mGUI = new BasicGUI(mGraphics2D,GUICoordinatesMode.SCREEN,true,4);
		//Alternatively: normalized: mGUI = new BasicGUI(mGraphics2D,GUICoordinatesMode.NORMALIZED,true);
		mGUI.setDefaultActionListener(this);
		addMenuItem("Strings", new StringSampleState());
		addMenuItem("GUI", new GUISampleState());
		addMenuItem("Sounds", new SoundSampleState());
		addMenuItem("Alloc - free", new MemorySampleState());
		addMenuItem("Tails", new TailSampleState());
		addMenuItem("Matrix stack", new MatrixStackSampleState());
		addMenuItem("Advanced rotations", new Rotations3DSampleState());
		addMenuItem("Stroke", new StrokeDrawerSampleState());
		addMenuItem("Icy terrain", new IcyTerrainState());
		addMenuItem("Polygon", new PolygonSampleState());
		addMenuItem("Particles", new ParticleSampleState());
		addMenuItem("Texture Atlas", new TexAtlasSampleState());
		addMenuItem("Nested Render targets", new NestedTextureRenderTargetsSampleState());
		addMenuItem("Shader permutations", new ShaderPermutationsSampleState());
		addMenuItem("Obj loader", new OBJSampleState());
		addMenuItem("3D skeleton", new Skeleton3DSampleState());
		refreshLayout();
	}
	
	private void refreshLayout() {
		//mGUI.refreshCoordinateSystem();
		final float topY = mGUI.getGUITop()+0.13f*SCALE;
//		if(mGraphics.getSurfaceHeight()>mGraphics.getSurfaceWidth()) {
//			float y = topY;
//			for(DefaultRectButton button:mButtons) {
//				button.setPosCentered(mGUI.getGUICenterX(), y);
//				y += 0.2f*SCALE;
//			}
//		}else{
		float x;
		if(false && mGraphics.getSurfaceHeight()<mGraphics.getSurfaceWidth())
			x = mGUI.getGUICenterX()-1f;
		else 
			x = mGUI.getGUICenterX()-0.5f;
			float y = topY;
			for(DefaultRectButton button:mButtons) {
				button.setPosCentered(x, y);
				y += 0.2f*SCALE;
				if(y>=mGUI.getGUIBottom()-0.2f) {
					y = topY;
					x += 1;
				}
			}
//		}
	}
	
	public void addMenuItem(String caption, YangProgramState<?> state) {
		DefaultRectButton newButton = new DefaultRectButton();
		newButton.getPass(GUICaptionDrawer.class).createCaption(caption);
		newButton.getPass(GUIRectDrawer.class).setBorderSize(0.01f);
		//In normalized coordinates: newButton.setPosAndDimCentered(0, -1+(0.15f+mButtons.size()*0.24f)*SCALE,1*SCALE, 0.15f*SCALE);
		mGUI.addComponent(newButton);
		newButton.setExtents(SCALE*0.8f, 0.15f*SCALE);
		mButtons.add(newButton);
		mProgramStates.put(newButton, state);
	}
	
	@Override
	public void step(float deltaTime) {
		mGUI.step(deltaTime);
	}

	@Override
	public void draw() {
		mGraphics2D.activate();
		mGraphics.clear(0, 0, 0.1f);
		mGUI.draw();
	}
	
	@Override
	public boolean rawEvent(YangEvent event) {
		if(mGUI!=null)
			return mGUI.handleEvent(event)!=null;
		else
			return false;
	}

	@Override
	public void onGUIAction(GUIComponent sender) {
		YangProgramState<?> state = mProgramStates.get(sender);
		if(state!=null)
			mStateSystem.setState(state);
	}
	
	@Override
	public void keyUp(int code) {
		if(code==Keys.ESC)
			mStateSystem.exit();
	}

	@Override
	public void surfaceSizeChanged(int width,int height) {
		mGUI.refreshCoordinateSystem();
		refreshLayout();
	}
	
}
