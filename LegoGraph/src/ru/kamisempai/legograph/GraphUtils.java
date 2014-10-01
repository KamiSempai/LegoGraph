package ru.kamisempai.legograph;

public class GraphUtils {
	
	public static enum GraphType {
		CLEEN,
		EXERCISE,
		MEASURE,
		TRAINING_LEN
	}
	
	public static int typeToInt(GraphType pType) {
		int id = 0;
		for(GraphType type: GraphType.values()) {
			if(type == pType) return id;
			id++;
		}
		return 0;
	}
	
	public static GraphType intToType(int pIntType) {
		GraphType[] values = GraphType.values();
		if(pIntType < values.length) return values[pIntType];
		return GraphType.CLEEN;
	}

}
