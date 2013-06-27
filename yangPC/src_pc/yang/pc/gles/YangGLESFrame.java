package yang.pc.gles;

import javax.swing.JFrame;

import yang.events.EventQueueHolder;
import yang.graphics.YangSurface;
import yang.model.App;
import yang.model.DebugYang;
import yang.pc.PCEventHandler;
import yang.pc.PCFrame;

public class YangGLESFrame extends PCFrame{

	private static final long serialVersionUID = 1L;
	
	public PCGL2ES2Graphics mGraphics;
	public EventQueueHolder mEventListener;
	public PCEventHandler mPCEventHandler;

	public YangGLESFrame(String title) {
		super();
		setTitle(title);
		mEventListener = null;
	}
	
	public YangGLESFrame() {
		this("Yang GL2ES2");
	}
	
	public YangGLESFrame init(int width,int height,boolean autoBuild,boolean frameDecorator) {
		if(DebugYang.FORCE_FULLSCREEN) {
			width = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
			height = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
			frameDecorator = false;
		}
		setUndecorated(!frameDecorator);
		FullGraphicsInitializer initializer = new FullGraphicsInitializer();
		initializer.init(width, height);
		mGraphics = initializer.mTranslator;
		
		if(autoBuild) {
			this.add(mGraphics.getPanel());
			this.pack();
		}
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		App.gfxLoader = mGraphics.mGFXLoader;
		return this;
	}
	
	public YangGLESFrame initFullScreen(boolean autoBuild) {
		return init(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,java.awt.Toolkit.getDefaultToolkit().getScreenSize().height,autoBuild,false);
	}

	public YangGLESFrame init(int width,int height) {
		return init(width,height,true,true);
	}
	
	public void run() {
		present();
		mGraphics.run();
	}
	
	public void setSurface(YangSurface surface) {
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
	protected void close() {
		super.close();
		mGraphics.stop();
	}
	
}
