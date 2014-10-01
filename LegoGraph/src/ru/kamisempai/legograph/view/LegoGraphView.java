package ru.kamisempai.legograph.view;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import ru.kamisempai.legograph.GraphPoint;
import ru.kamisempai.legograph.adapter.GraphAdapter;
import ru.kamisempai.legograph.drawer.DaysXAxisDrawer;
import ru.kamisempai.legograph.drawer.HeaderDrawer;
import ru.kamisempai.legograph.drawer.HeaderDrawerMonths;
import ru.kamisempai.legograph.drawer.YAxisDrawer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.widget.Scroller;


public class LegoGraphView extends View {
	// Metrics
	private DisplayMetrics mDisplayMetrics;
	
	// Colors and paints
	private Paint mBackgrounPaint;
	private Paint mGraphPaint;
	private float mPointRadius;
	
	// Graph parameters
	private float mMinZoomX = 0.1f;
	private float mMaxZoomX = 5;
	private float mZoomX;
	private long mPosition;
	private long mTimeInterval;
	private long mVisibleTimeInterval;
	private long mMaxPosition;
	private long mMinPosition;
	private boolean mMinMaxFixed = false;
	private float mMaxValue;
	private float mMinValue;
	private float mMinValieInterval = 10; 
	private long mValueOverscroll;
	private float mOverscroll;

	private float mGraphAreaHeight;
	private float mGraphAreaWidth;
	private float mGraphTopPading;
	private float mGraphBottomPading;
	
	private DaysXAxisDrawer mXAxisDrawer = new DaysXAxisDrawer();
	private YAxisDrawer mYAxisDrawer = new YAxisDrawer();
	
	private ArrayList<HeaderDrawer> mHeaderDrawers = new ArrayList<HeaderDrawer>();
	
	// Gesture and scroll
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private Scroller mScroller;
	private float mScrollPosition;
	
	private boolean mMeasurementChanged;
	private float mXKoef;
	private float mYKoef;
	
	private GraphAdapter mAdapter;
	private LinkedList<GraphPoint> mPointsPool = new LinkedList<GraphPoint>();
	private LinkedList<GraphPoint> mPointsList = new LinkedList<GraphPoint>(); 
	private Path mGraphPath = new Path();
	
	public LegoGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mHeaderDrawers.add(new HeaderDrawerMonths(context));
        
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        mGestureDetector = new GestureDetector(context, mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mScroller = new Scroller(context);
        
        reset();

        mPointRadius = getDpToPx(2);
        mOverscroll = getDpToPx(40);
        
        // Initialise paints
        mBackgrounPaint = new Paint();
        mBackgrounPaint.setStyle(Paint.Style.STROKE);
        mBackgrounPaint.setStrokeJoin(Paint.Join.ROUND);
        mBackgrounPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgrounPaint.setStrokeWidth(100);
        mBackgrounPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        
        
        mGraphPaint = new Paint();
		mGraphPaint.setStyle(Paint.Style.STROKE);
		mGraphPaint.setAntiAlias(true);
		mGraphPaint.setStrokeJoin(Paint.Join.ROUND);
		mGraphPaint.setStrokeCap(Paint.Cap.ROUND);
		mGraphPaint.setColor(Color.BLUE);
		mGraphPaint.setStrokeWidth(getDpToPx(3));
    }
	
	public void reset() {

		mMinZoomX = 0.1f; // ����������� ���
		mMaxZoomX = 5; // ������������ ���
		
        mTimeInterval = 2592000000l;
        mZoomX = 0.5f;
        mVisibleTimeInterval = (long) (mTimeInterval / mZoomX);
        
        mGraphTopPading = mXAxisDrawer.getTopPadding() + mYAxisDrawer.getTopPadding();
        for(HeaderDrawer header: mHeaderDrawers)
        	mGraphTopPading += header.getHeight();
        mGraphBottomPading = mXAxisDrawer.getBottomPadding() + mYAxisDrawer.getBottomPadding();
	}
	
	private OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
		public boolean onDown(MotionEvent e) {
			return true;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			mScroller.forceFinished(true);
			performScroll(distanceX);
			invalidate();
			return true;
		}
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			mScrollPosition = 0;
	        mScroller.fling(0, 0, (int) (-velocityX / 1.5f), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
	        invalidate();
	        return true;
	    }

