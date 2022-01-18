package org.laksono.data;

import java.util.Random;

public class DataFactory {
	
	public static DataModel create(int depth) {
		DataModel root = new DataModel(null, Integer.valueOf(0));
		DataModel child = createData(root, depth-1);
		root.addChild(child);
		
		return root;
	}
	
	public static DataModel createData(DataModel parent, int depth) {
		if (depth <= 0)
			return null;
		
		DataModel data = new DataModel(parent, Integer.valueOf(depth));
		parent.addChild(data);
		if (depth == 1)
			return data;
		
		Random r = new Random();
		int numChildren = r.nextInt(10);
		for (int i=0; i<numChildren; i++) {
			DataModel child = createData(data, depth-1);
			data.addChild(child);
		}
		return data;
	}
}
