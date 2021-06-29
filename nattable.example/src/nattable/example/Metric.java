 
package nattable.example;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Metric {
	@Inject
	public Metric() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		final List<MetricData> list = createMetrics();
		
		IColumnPropertyAccessor<MetricData> columnAccessor = new IColumnPropertyAccessor<Metric.MetricData>() {

			@Override
			public Object getDataValue(MetricData rowObject, int columnIndex) {
				switch (columnIndex) {
				case 0: return rowObject.id;
				case 1: return rowObject.name;
				case 2: return rowObject.description;
				case 3: return rowObject.formula;
				case 4: return rowObject.visible;
				}
				return null;
			}

			@Override
			public void setDataValue(MetricData rowObject, int columnIndex, Object newValue) {
				switch (columnIndex) {
				case 0: rowObject.id = (int) newValue; break;
				case 1: rowObject.name = (String) newValue; break;
				case 2: rowObject.description = (String) newValue; break;
				case 3: rowObject.formula = (String) newValue; break;
				case 4: rowObject.visible = (boolean) newValue; break;
				}
			}

			@Override
			public int getColumnCount() {
				return 5;
			}

			@Override
			public String getColumnProperty(int columnIndex) {
				return String.valueOf(columnIndex);
			}

			@Override
			public int getColumnIndex(String propertyName) {
				return Integer.valueOf(propertyName);
			}
		};
		ListDataProvider<MetricData> dataProvider = new ListDataProvider<Metric.MetricData>(list, columnAccessor);
		
		DefaultGridLayer gridLayer = new DefaultGridLayer(dataProvider, new IDataProvider() {
			
			@Override
			public void setDataValue(int columnIndex, int rowIndex, Object newValue) {}
			
			@Override
			public int getRowCount() {
				return 1;
			}
			
			@Override
			public Object getDataValue(int columnIndex, int rowIndex) {
				switch (columnIndex) {
				case 0: return "Id";
				case 1: return "Name";
				case 2: return "Description";
				case 3: return "Formula";
				case 4: return "Visible";
				}
				return null;
			}
			
			@Override
			public int getColumnCount() {
				return 5;
			}
		});
		
        ColumnLabelAccumulator columnLabelAccumulator = new ColumnLabelAccumulator(dataProvider);
        ((DataLayer) gridLayer.getBodyDataLayer()).setConfigLabelAccumulator(columnLabelAccumulator);

        NatTable natTable = new NatTable(parent, gridLayer, false);
        natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
        natTable.addConfiguration(new EditConfiguration());

        natTable.configure();
	}
	
	
	public class EditConfiguration extends AbstractRegistryConfiguration {

		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
			
	        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(),
	                DisplayMode.EDIT, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);

	        // The CheckBoxCellEditor can also be visualized like a check button
	        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
	                DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);

	        // using a CheckBoxCellEditor also needs a Boolean conversion to work
	        // correctly
	        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
	                new DefaultBooleanDisplayConverter(), DisplayMode.NORMAL,
	                ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 4);
		}
	}
	
	private List<MetricData> createMetrics() {
		List<MetricData> list = new ArrayList<Metric.MetricData>();
		for (int i=0; i<100; i++) {
			MetricData data = new MetricData();
			data.id = i;
			data.name = "metric " + i;
			data.description = "metric long description that no one care " + i;
			data.formula = "formula " + i;
			data.visible = i % 2 == 0;
			list.add(data);
		}
		return list;
	}
	
	private static class MetricData {
		int id;
		String name;
		String description;
		String formula;
		boolean visible;
	}
	
	
	public static void main(String []args ) {
        // Setup
        Display display = Display.getDefault();
        Shell shell = new Shell(display, SWT.SHELL_TRIM);
        shell.setLayout(new FillLayout());
        shell.setSize(800, 500);
        shell.setText("test table");
        
        Metric metric = new Metric();
        
        // Create example control
        metric.postConstruct(shell);

        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        shell.dispose();
        display.dispose();

	}
	
}