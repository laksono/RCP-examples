package nattable.example;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.tree.GlazedListTreeData;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.TreeRowModel;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.TreeList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class NatVog2 {

	public NatVog2(Composite parent) {
        final TreeItem<Todo> root = TreeItem.create(listChildren);
        
        EventList<TreeItem<Todo>> eventList = GlazedLists.eventList(listChildren);
		ObservableElementList<TreeItem<Todo>> observableElementList = new ObservableElementList<>(eventList,
		        GlazedLists.beanConnector(TreeItem.class));
		
        Composite btnArea = new Composite(parent, SWT.BORDER);
        
        Button btnAdd = new Button(btnArea, SWT.PUSH);
        btnAdd.setText("Add");
        btnAdd.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		if (eventList != null) {
        			int size = eventList.size();
        			Todo t = new Todo(size, "Add " + size, "Desc " + size);
        			TreeItem<Todo> ti = new TreeItem<NatVog2.Todo>(t);
        			root.add(ti);
        			ti.setParent(root);
        			
        			observableElementList.add(ti);
        			System.out.println("add: " + ti + ".  so: " + observableElementList.size() + 
							". sl: " + listChildren.size() + 
							". el: " + eventList.size());
        		}
        	}
		});
        
        Button btnRemove = new Button(btnArea, SWT.PUSH);
        btnRemove.setText("Remove");
        btnRemove.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		int last = observableElementList.size();
    			TreeItem<Todo> ti = observableElementList.get(last-1);
    			observableElementList.remove(ti);
    			System.out.println("  remove: " + ti + ".  so: " + observableElementList.size() + 
    								". sl: " + listChildren.size() + 
    								". el: " + eventList.size());
        	}
		});
        
        GridDataFactory.swtDefaults().grab(true, false).applyTo(btnArea);
        GridLayoutFactory.swtDefaults().numColumns(3).applyTo(btnArea);

        TreeList<TreeItem<Todo>> treeList = new TreeList<TreeItem<Todo>>(observableElementList, new TreeItemFormat(),
                														 new TodoExpansionModel());
        GlazedListTreeData<TreeItem<Todo>> glazedListTreeData = new GlazedListTreeData<>(treeList);
        TreeRowModel<TreeItem<Todo>> glazedListTreeRowModel = new TreeRowModel<>(glazedListTreeData);

        // layers
        IDataProvider dataProvider = new TodoDataProvider(observableElementList);
        DataLayer dataLayer = new DataLayer(dataProvider);

        // add a GlazedListsEventLayer event layer that is responsible for
        // updating the grid on list changes

        GlazedListsEventLayer<TreeItem<Todo>> glazedListsEventLayer = new GlazedListsEventLayer<>(dataLayer, eventList);
        
        TreeLayer treeLayer = new TreeLayer(glazedListsEventLayer, glazedListTreeRowModel);
        treeLayer.setRegionName(GridRegion.BODY);

        SelectionLayer selectionLayer = new SelectionLayer(treeLayer);

        ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

        NatTable nat = new NatTable(parent, viewportLayer, true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(nat);

        GridLayoutFactory.fillDefaults().generateLayout(parent);
    }
	
	
	
	private static class Todo {

		private int id;
		private String summary;
		private String description;

		public Todo(int id) {
			this(id, "", "");
		}

		public Todo(int id, String summary, String description) {
			this.id = id;
			this.summary = summary;
			this.description = description;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return getId() + " : " + getSummary();
		}	
	}
	
	
	private static class TodoExpansionModel implements TreeList.ExpansionModel<TreeItem<Todo>> {

		@Override
		public boolean isExpanded(TreeItem<Todo> element, List<TreeItem<Todo>> path) {
			System.out.println("ele: " + element.getItem() +":" + element.hasChildren() + ". p: " + path);
			return element.hasChildren(); //element.getParent() == null;
		}

		@Override
		public void setExpanded(TreeItem<Todo> element, List<TreeItem<Todo>> path, boolean expanded) {
		}		
	}
	

	public static class TreeItem<T> implements Iterable<TreeItem<T>> {
		protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
		
		private List<TreeItem<T>> children = new ArrayList<>();
		private TreeItem<T> parent;
		private T item;
		
		public TreeItem(T item) {
			this.item = item;
			this.parent = null;
			this.children = new ArrayList<NatVog2.TreeItem<T>>(0);
		}
		
		public T getItem() {
			return item;
		}

		public TreeItem<T> getParent() {
			return parent;
		}
		
		public void setParent(TreeItem<T> parent) {
			this.parent = parent;
		}
		
		public void add(TreeItem<T> child) {
			child.setParent(this);
			children.add(child);
		}
		
		public boolean hasChildren() {
			return !children.isEmpty();
		}
		
		public void remove(TreeItem<T> child) {
			child.setParent(null);
			children.remove(child);
		}

		public void addPropertyChangeListener( PropertyChangeListener listener ) {
			changeSupport.addPropertyChangeListener(listener);
		}
		
		public void removePropertyChangeListener( PropertyChangeListener listener ) {
			changeSupport.removePropertyChangeListener(listener);
		}
		
		@Override
		public Iterator<TreeItem<T>> iterator() {
			return children.iterator();
		}
		
		@Override
		public String toString() {
			return String.valueOf(getItem());
		}
		
		public static TreeItem<Todo> create(List<TreeItem<Todo>> listChildren) {
			Todo r = new Todo(0, "root", "parent");
			TreeItem<Todo> root = new TreeItem<NatVog2.Todo>(r);
			listChildren.add(root);
			
			for (int i=0; i<4; i++) {				
				TreeItem<Todo> tchild = new TreeItem<NatVog2.Todo>(new Todo((i+1)*10, "child " + i, "desc " + i));
				root.add(tchild);
				tchild.setParent(root);
				listChildren.add(tchild);
				
				for (int j=0; j< i+2; j++) {
					Todo td = new Todo(100*(i+1)+j, "cchild " + i + "." + j, "cdesc " + i + "." + j);
					TreeItem<Todo> ti = new TreeItem<NatVog2.Todo>(td);
					ti.setParent(tchild);
					tchild.add(ti);
					listChildren.add(ti);
				}
			}
			
			return root;
		}
	}	
	
	private List<TreeItem<Todo>> listChildren = new ArrayList<NatVog2.TreeItem<Todo>>();
	
	
	
	private static class TreeItemFormat implements TreeList.Format<TreeItem<Todo>> {

	    @Override
	    public void getPath(List<TreeItem<Todo>> path, TreeItem<Todo> element) {
	    	TreeItem<Todo> item = element;
	        path.add(item);
	    	
	        while (item.getParent() != null) {
	            item = item.getParent();
	            path.add(item);
	        }
	        Collections.reverse(path);
	    }

	    @Override
	    public boolean allowsChildren(TreeItem<Todo> element) {
	        return element.hasChildren();
	    }

	    @Override
	    public Comparator<? super TreeItem<Todo>> getComparator(int depth) {
	        return (o1, o2) -> o1.getItem().getSummary().compareTo(o2.getItem().getSummary());
	    }
	}
	
	
	private class TodoDataProvider implements IDataProvider {

		private List<TreeItem<Todo>> persons;

		public TodoDataProvider(EventList<TreeItem<Todo>> persons) {
			this.persons = persons;
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			TreeItem<Todo> todo = persons.get(rowIndex);
			
			switch (columnIndex) {
			case 0: return todo.getItem().getSummary();
			case 1: return todo.getItem().getDescription();
			case 2: return todo.getItem().getId();
				
			}
			return todo;
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			TreeItem<Todo> todo = persons.get(rowIndex);
			switch (columnIndex) {
			case 0: todo.getItem().summary = (String) newValue;
					break;
			case 1: todo.getItem().description = (String) newValue;
					break;
			case 2: todo.getItem().id = (Integer) newValue;
					break;
			}
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return persons.size();
		}
	}
	
	
	
    public static void main(String[] argv) {
        Display display = new Display();
        Shell shell = new Shell(display);
        
        Label label = new Label(shell, SWT.NONE);
        label.setText("Table:");
        GridDataFactory.swtDefaults().grab(false, false).applyTo(label);

        NatVog2 nv = new NatVog2(shell);
		
        GridDataFactory.fillDefaults().grab(true, true).applyTo(shell);
		GridLayoutFactory.fillDefaults().applyTo(shell);
		
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
    }

}
