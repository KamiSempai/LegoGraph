package ru.kamisempai.legograph.drawer;

import java.util.Calendar;

import ru.kamisempai.legograph.view.LegoGraphView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DaysXAxisDrawer implements AxisDrawer {
	// AxisX parameters
	private long mAxisXStep = 86400000;
	private long mAxisXCurrentStep = mAxisXStep;
	private float mAxisXMinSize = 80;
	private float mXKoef;
	private int mTextSize;
	private Paint mAxisTextPaint;
	private Paint mAxisPaint;
	private Calendar mCalendar;
	
	public DaysXAxisDrawer() {
		mCalendar = Calendar.getInstance();
        
		mTextSize = 30;
		
		mAxisTextPaint = new Paint();
		mAxisTextPaint.setAntiAlias(true);
		mAxisTextPaint.setColor(Color.BLACK);
		mAxisTextPaint.setTextSize(mTextSize);

		mAxisPaint = new Paint();
		mAxisPaint.setStyle(Paint.Style.STROKE);
		mAxisPaint.setStrokeJoin(Paint.Join.ROUND);
		mAxisPaint.setStrokeCap(Paint.Cap.ROUND);
		mAxisPaint.setColor(Color.BLACK);
		mAxisPaint.setStrokeWidth(1);
	}

	@Override
	public void draw(Canvas canvas, LegoGraphView graphView) {
		long axisPsition = graphView.getPosition() - graphView.getPosition() % mAxisXCurrentStep;
		while(axisPsition > graphView.getPosition() - graphView.getVisibleTimeInterval() - mAxisXCurrentStep) {
			mCalendar.setTimeInMillis(axisPsition);
			float xPosition = (axisPsition - graphView.getPosition()) * mXKoef + graphView.getMeasuredWidth();
			canvas.drawLine(xPosition, graphView.getGraphTopPading(), xPosition,  graphView.getGraphAreaHeight() + graphView.getGraphTopPading(), mAxisPaint);
			canvas.drawText(Integer.toString(mCalendar.get(Calendar.DAY_OF_MONTH)), xPosition, graphView.getGraphAreaHeight() + mTextSize + graphView.getGraphTopPading(), mAxisTextPaint);
			axisPsition -= mAxisXCurrentStep;
		}
	}

	@Override
	public void onScale(LegoGraphView graphView) {
		mXKoef = graphView.getGraphAreaWidth() / (float) (graphView.getVisibleTimeInterval());
		mAxisXCurrentStep = mAxisXStep;
		while(mAxisXCurrentStep * mXKoef < mAxisXMinSize) mAxisXCurrentStep += mAxisXStep;
	}

	@Override
	public float getTopPadding() {
		return 0;
	}

	@Override
	public float getBottomPadding() {
		return mTextSize;
	}

}
