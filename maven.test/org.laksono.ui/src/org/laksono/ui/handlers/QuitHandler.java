package org.laksono.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class QuitHandler {
	@Execute
	public void execute(IWorkbench workbench, Shell shell) {

		boolean quit = MessageDialog.openConfirm(shell, "Confirmation", "Do you want to exit?");

		if (quit) {
			workbench.close();
		}
	}
}
