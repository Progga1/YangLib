package yang.model;

import yang.math.MathFunc;
import yang.math.objects.Point2f;
import yang.math.objects.Vector2f;
import yang.util.YangList;

public class PointListIterator {
	private YangList<Point2f> mPointList;	
	public	Point2f cur;
	
	private int tarInd;
	private Point2f	tar;
	private Vector2f dir;
	private Vector2f curDir;
	
	private float mSpeed; //Units per second
	private float mThresDistSq;
	private float mMixIn;
	private boolean mJittery;
	private float mJitterAmount; 
	
	
	public PointListIterator() {
		mPointList 		= new YangList<Point2f>();
		cur  			= new Point2f();
		curDir  		= new Vector2f();
		tarInd			= 0;
		dir		 		= new Vector2f();
		
		mSpeed			= 0.5f;
		mThresDistSq	= 0.01f;
		mMixIn			= 0.1f;
	}
	
	/**
	 * Set parameters of interpolation
	 * @param speed Set the speed of the interpolation
	 * @param thresholdDistance How close the interpolator should come to a target point, before the next point will be targeted
	 * @param sharpEdgeAmount between 0 and 1, amount of direction that will be added to the current direction
	 */
	public void setParams(float speed, float thresholdDistance, float sharpEdgeAmount) {
		mSpeed 			= speed;
		mThresDistSq 	= thresholdDistance;
		mMixIn 			= sharpEdgeAmount;		
	}
	
	public void enableJitter(float amount) {
		mJittery = true;
		mJitterAmount = amount;
	}
	
	public void addPoints(YangList<Point2f> pointsToAdd) {
		mPointList.addAll(pointsToAdd);
	}
	
	public void addPoint(Point2f point2f) {
		mPointList.add(point2f);
	}
	
	public void reset() {
		tarInd 	= 0;
		tar 	= mPointList.get(tarInd);
		cur.set(tar.mX,tar.mY-0.001f);
	}
	
	public void update(float deltaTime) {
		if(mPointList.size() == 0) return;
		
		// new direction
		dir.set(tar);
		dir.sub(cur);
		dir.setMagnitude(mSpeed*deltaTime);
		
		
		if(mJittery){
			dir.add(MathFunc.randomF(-mJitterAmount, mJitterAmount), MathFunc.randomF(-mJitterAmount, mJitterAmount));
		}
		
		// merge with current direction
		curDir.interpolate(dir, mMixIn);
		
		cur.add(curDir);
		if(cur.getDistance(tar) < mThresDistSq) {
			nextTarget();
		}
	}

	private void nextTarget() {
		if(tarInd >= mPointList.size()-1) {
			tarInd = 0;
			
		} else {
			tarInd++;
		}
		
		tar = mPointList.get(tarInd);				
	}

	
	
	
}
