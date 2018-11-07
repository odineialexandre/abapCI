package abapci.testResult.visualizer;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Color;

import abapci.domain.AbapPackageTestState;
import abapci.utils.ColorChooser;

public class ResultVisualizerOutput {

	private String globalTestState; 
	private Color backgroundColor;
	private List<AbapPackageTestState> abapPackageTestStatesForCurrentProject; 

	private VisualizerInfolineBuilder visualizerInfolineBuilder;
	private boolean showAtcInfo;
	private IProject project; 
	
	private ColorChooser contrastColorDeterminer = new ColorChooser(); 
	
	
	public void setGlobalTestState(String globalTestState) {		
		this.globalTestState = globalTestState; 
		visualizerInfolineBuilder = new VisualizerInfolineBuilder(); 
	}
	
	public String getGlobalTestState() 
	{
		return globalTestState; 
	}

	public void setAbapPackageTestStates(List<AbapPackageTestState> abapPackageTestStatesForCurrentProject) {
		this.abapPackageTestStatesForCurrentProject = abapPackageTestStatesForCurrentProject; 
	}
	
	public List<AbapPackageTestState> getAbapPackageTestStates() 
	{
		return abapPackageTestStatesForCurrentProject; 
	}

	public void setCurrentProject(IProject project) 
	{
		this.project = project;  
	}

	public void setShowAtcInfo(boolean enabled) 
	{
		showAtcInfo = enabled; 
	}
	
	public String getInfoline() 
	{
		return visualizerInfolineBuilder.buildInfoLine(project, abapPackageTestStatesForCurrentProject, showAtcInfo); 
	}

	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}
	
	public  Color getBackgroundColor() {
		return backgroundColor; 
	}

	public Color getForegroundColor() {
       return contrastColorDeterminer.getContrastColor(backgroundColor); 
	}

}