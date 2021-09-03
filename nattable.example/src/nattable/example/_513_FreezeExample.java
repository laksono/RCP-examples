/*******************************************************************************
 * Copyright (c) 2013, 2020 Dirk Fauth and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Dirk Fauth <dirk.fauth@googlemail.com> - initial API and implementation
 *******************************************************************************/
package nattable.example;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommandHandler;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.freeze.CompositeFreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.FreezeLayer;
import org.eclipse.nebula.widgets.nattable.freeze.config.DefaultFreezeGridBindings;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultBodyDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.persistence.command.DisplayPersistenceDialogCommandHandler;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.AbstractHeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * Simple example showing how to add the functionality for freezing regions to a
 * grid.
 *
 * Also adds the functionality to manage NatTable states to proof that the
 * visibility states are stored and loaded correctly.
 */
public class _513_FreezeExample {

    public static void main(String[] args) throws Exception {
		System.out.println("Test begin");

		final Display display = new Display();
		final Shell   shell   = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText("Test Tree");

		_513_FreezeExample te = new _513_FreezeExample();
		te.createExampleControl(shell);
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		 
		display.dispose();
		
		System.out.println("Test end");

    }


    public Control createExampleControl(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);

        // property names of the Person class
        String[] propertyNames = {
                "firstName",
                "lastName",
                "gender",
                "married",
                "birthday" };

        // mapping from property to label, needed for column header labels
        Map<String, String> propertyToLabelMap = new HashMap<>();
        propertyToLabelMap.put("firstName", "Firstname");
        propertyToLabelMap.put("lastName", "Lastname");
        propertyToLabelMap.put("gender", "Gender");
        propertyToLabelMap.put("married", "Married");
        propertyToLabelMap.put("birthday", "Birthday");

        // build the body layer stack
        // Usually you would create a new layer stack by extending
        // AbstractIndexLayerTransform and setting the ViewportLayer as
        // underlying layer. But in this case using the ViewportLayer directly
        // as body layer is also working.
        IDataProvider bodyDataProvider =
                new DefaultBodyDataProvider<>(
                        PersonService.getPersons(10),
                        propertyNames);
        DataLayer bodyDataLayer =
                new DataLayer(bodyDataProvider);
        ColumnReorderLayer columnReorderLayer =
                new ColumnReorderLayer(bodyDataLayer);
        ColumnHideShowLayer columnHideShowLayer =
                new ColumnHideShowLayer(columnReorderLayer);
        final SelectionLayer selectionLayer =
                new SelectionLayer(columnHideShowLayer);
        final ViewportLayer viewportLayer =
                new ViewportLayer(selectionLayer);

        final FreezeLayer freezeLayer =
                new FreezeLayer(selectionLayer);
        final CompositeFreezeLayer compositeFreezeLayer =
                new CompositeFreezeLayer(freezeLayer, viewportLayer, selectionLayer);

        // build the column header layer
        IDataProvider columnHeaderDataProvider =
                new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabelMap);
        DataLayer columnHeaderDataLayer =
                new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
        ColumnHeaderLayer columnHeaderLayer =
                new ColumnHeaderLayer(columnHeaderDataLayer, compositeFreezeLayer, selectionLayer);

        // build the row header layer
        IDataProvider rowHeaderDataProvider =
                new DefaultRowHeaderDataProvider(bodyDataProvider);
        DataLayer rowHeaderDataLayer =
                new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
        ILayer rowHeaderLayer =
                new RowHeaderLayer(rowHeaderDataLayer, compositeFreezeLayer, selectionLayer);

        // build the corner layer
        IDataProvider cornerDataProvider =
                new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
        DataLayer cornerDataLayer =
                new DataLayer(cornerDataProvider);
        ILayer cornerLayer =
                new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);

        // build the grid layer
        GridLayer gridLayer =
                new GridLayer(compositeFreezeLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        // turn the auto configuration off as we want to add our header menu
        // configuration
        final NatTable natTable = new NatTable(panel, gridLayer, false);

        // as the autoconfiguration of the NatTable is turned off, we have to
        // add the DefaultNatTableStyleConfiguration manually
        natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
        natTable.addConfiguration(new DefaultFreezeGridBindings());

        // add the menu configuration
        IMenuItemState freezeActiveState = new IMenuItemState() {

            @Override
            public boolean isActive(NatEventData natEventData) {
                return freezeLayer.isFrozen();
            }
        };
        natTable.addConfiguration(new AbstractHeaderMenuConfiguration(natTable) {
            @Override
            protected PopupMenuBuilder createColumnHeaderMenu(NatTable natTable) {
                return super.createColumnHeaderMenu(natTable)
                        .withHideColumnMenuItem()
                        .withShowAllColumnsMenuItem()
                        .withColumnChooserMenuItem()
                        .withFreezeColumnMenuItem()
                        .withUnfreezeMenuItem()
                        .withVisibleState(PopupMenuBuilder.UNFREEZE_MENU_ITEM_ID, freezeActiveState);
            }

            @Override
            protected PopupMenuBuilder createCornerMenu(NatTable natTable) {
                return super.createCornerMenu(natTable)
                        .withShowAllColumnsMenuItem()
                        .withStateManagerMenuItemProvider();
            }

            @Override
            protected PopupMenuBuilder createRowHeaderMenu(NatTable natTable) {
                return super.createRowHeaderMenu(natTable)
                        .withFreezeRowMenuItem()
                        .withUnfreezeMenuItem()
                        .withVisibleState(PopupMenuBuilder.UNFREEZE_MENU_ITEM_ID, freezeActiveState);
            }
        });

        natTable.addConfiguration(new AbstractUiBindingConfiguration() {

            private final Menu bodyMenu = new PopupMenuBuilder(natTable)
                    .withInspectLabelsMenuItem()
                    .withFreezePositionMenuItem(true)
                    .withUnfreezeMenuItem()
                    .withVisibleState(PopupMenuBuilder.UNFREEZE_MENU_ITEM_ID, freezeActiveState)
                    .build();

            @Override
            public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
                uiBindingRegistry.registerMouseDownBinding(
                        new MouseEventMatcher(SWT.NONE, null, 3),
                        new PopupMenuAction(this.bodyMenu));
            }

        });

        natTable.configure();

        panel.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().grab(true, true).applyTo(panel);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

        gridLayer.registerCommandHandler(
                new DisplayPersistenceDialogCommandHandler(natTable));

        DisplayColumnChooserCommandHandler columnChooserCommandHandler =
                new DisplayColumnChooserCommandHandler(
                        selectionLayer,
                        columnHideShowLayer,
                        columnHeaderLayer,
                        columnHeaderDataLayer,
                        null,
                        null);
        gridLayer.registerCommandHandler(columnChooserCommandHandler);

        return panel;
    }

}
