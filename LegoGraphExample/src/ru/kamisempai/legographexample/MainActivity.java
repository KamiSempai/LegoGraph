package ru.kamisempai.legographexample;

import ru.kamisempai.adaptgraphexample.R;
import ru.kamisempai.legograph.adapter.CursorGraphAdapter;
import ru.kamisempai.legograph.view.LegoGraphView;
import android.support.v7.app.ActionBarActivity;
import android.database.MatrixCursor;
import android.os.Bundle;

public class MainActivity extends ActionBarActivity {

	private static final Long[] DATES =   {100l, 110l , 120l, 130l, 140l, 150l};
	private static final Float[] VALUES = {3f, 1f,   2f,  4f,  -2f, 2f};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		LegoGraphView graphView = (LegoGraphView) findViewById(R.id.legoGraphView);
		
		MatrixCursor cursor = new MatrixCursor(new String[] {"date", "value"});
		
		for(int i = 0; i < DATES.length && i < VALUES.length; i++) {
			cursor.addRow(new Object[] {DATES[i], VALUES[i]});
		}
		
		CursorGraphAdapter adapter = new CursorGraphAdapter(cursor, cursor.getColumnIndex("date"), cursor.getColumnIndex("value"), 86400000l);
		
		graphView.setAdapter(adapter);
	}
}
