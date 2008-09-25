/* Mesquite chromaseq source code.  Copyright 2005-2008 D. Maddison and W. Maddison.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
*/
package mesquite.chromaseq.PrimersListAssistant;

	import mesquite.lists.lib.*;

	import mesquite.lib.*;
	import mesquite.lib.table.*;

		/* ======================================================================== */
		public class PrimersListAssistant extends TaxonListAssistant {
			Taxa taxa;
			MesquiteTable table=null;
			NameReference anr = NameReference.getNameReference("ToLLeaves");
			/*.................................................................................................................*/
			public boolean startJob(String arguments, Object condition, boolean hiredByName) {
				return true;
		  	 }
		 
			/*.................................................................................................................*/
			public void setTableAndTaxa(MesquiteTable table, Taxa taxa){
				if (this.taxa != null)
					this.taxa.removeListener(this);
				this.taxa = taxa;
				if (this.taxa != null)
					this.taxa.addListener(this);
				this.table = table;
			}
			public void changed(Object caller, Object obj, Notification notification){
				outputInvalid();
				parametersChanged(notification);
			}
			public String getTitle() {
				return "ToL Leaf";
			}
			public String getStringForTaxon(int ic){

				if (taxa!=null) {
					MesquiteBoolean n = (MesquiteBoolean)taxa.getAssociatedObject(anr, ic);
					if (n !=null)
						if (n.getValue())
							return "leaf";
						else
							return "branch";

				}
				return "-";
			}
			/*...............................................................................................................*/
			/** returns whether or not a cell of table is editable.*/
			public boolean isCellEditable(int row){
				return false;
			}
			/*...............................................................................................................*/
			/** for those permitting editing, indicates user has edited to incoming string.*/
			public void setString(int row, String s){
				if (taxa!=null) {
					taxa.setAssociatedObject(anr, row, s);
				}
				
			}
			public boolean useString(int ic){
				return true;
			}
			
			public String getWidestString(){
				return "88888888888888888  ";
			}
			/*.................................................................................................................*/
		    	 public String getName() {
				return "ToL Leaf";
		   	 }
			/*.................................................................................................................*/
		   	public boolean isPrerelease(){
		   		return true;  
		   	}
			/*.................................................................................................................*/
			/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer
			 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.
			 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/
			public int getVersionOfFirstRelease(){
				return NEXTRELEASE;  
			}
			/*.................................................................................................................*/
			/** returns whether this module is requesting to appear as a primary choice */
		   	public boolean requestPrimaryChoice(){
		   		return true;  
		   	}
		   	 
			/*.................................................................................................................*/
		 	/** returns an explanation of what the module does.*/
		 	public String getExplanation() {
		 		return "Lists whether a taxon is a leaf in the Tree of Life Web Project." ;
		   	 }
		}
