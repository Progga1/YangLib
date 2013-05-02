package yang.pc.gles;

import javax.swing.JFrame;

import yang.graphics.SurfaceInterface;
import yang.graphics.events.EventQueueHolder;
import yang.model.App;
import yang.pc.PCEventHandler;
import yang.pc.PCFrame;

public class GLESFrame extends PCFrame{

	private static final long serialVersionUID = 1L;
	
	public PCGL2ES2Graphics mGraphics;
	public EventQueueHolder mEventListener;
	public PCEventHandler mPCEventHandler;

	public GLESFrame(String title) {
		super();
		setTitle(title);
		mEventListener = null;
	}
	
	public void init(int width,int height,boolean autoBuild,boolean frameDecorator) {
		setUndecorated(!frameDecorator);
		mGraphics = new PCGL2ES2Graphics(width,height,false);
		
		if(autoBuild) {
			this.add(mGraphics.getPanel());
			this.pack();
		}
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		App.gfxLoader = mGraphics.mGFXLoader;
		App.exit = this;
	}
	
	public void initFullScreen(boolean autoBuild) {
		init(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,java.awt.Toolkit.getDefaultToolkit().getScreenSize().height,autoBuild,false);
	}

	public void init(int width,int height) {
		init(width,height,true,true);
	}
	
	public void run() {
		present();
		mGraphics.run();
	}
	
	public void setSurface(SurfaceInterface surface) {
		mSurface = surface;
		mSurface.setGraphics(mGraphics);
		mGraphics.setSurface(mSurface);
		
		if(surface instanceof EventQueueHolder)
			this.setEventListener((EventQueueHolder)surface);
	}

	public void setEventListener(EventQueueHolder eventListener) {
		if(mEventListener==null) {
			PCEventHandler eventHandler = mGraphics.setMouseEventListener(eventListener);
			addKeyListener(eventHandler);
			mGraphics.getPanel().addKeyListener(eventHandler);
			mEventListener = eventListener;
			mPCEventHandler = eventHandler;
		}
	}
	
	@Override
	public void exit() {
		mGraphics.stop();
		super.exit();
	}
	
}
