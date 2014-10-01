package ru.kamisempai.legograph.drawer;

import ru.kamisempai.legograph.view.LegoGraphView;
import android.graphics.Canvas;

public interface HeaderDrawer {
	public void draw(LegoGraphView graphView, Canvas canvas);
	public float getHeight();
}
