package yang.pc.tools.animator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import yang.graphics.skeletons.Skeleton;
import yang.graphics.skeletons.animations.Animation;
import yang.pc.tools.StringListBox;
import yang.util.Util;

public class AnimatorSideBar extends JPanel implements ListSelectionListener {

	private static final long serialVersionUID = 1L;
	public AnimatorFrame mFrame;
	public StringListBox mAnimationListBox;
	public StringListBox mSkeletonListBox;
	protected boolean mRefreshing = false;
	
	public AnimatorSideBar(AnimatorFrame animatorFrame) {
		mFrame = animatorFrame;
	}
	
	public void init(AnimatorFrame parent) {
		this.setPreferredSize(new Dimension(256,0));
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		topPanel.setPreferredSize(new Dimension(0,24));
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(2,1));
		
		mSkeletonListBox = new StringListBox(this);
		mAnimationListBox = new StringListBox(this);
		
		centerPanel.add(mSkeletonListBox.getScrollPane());
		centerPanel.add(mAnimationListBox.getScrollPane());
		this.add(topPanel,BorderLayout.NORTH);
		this.add(centerPanel,BorderLayout.CENTER);
	}
	
	public void refreshAnimationListBox() {
		mRefreshing = true;
		mAnimationListBox.clear();
		for(Animation<?> animation:mFrame.getAnimator().mCurAnimationSystem.mAnimations) {
			mAnimationListBox.addItem(Util.getClassName(animation));
		}
		mAnimationListBox.setSelectedIndex(0);
		mAnimationListBox.repaint();
		mRefreshing = false;
	}
	
	public void refreshSkeletonListBox() {
		mRefreshing = true;
		mSkeletonListBox.clear();
		for(Skeleton skeleton:mFrame.getAnimator().mSkeletons) {
			mSkeletonListBox.addItem(Util.getClassName(skeleton,"Skeleton"));
		}
		mSkeletonListBox.setSelectedIndex(0);
		mSkeletonListBox.repaint();
		refreshAnimationListBox();
		mRefreshing = false;
	}

	@Override
	public void valueChanged(ListSelectionEvent event) {
		if(mRefreshing)
			return;
		if(event.getSource()==mSkeletonListBox) {
			mFrame.getAnimator().selectSkeleton(mSkeletonListBox.getSelectedIndex());
			refreshAnimationListBox();
		}
		if(event.getSource()==mAnimationListBox) {
			mFrame.getAnimator().selectAnimation(mAnimationListBox.getSelectedIndex());
		}
	}

	public void start() {
		refreshSkeletonListBox();
	}
	
}
