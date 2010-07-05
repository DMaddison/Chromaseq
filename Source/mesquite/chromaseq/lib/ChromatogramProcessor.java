/* Mesquite chromaseq source code.  Copyright 2005-2009 D. Maddison and W. Maddison.Version 2.7, August 2009.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.lib; import mesquite.lib.*;import mesquite.molec.lib.*;/* ======================================================================== *//* This duty classes processes the chromatograms presented to it by a ChromatogramSource */public abstract class  ChromatogramProcessor extends MesquiteModule {	protected DNADatabaseURLSource databaseURLSource = null;   	 public Class getDutyClass() {   	 	return ChromatogramProcessor.class;   	 } 	public String getDutyName() { 		return "Chromatogram Processor";   	 } 		public void checkDatabaseSource() {		if (databaseURLSource==null)			databaseURLSource= (DNADatabaseURLSource)hireEmployee(DNADatabaseURLSource.class, "Source of Database Connectivity");	} 	public DNADatabaseURLSource getDatabaseURLSource() { 		checkDatabaseSource(); 		return databaseURLSource; 	}	 public abstract boolean processChromatograms(MesquiteProject project, boolean appendIfPossible);	 public abstract boolean processChromatograms(MesquiteProject project, boolean appendIfPossible,  String outputDirectory);	   	 }