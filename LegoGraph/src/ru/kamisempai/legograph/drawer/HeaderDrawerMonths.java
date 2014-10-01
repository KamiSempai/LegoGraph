package ru.kamisempai.legograph.drawer;

import java.util.Calendar;

import ru.kamisempai.legograph.view.LegoGraphView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public class HeaderDrawerMonths implements HeaderDrawer {
	private float textOffset;
	private Calendar mCalendar;
	private Paint mAxisTextPaint;


	private float mMonthTextMargin; // ���������� ����� ���������� ������
	private String[] mMonths; // �������� �������
	
	public HeaderDrawerMonths(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        textOffset = getDpToPx(displayMetrics, 2);
        mMonthTextMargin = getDpToPx(displayMetrics, 5);
        mCalendar = Calendar.getInstance();
        
        mMonths = new String[] {"1 month", "2 month", "3 month", "4 month", "5 month", "6 month", "7 month", "8 month", "9 month", "10 month", "11 month", "12 month"};
        
		mAxisTextPaint = new Paint();
		mAxisTextPaint.setAntiAlias(true);
		mAxisTextPaint.setColor(Color.BLACK);
		mAxisTextPaint.setTextSize(30);
	}

	@Override
	public void draw(LegoGraphView graphView, Canvas canvas) {
		float mXKoef = graphView.getGraphAreaWidth() / (float) (graphView.getVisibleTimeInterval());
		mCalendar.setTimeInMillis((graphView.getPosition() - graphView.getVisibleTimeInterval()) - (graphView.getPosition() - graphView.getVisibleTimeInterval()) % 86400000l);
		mCalendar.set(Calendar.DAY_OF_MONTH, 1);
		String firstMonth = mMonths[mCalendar.get(Calendar.MONTH)];
		mCalendar.add(Calendar.MONTH, 1);
		float firstTextWidth = mAxisTextPaint.measureText(firstMonth);
		float firstTextPosition = 1;
		float secondTextPosition = (mCalendar.getTimeInMillis() - graphView.getPosition()) * mXKoef + graphView.getMeasuredWidth();
		if(secondTextPosition < firstTextWidth + mMonthTextMargin + 1) firstTextPosition = secondTextPosition - firstTextWidth - mMonthTextMargin + 1;
		canvas.drawText(firstMonth, firstTextPosition, mAxisTextPaint.getTextSize() - textOffset, mAxisTextPaint);
		while(mCalendar.getTimeInMillis() < graphView.getPosition()) {
			String secondMonth = mMonths[mCalendar.get(Calendar.MONTH)];
			canvas.drawText(secondMonth, (mCalendar.getTimeInMillis() - graphView.getPosition()) * mXKoef + graphView.getMeasuredWidth(), mAxisTextPaint.getTextSize() - textOffset, mAxisTextPaint);
			mCalendar.add(Calendar.MONTH, 1);
		}
	}

	@Override
	public float getHeight() {
		return mAxisTextPaint.getTextSize();
	}
	
	private float getDpToPx(DisplayMetrics displayMetrics, float pDp) {
		return pDp * (displayMetrics.densityDpi / 160f);
	}

}
