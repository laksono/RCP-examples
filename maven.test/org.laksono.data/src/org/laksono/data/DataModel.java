package org.laksono.data;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
	private DataModel parent;
	private Object data;
	private List<DataModel> children;
	
	public DataModel(DataModel parent, Object data) {
		this.parent = parent;
		this.data   = data;
		children    = new ArrayList<>();
	}
	
	
	
	public DataModel getParent() {
		return parent;
	}
	
	public void setParent(DataModel parent) {
		this.parent = parent;
	}


	public Object getData() {
		return data;
	}


	public void setData(Object data) {
		this.data = data;
	}


	public List<DataModel> getChildren() {
		return children;
	}


	public void setChildren(List<DataModel> children) {
		this.children = children;
	}
	
	public void addChild(DataModel child) {
		this.children.add(child);
	}
}
