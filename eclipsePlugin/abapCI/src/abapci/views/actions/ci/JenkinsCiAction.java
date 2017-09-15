package abapci.views.actions.ci;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import abapci.AbapCiPlugin;
import abapci.Domain.AbapPackageInfo;
import abapci.handlers.JenkinsHandler;

public class JenkinsCiAction extends AbstractCiAction {
	

	public JenkinsCiAction(String label, String tooltip) {
		this.setText(label);
		this.setToolTipText(tooltip);
		this.setImageDescriptor(AbapCiPlugin.getImageDescriptor("icons/jenkins.ico"));
	}

	public void run() {
		
		//TODO errorhandling if called without valid package name 
		//TODO Handling for more than one package 
		
		String firstPackage = null; 
		
		try {
			Map<String, String> packageNames = getSelectedPackages();
            firstPackage = packageNames.entrySet().iterator().next().getValue(); 
			
			new JenkinsHandler().execute(new ExecutionEvent(null, packageNames, null, null));
		
		}
		catch(Exception ex) 
		{
			// TODO errorhandling for exception in Jenkins call, e.g. wrong Url, username, password, ...	
		}
		
		UpdateViewerInput(new AbapPackageInfo(firstPackage), AbapCiActionEnum.JENKINS); 
	}
}
