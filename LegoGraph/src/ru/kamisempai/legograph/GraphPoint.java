package ru.kamisempai.legograph;

public class GraphPoint {
	private int mId;
	private float mY;
	private long mX;
	
	public float getY() {
		return mY;
	}
	public void setY(float value) {
		this.mY = value;
	}
	public long getX() {
		return mX;
	}
	public void setX(long value) {
		this.mX = value;
	}
	public int getId() {
		return mId;
	}
	public void setId(int id) {
		this.mId = id;
	}
}
