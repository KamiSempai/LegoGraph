package ru.kamisempai.legograph.drawer;

import ru.kamisempai.legograph.view.LegoGraphView;
import android.graphics.Canvas;

public interface AxisDrawer {
	public void onScale(LegoGraphView graphView);
	public void draw(Canvas canvas, LegoGraphView graphView);
	
	public float getTopPadding();
	public float getBottomPadding();
}