		public boolean onSingleTapUp(MotionEvent e) {
			
			return super.onSingleTapUp(e);
		}

	};
	
	private OnScaleGestureListener mScaleGestureListener = new OnScaleGestureListener() {

	    @Override
	    public void onScaleEnd(ScaleGestureDetector detector) {
	    }

	    @Override
	    public boolean onScaleBegin(ScaleGestureDetector detector) {
	        return true;
	    }

	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	    	if(performZoom(detector.getScaleFactor(), detector.getFocusX()))
		        ViewCompat.postInvalidateOnAnimation(LegoGraphView.this);
	        return true;
	    }
	};
	
	public boolean onTouchEvent(MotionEvent event) {
		if(mAdapter != null) {
		    int action = event.getAction();
		    if (action == MotionEvent.ACTION_CANCEL) {
		        
		    }
		    return super.onTouchEvent(event) || mGestureDetector.onTouchEvent(event) || mScaleGestureDetector.onTouchEvent(event);
		    //return super.onTouchEvent(event) || mGestureDetector.onTouchEvent(event);
		}
		return false;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMeasurementChanged = true;
	}
	
    @Override
    protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		if(mAdapter != null) {
	    	if(mMeasurementChanged) {
	    		mMeasurementChanged = false;
	    		mGraphAreaHeight = getMeasuredHeight() - mGraphBottomPading - mGraphTopPading - 2;
	    		mGraphAreaWidth = getMeasuredWidth();
	    		
	    		calkYKoef();
	    		calkXKoef();
	    		
				performZoom(1, 0);
	    	}
	    	
	    	if (mScroller.computeScrollOffset()) {
	    		performScroll(mScroller.getCurrX() - mScrollPosition);
	    		mScrollPosition = mScroller.getCurrX();
	        }

	    	drawAxis(canvas);
	    	drawGraph(canvas);
	    	
	    	for(HeaderDrawer header: mHeaderDrawers)
	    		header.draw(this, canvas);
	    	
	    	if (!mScroller.isFinished()) {
	            ViewCompat.postInvalidateOnAnimation(this);
	        }
		}
    }
    
   /* @Override
    protected Parcelable onSaveInstanceState() {
        SavedState st = new SavedState(super.onSaveInstanceState());
        st.position = mPosition;
        st.zoom = mZoomX;
        return st;
    }
    
    protected void onRestoreInstanceState(Parcelable state) {
    	  if (!(state instanceof SavedState)) {
    	    super.onRestoreInstanceState(state);
    	    return;
    	  }

    	  SavedState ss = (SavedState) state;
    	  super.onRestoreInstanceState(ss.getSuperState());

    	  mPosition = ss.position;
    	  mZoomX = ss.zoom;
    	};
    */
    public void setAdapter(GraphAdapter pAdapter) {
    	reset();
    	mAdapter = pAdapter;
    	refrash();
    }
    
	public long getPosition() {
		return mPosition;
	}

	public long getVisibleTimeInterval() {
		return mVisibleTimeInterval;
	}
	
	public float getGraphAreaWidth() {
		return mGraphAreaWidth;
	}
	
	public float getGraphAreaHeight() {
		return mGraphAreaHeight;
	}
	
	public float getGraphTopPading() {
		return mGraphTopPading;
	}
	
	public float getGraphBottompPading() {
		return mGraphBottomPading;
	}
	
	public float getMinValue() {
		return mMinValue;
	}
	
	public float getMaxValue() {
		return mMaxValue;
	}
    
    private static final float valueOffset = 0.00f;
    public void fixMaxMinValue(float pMax, float pMin) {
    	mMinMaxFixed = true;
    	mMaxValue = pMax + (pMax - pMin) * valueOffset;
    	mMinValue = pMin - (pMax - pMin) * valueOffset;
    	if(mMaxValue - mMinValue < mMinValieInterval) {
    		float additionalInterval = (mMinValieInterval - (mMaxValue - mMinValue)) / 2;
    		mMaxValue += additionalInterval;
    		mMinValue -= additionalInterval;
    	}
    	if(mMaxValue != (int) mMaxValue) mMaxValue = (int) mMaxValue + 1;
    	if(mMinValue != (int) mMinValue) mMinValue = (int) mMinValue;
    }
    
    private void calkXKoef() {
		mXKoef = mGraphAreaWidth / (float) (mVisibleTimeInterval);
        mValueOverscroll = (long) (mOverscroll / mXKoef);
        mXAxisDrawer.onScale(this);
    }
    
    private void calkYKoef() {
		mYKoef = mGraphAreaHeight / (float) (mMaxValue - mMinValue);
		mYAxisDrawer.onScale(this);
    }

	private void refrash() {
		mPointsPool.addAll(mPointsList);
		mPointsList.clear();
		if(mAdapter != null) {
			GraphPoint point = mAdapter.getLastPoint(getConvertPoint());
			if(point != null) {
				mPosition = point.getX();
				mMaxPosition = mPosition;
				if(!mMinMaxFixed) {
					mMaxValue = point.getY();
					mMinValue = point.getY();
				}
				mPointsList.addFirst(point);
				fillLeft();
			}
		}
		invalidate();
	}
	
	private boolean fillLeft() {
		boolean pointsAdded = false;
		int position = mPointsList.getFirst().getId();
		GraphPoint convertPoint = getConvertPoint();
		GraphPoint point = mAdapter.getPoint(position - 1, convertPoint);
		if(convertPoint != null && convertPoint != point) mPointsPool.add(convertPoint);
		while(point != null) {
			pointsAdded = true;
			mPointsList.addFirst(point);
			if(!mMinMaxFixed) {
				if(point.getY() > mMaxValue) mMaxValue = point.getY();
				if(point.getY() < mMinValue) mMinValue = point.getY();
			}
			if(point.getX() > mPosition - mVisibleTimeInterval) {
				convertPoint = getConvertPoint();
				point = mAdapter.getPreviousPoint(convertPoint);
				if(convertPoint != null && convertPoint != point) mPointsPool.add(convertPoint);
				if(point == null) {
					mMinPosition = mPointsList.getFirst().getX();
				}
			}
			else point = null;
		}
		return pointsAdded;
	}
	
	private boolean fillReight() {
		boolean pointsAdded = false;
		int position = mPointsList.getLast().getId();
		GraphPoint convertPoint = getConvertPoint();
		GraphPoint point = mAdapter.getPoint(position + 1, convertPoint);
		if(convertPoint != null && convertPoint != point) mPointsPool.add(convertPoint);
		while(point != null) {
			pointsAdded = true;
			mPointsList.addLast(point);
			if(!mMinMaxFixed) {
				if(point.getY() > mMaxValue) mMaxValue = point.getY();
				if(point.getY() < mMinValue) mMinValue = point.getY();
			}
			if(point.getX() < mPosition) {
				convertPoint = getConvertPoint();
				point = mAdapter.getNextPoint(convertPoint);
				if(convertPoint != null && convertPoint != point) mPointsPool.add(convertPoint);
			}
			else point = null;
		}
		return pointsAdded;
	}
	
	private void performScroll(float pDistance) {
		mPosition += pDistance / mXKoef;
		if (mPosition >= mMaxPosition + mValueOverscroll) {
			mPosition = mMaxPosition + mValueOverscroll;
			mScroller.forceFinished(true);
		}
		if (mPosition < mMinPosition + mVisibleTimeInterval - mValueOverscroll) {
			mPosition = mMinPosition + mVisibleTimeInterval - mValueOverscroll;
			mScroller.forceFinished(true);
		}
		
		if(mPointsList.getFirst().getX() > mPosition - mVisibleTimeInterval)
			fillLeft();
		if(mPointsList.getLast().getX() < mPosition && mPosition < mMaxPosition)
			fillReight();
	}
	
	private boolean performZoom(float pScaleFactor, float pFocusX) {
		float newZoom = mZoomX * pScaleFactor;
        if (newZoom < mMinZoomX) {
        	newZoom = mMinZoomX;
        }
        if (newZoom > mMaxZoomX) {
        	newZoom = mMaxZoomX;
        }
        if((long) (mTimeInterval / newZoom) > mMaxPosition - mMinPosition + mValueOverscroll * 2) {
        	mXKoef = (mGraphAreaWidth - mOverscroll * 2) / (mMaxPosition - mMinPosition);
        	mValueOverscroll = (long) (mOverscroll / mXKoef);
        	mVisibleTimeInterval = mMaxPosition - mMinPosition + mValueOverscroll * 2;
        	mMinZoomX = (float) mTimeInterval / (float) mVisibleTimeInterval;
        	if(mMinZoomX > mMaxZoomX) mMinZoomX = mMaxZoomX;
        	mZoomX = mMinZoomX;
        	mPosition = mMaxPosition;
        	calkXKoef();
        	performScroll(0);
        	return true;
        }
        if(newZoom != mZoomX) {
        	mZoomX = newZoom;
        	mVisibleTimeInterval = (long) (mTimeInterval / mZoomX);
        	calkXKoef();
        	performScroll(-mGraphAreaWidth * (pScaleFactor - 1) * (mGraphAreaWidth - pFocusX) / mGraphAreaWidth);
        	return true;
        }
        else performScroll(0);
        return false;
	}
	
	private GraphPoint getConvertPoint() {
		if(mPointsPool.size() > 0) return mPointsPool.removeLast();
		return null;
	}
	
	private void drawGraph(Canvas canvas) {
		refrashGraphPath();
		canvas.drawPath(mGraphPath, mGraphPaint);
		if(mPointsList.size() >= 2) {
			GraphPoint point = mPointsList.getLast();
			canvas.drawCircle((point.getX() - mPosition) * mXKoef + getMeasuredWidth(),
					mGraphAreaHeight + mGraphTopPading - (float) (point.getY() - mMinValue) * mYKoef,
					mPointRadius, mGraphPaint);
			ListIterator<GraphPoint> iterator = mPointsList.listIterator(mPointsList.size() - 1);
			while (iterator.hasPrevious()) {
				point = iterator.previous();
				canvas.drawCircle((point.getX() - mPosition) * mXKoef + mGraphAreaWidth,
						mGraphAreaHeight + mGraphTopPading - (float) (point.getY() - mMinValue) * mYKoef,
						mPointRadius, mGraphPaint);
			}
		}
		
	}
	
	private void drawAxis(Canvas canvas) {
		mXAxisDrawer.draw(canvas, this);
		mYAxisDrawer.draw(canvas, this);
	}
	
	private void refrashGraphPath() {
		mGraphPath.reset();
		if(mPointsList.size() >= 2) {
			ListIterator<GraphPoint> iterator = mPointsList.listIterator(mPointsList.size() - 1);
			GraphPoint graphPoint = mPointsList.getLast();
			mGraphPath.moveTo((graphPoint.getX() - mPosition) * mXKoef + getMeasuredWidth(),
					mGraphAreaHeight + mGraphTopPading - (float) (graphPoint.getY() - mMinValue) * mYKoef);
			
			graphPoint = iterator.previous();
			boolean preLastPointIsVisible = graphPoint.getX() < mPosition;
			while(graphPoint != null) {
				mGraphPath.lineTo((graphPoint.getX() - mPosition) * mXKoef + getMeasuredWidth(),
						mGraphAreaHeight + mGraphTopPading - (float) (graphPoint.getY() - mMinValue) * mYKoef);
				if(iterator.hasPrevious()) graphPoint = iterator.previous();
				else graphPoint = null;
			}
			boolean secondPointIsVisible = iterator.next().getX() > mPosition - mVisibleTimeInterval;
			
			if(!preLastPointIsVisible) mPointsPool.add(mPointsList.removeLast());
			if(!secondPointIsVisible) mPointsPool.add(mPointsList.removeFirst());
		}
	}
	
	private float getDpToPx(float pDp) {
		return pDp * (mDisplayMetrics.densityDpi / 160f);
	}
	
	public static class SavedState extends BaseSavedState {

	    long position;
	    float zoom;

	    SavedState(Parcelable superState) {
	        super(superState);
	    }

	    @Override
	    public void writeToParcel(Parcel out, int flags) {
	          super.writeToParcel(out, flags);
	          out.writeLong(position);
	          out.writeFloat(zoom);
	    }

	    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
	        public SavedState createFromParcel(Parcel in) {
	        return new SavedState(in);
	        }

	        public SavedState[] newArray(int size) {
	            return new SavedState[size];
	        }
	    };

	    private SavedState(Parcel in) {
	          super(in);
	          position = in.readLong();
	          zoom = in.readFloat();
	    }

	}
}
