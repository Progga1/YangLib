package yang.pc.tools.animator;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JPanel;

import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.programs.BasicProgram;
import yang.graphics.skeletons.Skeleton;
import yang.graphics.skeletons.animations.AnimationSystem;
import yang.graphics.skeletons.elements.Joint;
import yang.graphics.tools.animator.Animator;
import yang.graphics.tools.animator.DefaultAnimatorSurface;
import yang.model.App;
import yang.model.ExitCallback;
import yang.pc.PCGraphics;
import yang.pc.fileio.PCDataStorage;
import yang.pc.fileio.PCResourceManager;
import yang.pc.fileio.PCSoundLoader;
import yang.pc.gles.YangGLESFrame;
import yang.pc.tools.keymainmenu.KeyItem;
import yang.pc.tools.keymainmenu.KeyMainMenu;
import yang.pc.tools.keymainmenu.KeyMenuListener;
import yang.sound.SoundManager;

public class AnimatorFrame implements InitializationCallback, KeyMenuListener, ExitCallback {

	private DefaultAnimatorSurface mSurface;
	private YangGLESFrame mFrame;
	private String mTitle;
	private BasicProgram[] mUsedShaders;
	public PCGraphics mGraphics;
	private KeyMainMenu mKeyMainMenu;
	private AnimatorSideBar mSideBar;
	private AnimatorTimeBar mTimeBar;
	private JPanel mCenterPanel = new JPanel();
	
	private class InitCallback implements InitializationCallback {
		
		@Override
		public void initializationFinished() {
			if(mUsedShaders!=null)
				for(BasicProgram program:mUsedShaders)
					mSurface.mGraphics.addProgram(program);
			
			mSurface.mAnimator.centerCamera();
		}
	}
	
	public AnimatorFrame(String title) {
		mTitle = title;
	}
	
	public void init(int areaWidthAndHeight,BasicProgram[] usedShaders) {
		mUsedShaders = usedShaders;
		mFrame = new YangGLESFrame(mTitle);
		SoundManager sound = new SoundManager();
		mSurface = new DefaultAnimatorSurface(sound,this);
		mSurface.setInitializationCallback(new InitCallback());
		
		mKeyMainMenu = new KeyMainMenu(mFrame,this);
		mKeyMainMenu.nextSubMenu("Edit");
		mKeyMainMenu.addItem("PREVIOUSFRAME", "Next Frame").setShortCut('1');
		mKeyMainMenu.addItem("NEXTFRAME", "Previous Frame").setShortCut('2');
		mKeyMainMenu.addItem("PREVIOUSFRAMENOTLOAD", "Next Frame unload").setShortCut('3');
		mKeyMainMenu.addItem("NEXTFRAMENOTLOAD", "Previous Frame unload").setShortCut('4');
		mKeyMainMenu.addSeparator();
		mKeyMainMenu.addItem("UNFIX", "Unfix all joints").setShortCut('F');
		mKeyMainMenu.nextSubMenu("Animation");
		mKeyMainMenu.addItem("PLAY", "Play/Pause").setShortCut('W');
		mKeyMainMenu.addItem("STOP", "Stop").setShortCut('Q');
		mKeyMainMenu.addItem("PRINT", "Generate Source").setShortCut('S');
		mKeyMainMenu.addItem("PHYSICS", "Physics").setShortCut('P');
		mKeyMainMenu.nextSubMenu("View");
		mKeyMainMenu.addItem("DRAWSKEL", "Draw skeleton").setShortCut('B');
		
		mFrame.init(areaWidthAndHeight,areaWidthAndHeight);
		mGraphics = mFrame.mGraphics;
		mSideBar = new AnimatorSideBar(this);
		mSideBar.init(this);
		mFrame.getContentPane().setSize(areaWidthAndHeight+mSideBar.getWidth(),areaWidthAndHeight);
		mFrame.setLayout(new BorderLayout());
		mFrame.add(mSideBar,BorderLayout.WEST);
		mCenterPanel.setLayout(new BorderLayout());
		mCenterPanel.add(mGraphics.getPanel(),BorderLayout.CENTER);
		mFrame.add(mCenterPanel,BorderLayout.CENTER);
		mFrame.setSurface(mSurface);
		mSideBar.mSkeletonListBox.addKeyListener(mFrame.mPCEventHandler);
		mSideBar.mAnimationListBox.addKeyListener(mFrame.mPCEventHandler);
		
		App.soundManager = new SoundManager();
		App.storage = new PCDataStorage();
		App.soundLoader = new PCSoundLoader();
		App.gfxLoader = mGraphics.mGFXLoader;
		App.resourceManager = new PCResourceManager();
		
		mFrame.run();
	}
	
	public Animator getAnimator() {
		return mSurface.mAnimator;
	}
	
	public DefaultAnimatorSurface getAnimatorSurface() {
		return mSurface;
	}

	public void waitUntilInitialized() {
		mSurface.waitUntilInitialized();
	}

	public void addSkeleton(Class<? extends Skeleton> skeletonClass,AnimationSystem<?,?> animationSystem) {
		mSurface.mAnimator.addSkeleton(skeletonClass,animationSystem);
	}
	
	public void addSkeleton(Skeleton skeleton,AnimationSystem<?,?> animationSystem) {
		mSurface.mAnimator.addSkeleton(skeleton,animationSystem);
	}

	@Override
	public void initializationFinished() {
		refreshGUI();
		mTimeBar = new AnimatorTimeBar(mSurface.mAnimator);
		mCenterPanel.add(mTimeBar,BorderLayout.SOUTH);
		mFrame.pack();
		mTimeBar.run();
	}
	
	public void setChecked(String key,boolean checked) {
		KeyItem item = mKeyMainMenu.getItem(key);
		if(item==null)
			return;
		item.setText("("+(checked?"x":" ")+") "+item.getCaption());
	}
	
	public void refreshGUI() {
		setChecked("DRAWSKEL",getAnimator().isSkeletonDrawn());
	}

	@Override
	public void itemSelected(String key) {
		Animator animator = getAnimator();
		if(!animator.isPlaying()) {
			if(key=="PREVIOUSFRAME") {
				animator.previousFrame(true);
			}
			if(key=="NEXTFRAME") {
				animator.nextFrame(true);
			}
		}
		if(!animator.isPlaying()) {
			if(key=="PREVIOUSFRAMENOTLOAD") {
				animator.previousFrame(false);
			}
			if(key=="NEXTFRAMENOTLOAD") {
				animator.nextFrame(false);
			}
		}
		if(key=="UNFIX") {
			for(Joint joint:animator.getCurrentSkeleton().mJoints) {
				joint.mFixed = false;
			}
		}
		if(key=="PLAY") {
			if(animator.isPlaying())
				animator.setPaused(!animator.isPaused());
			else
				animator.play();
		}
		if(key=="STOP")
			animator.stop();
		if(key=="PRINT") {
			animator.savePose();
			String sourceCode = animator.mCurAnimation.toSourceCode();
			System.out.println(sourceCode);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sourceCode), null);
		}
		if(key=="PHYSICS") {
			if(animator.isPhysicsActive()) {
				animator.setPhysicsMode(false);
				animator.stop();
			}else
				animator.setPhysicsMode(true);
		}
		if(key=="DRAWSKEL") {
			animator.setDrawSkeleton(!animator.isSkeletonDrawn());
		}
		refreshGUI();
	}
	
	public void start() {
		mSideBar.start();
	}

	@Override
	public void exit() {
		System.exit(0);
	}
}
