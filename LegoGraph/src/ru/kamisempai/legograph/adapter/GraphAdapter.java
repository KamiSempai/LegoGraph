package ru.kamisempai.legograph.adapter;

import ru.kamisempai.legograph.GraphPoint;

public interface GraphAdapter {
	
	public GraphPoint getCurentPoint(GraphPoint pConvertPoint);
	public GraphPoint getLastPoint(GraphPoint pConvertPoint);
	public GraphPoint getPreviousPoint(GraphPoint pConvertPoint);
	public GraphPoint getNextPoint(GraphPoint pConvertPoint);
	public GraphPoint getPoint(int pPosition, GraphPoint pConvertPoint);
	public String valueToString(float pValue);
}
