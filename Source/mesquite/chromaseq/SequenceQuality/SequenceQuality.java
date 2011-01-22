/* Mesquite Chromaseq source code.  Copyright 2005-2010 David Maddison and Wayne Maddison.
Version 0.980   July 2010
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */

package mesquite.chromaseq.SequenceQuality;

import mesquite.categ.lib.MolecularState;
import mesquite.chromaseq.lib.*;
import mesquite.cont.lib.ContinuousData;
import mesquite.lib.Debugg;
import mesquite.lib.EmployeeNeed;
import mesquite.lib.MesquiteDouble;
import mesquite.lib.MesquiteModule;
import mesquite.lib.MesquiteNumber;
import mesquite.lib.MesquiteString;
import mesquite.lib.Notification;
import mesquite.lib.Taxa;
import mesquite.lib.Taxon;
import mesquite.lib.characters.CharacterData;
import mesquite.lib.characters.MCharactersDistribution;
import mesquite.lib.duties.MatrixSourceCoord;
import mesquite.lib.duties.NumberForTaxon;

	public class SequenceQuality extends NumberForTaxon {
		public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
			EmployeeNeed e = registerEmployeeNeed(MatrixSourceCoord.class, getName() + "  needs a source of sequences.",
			"The source of characters is arranged initially");
		}
		MatrixSourceCoord matrixSourceTask;
		Taxa currentTaxa = null;
		MCharactersDistribution observedStates =null;
		ContinuousData qualityData;
		CharacterData data;


		/*.................................................................................................................*/
		public boolean startJob(String arguments, Object condition, boolean hiredByName) {
			matrixSourceTask = (MatrixSourceCoord)hireCompatibleEmployee(MatrixSourceCoord.class, MolecularState.class, "Source of character matrix (for " + getName() + ")"); 
			if (matrixSourceTask==null)
				return sorry(getName() + " couldn't start because no source of character matrices was obtained.");
			return true;
		}

		/*.................................................................................................................*/
		/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */
		public void employeeQuit(MesquiteModule employee) {
			if (employee == matrixSourceTask)  // character source quit and none rehired automatically
				iQuit();
		}
		/*.................................................................................................................*/
		public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification) {
			observedStates = null;
			super.employeeParametersChanged(employee, source, notification);
		}
		/*.................................................................................................................*/
		/** returns whether this module is requesting to appear as a primary choice */
		public boolean requestPrimaryChoice(){
			return true;  
		}

		/** Called to provoke any necessary initialization.  This helps prevent the module's initialization queries to the user from
	   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/
		public void initialize(Taxa taxa){
			currentTaxa = taxa;
			matrixSourceTask.initialize(currentTaxa);
			observedStates = matrixSourceTask.getCurrentMatrix(taxa);
			data = observedStates.getParentData();
			
			qualityData = ChromaseqUtil.getQualityData(data);

		}
		
	   	int count = 0;
	   	double summ = 0;
		/*.................................................................................................................*/
		private double getQualityTaxon(int it){
			if (data == null || qualityData == null)
				return 0;
			double sum = 0;
			int num = 0;
			int numChars = data.getNumChars(false);
			for (int ic = 0; ic<numChars; ic++){
				if (!data.isInapplicable(ic, it) && !data.isUnassigned(ic, it)) {
					double d = ChromaseqUtil.getQualityScoreForEditedMatrixBase(data,ic, it);
					if (MesquiteDouble.isCombinable(d) && d>=0 && d<=100){
						sum += d;
						num++;
					}
				}
			}
			if (num == 0)
				return 0;
			return sum*1.0/num;
			/*
	   		Object obj = data.getCellObject(qualityNameRef, ic, it);//IF USED use  ChromaseqUtil.getIntegerCellObject
	   		if (obj instanceof MesquiteInteger)
	   			return ((MesquiteInteger)obj).getValue();
	   		return 0;
			 */
		}
		/*.................................................................................................................*
   	private double getQualityTaxon2(int it){
	   		if (data == null)
	   			return 0;
	   		if (qualityData == null)
	   			return 0;
	    		double sum = 0;
	   		for (int ic = 0; ic<qualityData.getNumChars(false); ic++){
	   			if (!data.isUnassigned(ic, it) && !qualityData.isInapplicable(ic, it)) {
	   				double d = ChromaseqUtil.getQualityScoreForEditedMatrixBase(data,ic, it);
	   				if (ic==0)
	   					Debugg.println("" + it + ": " + d);
	   				if (d>101 || MesquiteDouble.isCombinable(d))
	   					;
	   				else if (d>=90.0)
	   					sum += 1;
	   				else if (d>=80.0)
	   					sum += 0.9;
	   				else if (d>=70.0)
	   					sum += 0.7;
	   				else if (d>=60.0)
	   					sum += 0.5;
	   				else if (d>=50.0)
	   					sum += 0.4;
	   				else if (d>=40.0)
	   					sum += 0.2;
	   				else
	   					sum += 1/(100.0 - d); //count good states more!
	   					//sum += 1/((100.0 - d)*(100.0 - d)); //count good states more!
	   			}
	   		}
	   		summ = sum;
	   		return sum*50/qualityData.getNumChars(false);
	   	}
	/*.................................................................................................................*/


		public void calculateNumber(Taxon taxon, MesquiteNumber result, MesquiteString resultString){
			if (result==null)
				return;
			result.setToUnassigned();
			clearResultAndLastResult(result);
			Taxa taxa = taxon.getTaxa();
			int it = taxa.whichTaxonNumber(taxon);
			if (taxa != currentTaxa || observedStates == null ) {
				observedStates = matrixSourceTask.getCurrentMatrix(taxa);
				currentTaxa = taxa;
			}
			if (observedStates==null)
				return;
			data = observedStates.getParentData();
			qualityData = ChromaseqUtil.getQualityData(data);

			double qualityScore = getQualityTaxon(it);

			result.setValue(qualityScore);
		
		
			if (resultString!=null)
				resultString.setValue("Quality of sequence in matrix "+ observedStates.getName()  + ": " + result.toString());
			saveLastResult(result);
			saveLastResultString(resultString);
		}
		/*.................................................................................................................*/
		public String getName() {
			if (currentTaxa != null && observedStates == null)
				observedStates = matrixSourceTask.getCurrentMatrix(currentTaxa);
			if (observedStates != null && getProject().getNumberCharMatrices()>1){
				CharacterData d = observedStates.getParentData();
				if (d != null && d.getName()!= null) {
					String n =  d.getName();
					if (n.length()>12)
						n = n.substring(0, 12); 
					return "Qual.Score (" + n + ")";
				}
			}
			return "Quality Score";
		}

		/*.................................................................................................................*/
		public boolean isPrerelease() {
			return false;
		}
		public String getParameters() {
			return "Sequence quality in matrix from: " + matrixSourceTask.getParameters();
		}
		/*.................................................................................................................*/

		/** returns an explanation of what the module does.*/
		public String getExplanation() {
			return "Reports a measure of sequence quality as judged by Phred/Phrap scores, for a molecular sequence in a taxon." ;
		}

	}



