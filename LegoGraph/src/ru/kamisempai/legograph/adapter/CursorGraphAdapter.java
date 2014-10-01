package ru.kamisempai.legograph.adapter;

import ru.kamisempai.legograph.GraphPoint;
import android.database.Cursor;

public class CursorGraphAdapter implements GraphAdapter {
	private Cursor mCursor;
	private final int mDateColumnIndex;
	private final int mValueColumnIndex;
	private final long mTimeInterval;

	public CursorGraphAdapter(Cursor pCursor, int pDateColumnIndex, int pValueColumnIndex, long pTimeInterval) {
		mCursor = pCursor;
		mDateColumnIndex = pDateColumnIndex;
		mValueColumnIndex = pValueColumnIndex;
		mTimeInterval = pTimeInterval;
	}
	
	public GraphPoint getCurentPoint(GraphPoint pConvertPoint) {
		GraphPoint point = null;
		if(pConvertPoint == null)
			point = new GraphPoint();
		else point = pConvertPoint;
		point.setId(mCursor.getPosition());
		point.setX(mCursor.getLong(mDateColumnIndex) * mTimeInterval);
		point.setY(mCursor.getFloat(mValueColumnIndex));
		return point;
	}
	
	public GraphPoint getLastPoint(GraphPoint pConvertPoint) {
		if(mCursor.moveToLast())
			return getCurentPoint(pConvertPoint);
		return null;
	}
	
	public GraphPoint getPreviousPoint(GraphPoint pConvertPoint) {
		if(mCursor.moveToPrevious())
			return getCurentPoint(pConvertPoint);
		return null;
	}
	
	public GraphPoint getNextPoint(GraphPoint pConvertPoint) {
		if(mCursor.moveToNext())
			return getCurentPoint(pConvertPoint);
		return null;
	}
	
	public GraphPoint getPoint(int pPosition, GraphPoint pConvertPoint) {
		if(mCursor.moveToPosition(pPosition))
			return getCurentPoint(pConvertPoint);
		return null;
	}

	@Override
	public String valueToString(float pValue) {
		return Integer.toString((int) pValue);
	}
}
