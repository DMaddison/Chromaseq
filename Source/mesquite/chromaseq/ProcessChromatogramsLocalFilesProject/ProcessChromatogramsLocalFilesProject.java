/* Mesquite chromaseq source code.  Copyright 2005-2009 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.ProcessChromatogramsLocalFilesProject; import mesquite.lib.*;import mesquite.lib.duties.*;import mesquite.chromaseq.lib.*;/* ======================================================================== */public class ProcessChromatogramsLocalFilesProject extends GeneralFileMaker { 	//for importing sequences	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		return true;	}	/*.................................	................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return true;	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return false;	}	/** make a new    MesquiteProject.*/	public MesquiteProject establishProject(String arguments){		MesquiteProject project = null;		boolean success= false;		ChromatogramProcessor chromatogramProcessorTask = (ChromatogramProcessor)hireEmployee(ChromatogramProcessor.class, "Module to process chromatograms");		if (chromatogramProcessorTask != null){			FileCoordinator fileCoord = getFileCoordinator();			MesquiteFile thisFile = new MesquiteFile();			project = fileCoord.initiateProject(thisFile.getFileName(), thisFile);			success= chromatogramProcessorTask.processChromatograms(project, false);			fireEmployee(chromatogramProcessorTask);			if (success){				project.autosave = true;				return project;			}			project.developing = false;		}		return null;	}	/*.................................................................................................................*/	public String getName() {		return "Process Chromatogram Files in Directory...";	}	/*.................................................................................................................*/	public boolean showCitation() {		return false;	}	/*.................................................................................................................*/	public String getExplanation() {		return "Prepares a folder of chromatogram files for processing, and processes them.";	}	/*.................................................................................................................*/}