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
	
	private float mTime;
	private float mSpeed; //Units per second
	private float mThresDistSq;
	private float mMixIn; 
	
	
	public PointListIterator() {
		mPointList 		= new YangList<Point2f>();
		cur  			= new Point2f();
		curDir  		= new Vector2f();
		tarInd			= 0;
		dir		 		= new Vector2f();
		
		float f = 20;
		
		mTime 			= 0;
		mSpeed			= 0.5f * f;
		mThresDistSq	= 0.01f * f;
		mMixIn			= 0.1f;
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
		mTime += deltaTime;
		
		// new direction
		dir.set(tar);
		dir.sub(cur);
		dir.setMagnitude(mSpeed*deltaTime);
		
		
		float a = -0.001f;
		float b = 0.001f;
		dir.add(MathFunc.randomF(a, b), MathFunc.randomF(a, b));
		
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
