/* Mesquite.cartographer source code.  Copyright 2005 D. Maddison, W. Maddison. Version 1.0, April 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.aChromaseqIntro;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class aChromaseqIntro extends PackageIntro {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) { 		return true;  	 }  	 public Class getDutyClass(){  	 	return aChromaseqIntro.class;  	 } 	/*.................................................................................................................*/	 public String getExplanation() {	return "Chromaseq is a package of Mesquite modules providing tools for processing and displaying chromatogram and sequence data.";	 }   	/*.................................................................................................................*/    	 public String getName() {		return "Chromaseq Package";   	 }	/*.................................................................................................................*/	/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/ 	public String getPackageName(){ 		return "Chromaseq Package"; 	}	/*.................................................................................................................*/	/** Returns citation for a package of modules*/ 	public String getPackageCitation(){ 		return "Maddison, D.R., & W.P. Maddison.  2007.  Chromaseq.  A package of modules for Mesquite. Version 0.91."; 	}	/*.................................................................................................................*/	/** Returns whether there is a splash banner*/	public boolean hasSplash(){ 		return true; 	}}