package yang.pc.tools.animator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import yang.graphics.skeletons.animations.Animation;
import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.graphics.skeletons.animations.KeyFrame;
import yang.graphics.tools.animator.Animator;
import yang.pc.tools.RepaintThread;

public class AnimatorTimeBar extends JPanel implements MouseListener,MouseMotionListener {

	private static final long serialVersionUID = 1L;
	public final static Color CL_FILL = new Color(200,200,200);
	public final static Color CL_KEYFRAME = new Color(40,40,40);
	public final static Color CL_MARKER = new Color(140,90,20);

	protected Animator mAnimator;
	
	public AnimatorTimeBar(Animator animator) {
		mAnimator = animator;
		this.setPreferredSize(new Dimension(0, 40));
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	@Override
	public void paint(Graphics gfx) {
		gfx.setColor(CL_FILL);
		gfx.fillRect(0, 0, getWidth(), getHeight());
		
		int frameNr = 0;
		Animation<?> anim = mAnimator.getCurrentAnimation();
		if(anim==null || anim.mKeyFrames==null)
			return;
		AnimationPlayer<?> animPlayer = mAnimator.mCurAnimationPlayer;
		gfx.setColor(CL_KEYFRAME);
		for(KeyFrame frame:anim.mKeyFrames) {
			int x = (int)((float)frameNr/anim.mFrameCount*getWidth());
			gfx.drawLine(x, 0, x, getHeight());
			frameNr += 1/frame.mTimeFactor;
		}
		
		gfx.setColor(CL_MARKER);
		int x = (int)(animPlayer.getNormalizedAnimationTime()*getWidth());
		gfx.drawLine(x, 0, x, getHeight());
		gfx.drawLine(x+1, 0, x+1, getHeight());
		gfx.drawLine(x-1, 0, x-1, getHeight());
	}
	
	public void run() {
		new RepaintThread(this,40).start();
	}

	@Override
	public void mouseClicked(MouseEvent ev) {
		
	}

	@Override
	public void mouseEntered(MouseEvent ev) {
		
	}

	@Override
	public void mouseExited(MouseEvent ev) {
		
	}

	@Override
	public void mousePressed(MouseEvent ev) {
		mAnimator.saveChangedPose();
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		float norm = (float)ev.getX()/getWidth();
		while(norm>1)
			norm-=1;
		while(norm<0)
			norm+=1;
		int frameNr = mAnimator.mCurAnimation.timeToKeyFrameIndex((norm+0.5f/mAnimator.mCurAnimation.mPreviousFrames.length)*mAnimator.mCurAnimation.mTotalDuration);

		mAnimator.selectKeyFrame(frameNr, true);
	}

	@Override
	public void mouseDragged(MouseEvent ev) {
		mAnimator.mCurAnimationPlayer.setNormalizedAnimationTime((float)ev.getX()/(getWidth()));
	}

	@Override
	public void mouseMoved(MouseEvent ev) {
		
	}
	
}
