/* Mesquite.chromaseq source code.  Copyright 2005 D. Maddison, W. Maddison. Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.ColorDirection; import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;import mesquite.chromaseq.lib.*;/** ======================================================================== */public class ColorDirection extends DataWindowAssistantI implements CellColorer {	MolecularData data;	MesquiteTable table;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName){		return true;	}    	 public void viewChanged(CommandRecord commandRec){   	 }    	 public boolean setActiveColors(boolean active, CommandRecord commandRec){    			return true;  	 }	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return false;   	 }   	  	public void setTableAndData(MesquiteTable table, CharacterData data, CommandRecord commandRec){		this.table = table;		if (data instanceof MolecularData)			this.data = (MolecularData)data;	}	/*.................................................................................................................*/   	public boolean hasDisplayModifications(){   		return false;   	}     	public String getColorsExplanation(CommandRecord commandRec){   		return null;   	}    	public String getCellString(int ic, int it){		if (it<0 || ic<0 ||  !isActive())			return null;    		return "";   	}   	ColorRecord[] legend;   	public ColorRecord[] getLegendColors(CommandRecord commandRec){   		if (legend == null) {   			legend = new ColorRecord[2];	   		legend[0] = new ColorRecord(Color.green, "Forward");	  		legend[1] = new ColorRecord(Color.red, "Reverse");  		}   		return legend;   	}	/*.................................................................................................................*/   	public Color getCellColor(int ic, int it){   		if (it < 0 || ic < 0)    			return Color.white;   		if (data.isReverseDirectionAtSite(ic, it))   			return ColorDistribution.lightRed;   		else    			return ColorDistribution.lightGreen;    	}	/*.................................................................................................................*/	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */	public CompatibilityTest getCompatibilityTest(){		return new DNAStateOnlyTest();	}	/*.................................................................................................................*/	/*.................................................................................................................*/    	 public String getName() {		return "Direction of Sequence";   	 }	/*.................................................................................................................*/  	 public String getVersion() {		return null;   	 }   	 	/*.................................................................................................................*/  	 public String getExplanation() {		return "Colors cells by the direction of the molecular sequence.";   	 }}