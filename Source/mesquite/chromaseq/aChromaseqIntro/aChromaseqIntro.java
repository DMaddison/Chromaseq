/* Mesquite chromaseq source code.  Copyright 2005-2009 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.aChromaseqIntro;import mesquite.lib.duties.*;import mesquite.lib.*;/* ======================================================================== */public class aChromaseqIntro extends PackageIntro {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		return true;	}	public Class getDutyClass(){		return aChromaseqIntro.class;	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return true;  	}	/*.................................................................................................................*/	/** returns the URL of the notices file for this module so that it can phone home and check for messages */	public String  getHomePhoneNumber(){ 		if (MesquiteTrunk.debugMode)			return "http://mesquiteproject.org/packages/chromaseq/noticesDev.xml";		else if (isPrerelease()) 			return "http://mesquiteproject.org/packages/chromaseq/noticesPrerelease.xml";		else			return "http://mesquiteproject.org/packages/chromaseq/notices.xml";	}	/*.................................................................................................................*/	public String getExplanation() {		return "Chromaseq is a package of Mesquite modules providing tools for processing and displaying chromatogram and sequence data.";	}	/*.................................................................................................................*/	public String getName() {		return "Chromaseq Package";	}	/*.................................................................................................................*/	/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/	public String getPackageName(){		return "Chromaseq Package";	}	/*.................................................................................................................*/	/** Returns citation for a package of modules*/	public String getPackageCitation(){		return "Maddison, D.R., & W.P. Maddison.  2010.  Chromaseq.  A package of modules for processing chromatograms and sequence data in Mesquite. Version 0.975.";	}	/*.................................................................................................................*/	/** Returns version for a package of modules*/	public String getPackageVersion(){		return "0.975";	}	/*.................................................................................................................*/	/** Returns version for a package of modules as an integer*/	public int getPackageVersionInt(){		return 975;	}	/*.................................................................................................................*/	/** Returns build number for a package of modules as an integer*/	public int getPackageBuildNumber(){		return 3;	}	/*.................................................................................................................*/	public String getPackageURL(){		return "http://mesquiteproject.org/packages/chromaseq";  	}	public String getPackageDateReleased(){		return "1 February 2010";	}	/*.................................................................................................................*/	/** Returns whether there is a splash banner*/	public boolean hasSplash(){		return true; 	}	/*.................................................................................................................*/	public int getVersionOfFirstRelease(){		return NEXTRELEASE;  	}}