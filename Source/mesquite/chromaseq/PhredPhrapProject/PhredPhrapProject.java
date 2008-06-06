/* Mesquite chromaseq source code.  Copyright 2005-2008 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.PhredPhrapProject; import java.io.*;import java.util.*;import java.awt.*;import java.awt.event.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;import mesquite.cont.lib.*;import mesquite.chromaseq.lib.*;/* ======================================================================== */public class PhredPhrapProject extends GeneralFileMaker { 	//for importing sequences	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		//MesquiteTrunk.mesquiteTrunk.addMenuItem(MesquiteTrunk.fileMenu, "New Project from Chromatograms...", makeCommand("runPhredPhrap", this));		//MesquiteTrunk.mesquiteTrunk.addMenuItem(MesquiteTrunk.fileMenu, "-", null);		return true;	}	/*.................................	................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return true;	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return false;	}	/** make a new    MesquiteProject.*/	public MesquiteProject establishProject(String arguments){		MesquiteProject project = null;		boolean success= false;		PhPhRunner phphTask = (PhPhRunner)hireEmployee(PhPhRunner.class, "Module to run Phred & Phrap");		if (phphTask != null){			FileCoordinator fileCoord = getFileCoordinator();			MesquiteFile thisFile = new MesquiteFile();			project = fileCoord.initiateProject(thisFile.getFileName(), thisFile);			success= phphTask.doPhredPhrap(project, false);			fireEmployee(phphTask);			if (success){				project.autosave = true;				return project;			}			project.developing = false;		}		return null;	}	/*.................................................................................................................*    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	 if (checker.compare(this.getClass(), "Prepare ABI files for processing by Phred and Phrap and run them", null, commandName, "runPhredPhrap")) {    	 		PhPhRunner phphTask = (PhPhRunner)hireEmployee(PhPhRunner.class, "Module to run Phred & Phrap");    	 		if (phphTask != null)    	 			phphTask.doPhredPhrap(MesquiteTrunk.makeBlankProject(), false);    	 	}    	 	else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }	/*.................................................................................................................*/	public String getName() {		return "Phred/Phrap Import from Local Chromatograms...";	}	/*.................................................................................................................*/	public boolean showCitation() {		return false;	}	/*.................................................................................................................*/	public String getExplanation() {		return "Prepares a folder of abi files for Phred and Phrap, and makes a shell script to invoke Phred and Phrap within each folder, and runs the script.";	}	/*.................................................................................................................*/}