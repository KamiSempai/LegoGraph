package ru.kamisempai.legograph.drawer;

import ru.kamisempai.legograph.view.LegoGraphView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class YAxisDrawer implements AxisDrawer {

	private float mAxisYCount = 6; // ���������� ����
	private float mAxisYStep; // ���������� ����� ����� � ��������
	private float mAxisYValueStep;
	
	private int mTextSize; // ������ ������
	private Paint mAxisTextPaint;
	private Paint mAxisPaint; // ������ ��� ����
	
	public YAxisDrawer() {
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
		float axisPosition = graphView.getGraphAreaHeight() - 1 + graphView.getGraphTopPading();
		float textOffset = 5;
		float axisValue = graphView.getMinValue();
		while(axisPosition > 0) {
			canvas.drawLine(0, axisPosition, graphView.getMeasuredWidth(),  axisPosition, mAxisPaint);
			if(axisPosition - mAxisYStep > 0) canvas.drawText(valueToString(axisValue), 0, axisPosition - textOffset, mAxisTextPaint);
			axisPosition -= mAxisYStep;
			axisValue += mAxisYValueStep;
		}
	}

	@Override
	public void onScale(LegoGraphView graphView) {
		mAxisYValueStep = (int) ((graphView.getMaxValue() - graphView.getMinValue()) / mAxisYCount);
		if(mAxisYValueStep == 0)
			mAxisYValueStep = 1;
		mAxisYStep = graphView.getGraphAreaHeight() * mAxisYValueStep / (graphView.getMaxValue() - graphView.getMinValue());
	}

	public String valueToString(float pValue) {
		return Integer.toString((int) pValue);
	}

	@Override
	public float getTopPadding() {
		return 0;
	}

	@Override
	public float getBottomPadding() {
		return 0;
	}

}
