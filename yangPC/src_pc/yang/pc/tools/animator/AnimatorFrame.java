package yang.pc.tools.animator;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JPanel;

import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.programs.BasicProgram;
import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.graphics.skeletons.animations.Animation;
import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.graphics.skeletons.animations.AnimationSystem;
import yang.graphics.tools.animator.Animator;
import yang.graphics.tools.animator.DefaultAnimatorSurface;
import yang.model.App;
import yang.model.callback.ExitCallback;
import yang.pc.fileio.PCDataStorage;
import yang.pc.fileio.PCResourceManager;
import yang.pc.fileio.PCSoundManager;
import yang.pc.gles.PCGL2ES2Graphics;
import yang.pc.gles.YangGLESFrame;
import yang.pc.tools.keymainmenu.KeyItem;
import yang.pc.tools.keymainmenu.KeyMainMenu;
import yang.pc.tools.keymainmenu.KeyMenuListener;
import yang.physics.massaggregation.elements.Joint;
import yang.sound.AbstractSoundManager;

public class AnimatorFrame implements InitializationCallback, KeyMenuListener, ExitCallback {

	private DefaultAnimatorSurface mSurface;
	private YangGLESFrame mFrame;
	private final String mTitle;
	private BasicProgram[] mUsedShaders;
	public PCGL2ES2Graphics mGraphics;
	private KeyMainMenu mKeyMainMenu;
	private AnimatorSideBar mSideBar;
	private AnimatorTimeBar mTimeBar;
	private final JPanel mCenterPanel = new JPanel();

	public float[] mCopyData = new float[128];

	private class InitCallback implements InitializationCallback {

		@Override
		public void initializationFinished() {
			if(mUsedShaders!=null)
				for(final BasicProgram program:mUsedShaders)
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

		final AbstractSoundManager sound = new PCSoundManager();
		mSurface = new DefaultAnimatorSurface(sound,this);
		mSurface.setInitializationCallback(new InitCallback());

		mKeyMainMenu = new KeyMainMenu(mFrame,this);
		mKeyMainMenu.nextSubMenu("Edit");
		mKeyMainMenu.addItem("PREVIOUSFRAME", "Previous frame").setShortCut('1');
		mKeyMainMenu.addItem("NEXTFRAME", "Next frame").setShortCut('2');
		mKeyMainMenu.addItem("PREVIOUSFRAMENOTLOAD", "Previous frame no loading").setShortCut('3');
		mKeyMainMenu.addItem("NEXTFRAMENOTLOAD", "Next frame no loading").setShortCut('4');
		mKeyMainMenu.addSeparator();
		mKeyMainMenu.addItem("UNFIX", "Unfix all joints").setShortCut('F');
		mKeyMainMenu.addItem("UNDO", "Undo").setShortCut('Z');
		mKeyMainMenu.addItem("COPYFIXED", "Copy fixed joints").setShortCut('G');
		mKeyMainMenu.addItem("PASTEFIXED", "Paste fixed joints").setShortCut('H');
		mKeyMainMenu.nextSubMenu("Animation");
		mKeyMainMenu.addItem("PLAY", "Play/Pause").setShortCut('W');
		mKeyMainMenu.addItem("STOP", "Stop").setShortCut('Q');
		mKeyMainMenu.addItem("PRINT", "Generate Source").setShortCut('S');
		mKeyMainMenu.addItem("PRINTACTIVEJOINTS", "Source: active joints").setShortCut('G');
		mKeyMainMenu.addItem("PHYSICS", "Physics").setShortCut('P');
		mKeyMainMenu.nextSubMenu("View");
		mKeyMainMenu.addItem("DRAWSKEL", "Draw skeleton").setShortCut('B');

		mFrame.init(areaWidthAndHeight,areaWidthAndHeight);
		mGraphics = mFrame.mGraphics;
		mGraphics.getMainDisplay().setFramed(mFrame);
		mSideBar = new AnimatorSideBar(this);
		mSideBar.init(this);
		mFrame.getContentPane().setSize(areaWidthAndHeight+mSideBar.getWidth(),areaWidthAndHeight);
		mFrame.setLayout(new BorderLayout());
		mFrame.add(mSideBar,BorderLayout.WEST);
		mCenterPanel.setLayout(new BorderLayout());
		mCenterPanel.add(mGraphics.getMainDisplay().getComponent(),BorderLayout.CENTER);
		mFrame.add(mCenterPanel,BorderLayout.CENTER);
		mFrame.setSurface(mSurface);
		mSideBar.mSkeletonListBox.addKeyListener(mFrame.mPCEventHandler);
		mSideBar.mAnimationListBox.addKeyListener(mFrame.mPCEventHandler);

		App.soundManager = sound;
		App.storage = new PCDataStorage();
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

	public void addSkeleton(Class<? extends CartoonSkeleton2D> skeletonClass,AnimationSystem<?,?> animationSystem) {
		mSurface.mAnimator.addSkeleton(skeletonClass,animationSystem);
	}

	public void addSkeleton(CartoonSkeleton2D skeleton,AnimationSystem<?,?> animationSystem,AnimationPlayer<?> animationPlayer) {
		mSurface.mAnimator.addSkeleton(skeleton,animationSystem,animationPlayer);
	}

	public void addSkeleton(CartoonSkeleton2D skeleton,AnimationSystem<?,?> animationSystem) {
		mSurface.mAnimator.addSkeleton(skeleton,animationSystem,null);
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
		final KeyItem item = mKeyMainMenu.getItem(key);
		if(item==null)
			return;
		item.setText("("+(checked?"x":" ")+") "+item.getCaption());
	}

	public void refreshGUI() {
		setChecked("DRAWSKEL",getAnimator().isSkeletonDrawn());
	}

	@Override
	public void itemSelected(String key) {
		final Animator animator = getAnimator();
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
			for(final Joint joint:animator.getCurrentSkeleton().mJoints) {
				joint.mFixed = false;
			}
		}
		if(key=="UNDO") {
			animator.reselect();
		}
		if(key=="COPYFIXED") {
			int k = 0;
			for(Joint joint:animator.mCurSkeleton.mJoints) {
				if(joint.mFixed) {
					mCopyData[k] = joint.mX;
					mCopyData[k+1] = joint.mY;
					k += 2;
				}
			}
		}
		if(key=="PASTEFIXED") {
			int k = 0;
			for(Joint joint:animator.mCurSkeleton.mJoints) {
				if(joint.mFixed) {
					joint.mX = mCopyData[k];
					joint.mY = mCopyData[k+1];
					k += 2;
				}
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
			final String sourceCode = animator.mCurAnimation.toSourceCode();
			System.out.println(sourceCode);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sourceCode), null);
		}
		if(key=="PRINTACTIVEJOINTS") {
			Animation<?> anim = animator.mCurAnimation;
			anim.setJointsAnimated(animator.mCurSkeleton);
			final StringBuilder sourceCode = new StringBuilder(64);
			sourceCode.append("mActiveJoints = new boolean[]{");
			for(int i=0;i<anim.getJointCount();i++) {
				if(i>0)
					sourceCode.append(',');
				sourceCode.append(anim.isJointAnimated(i));
			}
			sourceCode.append("};");
			System.out.println(sourceCode);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sourceCode.toString()), null);
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
