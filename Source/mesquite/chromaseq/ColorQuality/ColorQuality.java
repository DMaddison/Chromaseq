/* Mesquite chromaseq source code.  Copyright 2005-2009 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.ColorQuality; import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;import mesquite.chromaseq.lib.*;import mesquite.cont.lib.*;//make display of ambiguities optional!!!!!/** ======================================================================== */public class ColorQuality extends DataWindowAssistantI implements CellColorer, CellColorerTaxa, CellColorerMatrix {	CharacterData data;	MesquiteTable table;	ContinuousData linkedData;	ChromaseqBaseMapper chromMapper;	TableTool trimmableTool, restoreTool, touchedTool, pleaseCheckTool; 	MesquiteBoolean showAmbiguities;	MesquiteBoolean showMarks;	MesquiteMenuItemSpec ambM, markM;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		if (containerOfModule() instanceof MesquiteWindow) {			trimmableTool = new TableTool(this, "markAsTrimmable", getPath(), "thumbsDown.gif", 5,13,"Mark as trimmable", "This tool marks selected cells as trimmable.", MesquiteModule.makeCommand("setTrimmable",  this) , null, null);			restoreTool = new TableTool(this, "restore", getPath(), "thumbsUp.gif", 5,1,"Mark as not trimmable", "This tool marks selected cells as not trimmable.", MesquiteModule.makeCommand("restore",  this) , null, null);			touchedTool = new TableTool(this, "touch", getPath(), "touched.gif", 1,8,"Mark as changed", "This tool marks selected cells as changed by hand.", MesquiteModule.makeCommand("touch",  this) , null, null);			pleaseCheckTool = new TableTool(this, "check", getPath(), "check.gif", 1,1,"Mark as to be checked", "This tool marks selected cells as to be checked.", MesquiteModule.makeCommand("check",  this) , null, null);		}		showAmbiguities = new MesquiteBoolean(true);		showMarks = new MesquiteBoolean(true);		return true;	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("toggleAmbiguities " + showAmbiguities.toOffOnString());  	 	temp.addLine("toggleMarks " + showMarks.toOffOnString());  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	if (checker.compare(this.getClass(),  "Marks selected cells as trimmable", "[column touched][row touched]", commandName, "setTrimmable")) {  	 		mark(arguments, 1);   	 	}    	 	else if (checker.compare(this.getClass(),  "Marks selected cells as not trimmable", "[column touched][row touched]", commandName, "restore")) {  	 		mark(arguments, 0);   	 	}    	 	else if (checker.compare(this.getClass(),  "Marks selected cells as touched", "[column touched][row touched]", commandName, "touch")) {  	 		mark(arguments, 2);   	 	}    	 	else if (checker.compare(this.getClass(),  "Marks selected cells as to be checked", "[column touched][row touched]", commandName, "check")) {  	 		mark(arguments, 3);   	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether ambiguities are shown", "[on; off]", commandName, "toggleAmbiguities")) {    	 		showAmbiguities.toggleValue(parser.getFirstToken(arguments));			if (table != null)				table.repaintAll();			parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether marks are shown", "[on; off]", commandName, "toggleMarks")) {    	 		showMarks.toggleValue(parser.getFirstToken(arguments));			if (table != null)				table.repaintAll();			parametersChanged();    	 	}		else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }   	 void mark(String arguments, int i){  	 		if (table!=null && data !=null){	   	 		MesquiteInteger io = new MesquiteInteger(0);	   			int firstColumnTouched= MesquiteInteger.fromString(arguments, io);	   			int firstRowTouched= MesquiteInteger.fromString(arguments, io);	   			if (!table.rowLegal(firstRowTouched)|| !table.columnLegal(firstColumnTouched))	   				return;	   			if ((table.isCellSelected(firstColumnTouched, firstRowTouched))||(table.isRowSelected(firstRowTouched))||(table.isColumnSelected(firstColumnTouched))) {	   					markSelectedCells(table, data, firstColumnTouched, firstRowTouched, i); // the touched cell or column is selected; therefore, just fill the selection.	   			}	   			else {	   				table.deselectAll();	   				table.selectCell(firstColumnTouched, firstRowTouched);	   				markSelectedCells(table, data, firstColumnTouched, firstRowTouched, i); // the touched cell or column is selected; therefore, just fill the selection.	   			}	   		}   	 }	/*.................................................................................................................*/	public void markSelectedCells(MesquiteTable table, CharacterData data, int firstColumnTouched, int firstRowTouched, int flag) {			boolean success = false;			if (table.anyCellSelected()) {	   			if (table.isCellSelected(firstColumnTouched, firstRowTouched)) {					for (int i=0; i<table.getNumColumns(); i++)						for (int j=0; j<table.getNumRows(); j++)							if (table.isCellSelected(i,j)) {								setFlag(i, j, flag);							}					success = true;				}			}			if (table.anyRowSelected()) {	   			if (table.isRowSelected(firstRowTouched)) {					for (int j=0; j<table.getNumRows(); j++) {						if (table.isRowSelected(j))							for (int i=0; i<table.getNumColumns(); i++)								setFlag(i, j, flag);					}					success = true;				}			}			if (table.anyColumnSelected()) {	   			if (table.isColumnSelected(firstColumnTouched)) {					for (int i=0; i<table.getNumColumns(); i++){						if (table.isColumnSelected(i))							for (int j=0; j<table.getNumRows(); j++) 								setFlag(i, j, flag);					}					success = true;				}			}			if (success){ 	   			table.repaintAll();				data.notifyListeners(this, new Notification(MesquiteListener.ANNOTATION_CHANGED));			}			}   	private void setFlag(int ic, int it, int c){   		if (data == null)   			return;		if (ic<0 && it<0){		}		else if (ic<0) { //taxon			Associable tInfo = data.getTaxaInfo(true);						ChromaseqUtil.setLongAssociated(tInfo, ChromaseqUtil.trimmableNameRef, it, c); 		}		else if (it < 0){ //character			ChromaseqUtil.setLongAssociated(data, ChromaseqUtil.trimmableNameRef, ic, c);		}		else if (!MesquiteInteger.isCombinable(c) || c<0){			ChromaseqUtil.setIntegerCellObject(data,ChromaseqUtil.trimmableNameRef, ic, it, null);		}		else {			MesquiteInteger ms = new MesquiteInteger(c);			ChromaseqUtil.setIntegerCellObject(data,ChromaseqUtil.trimmableNameRef, ic, it, ms);		}		table.redrawCell(ic,it);   	}   	private int getFlag(int ic, int it){   		if (data == null)   			return 0;		if (ic<0){  //taxon			Associable tInfo = data.getTaxaInfo(false);			if (tInfo == null)					return 0;			long c = ChromaseqUtil.getLongAssociated(tInfo,ChromaseqUtil.trimmableNameRef, it);			if (MesquiteLong.isCombinable(c))				return (int)c;		}		else if (it<0){ //character			long c = ChromaseqUtil.getLongAssociated(data,ChromaseqUtil.trimmableNameRef, ic);			if (MesquiteLong.isCombinable(c))				return (int)c;		}		else {			return ChromaseqUtil.getIntegerCellObject(data, ChromaseqUtil.trimmableNameRef, ic, it);		}   		return MesquiteInteger.unassigned;   	}    	 public void viewChanged(){   	 }   	 boolean wasActive = false;  	 public boolean setActiveColors(boolean active){		if ( trimmableTool != null){			if (wasActive && !active){				((MesquiteWindow)containerOfModule()).removeTool(trimmableTool);				((MesquiteWindow)containerOfModule()).removeTool(restoreTool);				((MesquiteWindow)containerOfModule()).removeTool(touchedTool);				((MesquiteWindow)containerOfModule()).removeTool(pleaseCheckTool);				deleteMenuItem(ambM);				deleteMenuItem(markM);				resetContainingMenuBar();			}			else if (!wasActive && active){				((MesquiteWindow)containerOfModule()).addTool(trimmableTool);				((MesquiteWindow)containerOfModule()).addTool(restoreTool);				((MesquiteWindow)containerOfModule()).addTool(touchedTool);				((MesquiteWindow)containerOfModule()).addTool(pleaseCheckTool);				ambM = addCheckMenuItem(null, "Show Ambiguities", makeCommand("toggleAmbiguities", this), showAmbiguities);				markM = addCheckMenuItem(null, "Show Marked Cells", makeCommand("toggleMarks", this), showMarks);				resetContainingMenuBar();			}		}		wasActive = active;		return true;   	 }	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return false;   	 }   	    	Associable tInfoData;	public void setTableAndData(MesquiteTable table, CharacterData data){		this.table = table;		this.data = data;		if (data != null)			tInfoData = data.getTaxaInfo(false);		Vector links = data.getDataLinkages();		linkedData = ChromaseqUtil.getQualityData(data);		chromMapper = new ChromaseqBaseMapper(data);	}	/*.................................................................................................................*/   	public boolean hasDisplayModifications(){   		return false;   	}   	private int getQuality(int ic, int it){   		if (data == null)   			return 0;   		if (linkedData == null)    			return 0;   		return (int)(chromMapper.getQualityScore(ic, it) + 0.01);   		/*   		Object obj = data.getCellObject(qualityNameRef, ic, it); //IF USED use  ChromaseqUtil.getIntegerCellObject   		if (obj instanceof MesquiteInteger)   			return ((MesquiteInteger)obj).getValue();   		return 0;   		*/   	}   	   	private boolean hasChromatograms(int it){   		long num = 0;			if (tInfoData != null)				num = ChromaseqUtil.getLongAssociated(tInfoData,ChromaseqUtil.chromatogramsExistRef, it);			if (num != 0 && MesquiteLong.isCombinable(num))				return num >0L;		return false; 	}   	int count = 0;   	double summ = 0;   	private double getQualityTaxon(int it){   		if (data == null)   			return 0;   		if (linkedData == null)   			return 0;    		double sum = 0;   		for (int ic = 0; ic<linkedData.getNumChars(false); ic++){   			if (!data.isUnassigned(ic, it) && !linkedData.isUnassigned(ic, it)) {   				double d = chromMapper.getQualityScore(ic, it);   				if (d>101)   					;   				else if (d>=90.0)   					sum += 1;   				else if (d>=80.0)   					sum += 0.9;   				else if (d>=70.0)   					sum += 0.7;   				else if (d>=60.0)   					sum += 0.5;   				else if (d>=50.0)   					sum += 0.4;   				else if (d>=40.0)   					sum += 0.2;   				else   					sum += 1/(100.0 - d); //count good states more!   					//sum += 1/((100.0 - d)*(100.0 - d)); //count good states more!   			}   		}   		summ = sum;   		return sum*50/linkedData.getNumChars(false);   		/*   		Object obj = data.getCellObject(qualityNameRef, ic, it);//IF USED use  ChromaseqUtil.getIntegerCellObject   		if (obj instanceof MesquiteInteger)   			return ((MesquiteInteger)obj).getValue();   		return 0;   		*/   	}   	public String getColorsExplanation(){   		return null;   	}    	public String getCellString(int ic, int it){		if (it<0 || ic<0 ||  !isActive())			return null;    		String expl = "";   		if (ic<0)  {   			//taxon; 			double seqQual = getQualityTaxon(it);   			expl += " Quality of sequence " + seqQual;   			   		}    		else {	    		int color = getFlag(ic, it);	   		if (showMarks.getValue()){		   		if (color == 1) //trimmable		   			expl += " Trimmable.";		   		else if (color == 2) //touched		   			expl += " Modified by hand.";		   		else if (color == 3) //please check		   			expl += " Please recheck!";	   		}	   		if (data.isInapplicable(ic, it)){	   			if (hasChromatograms(it))	   				expl += " No data but chromatogram available";	   			else	   				expl += " Gap or beyond end of sequences";	   		}	   		else {		  		int quality = getQuality(ic, it);		   		if (showAmbiguities.getValue()) {		   			long st = ((CategoricalData)data).getState(ic, it);		   			if (CategoricalState.isUnassigned(st) || CategoricalState.hasMultipleStates(st))		   				expl += " Ambiguous coding";		   			else		   				expl += "Quality of call: " + MesquiteInteger.toString(quality);		   		}		   				   		else 		   			expl += "Quality of call: " + MesquiteInteger.toString(quality);	   		}					}		//count sequence		int sLength = 0;		for (int icc = 0; icc<data.getNumChars(false); icc++)			if (!data.isInapplicable(icc, it))				sLength++;		//count trimmable		int tLength = 0;		for (int icc = 0; icc<data.getNumChars(false); icc++)			if (getFlag(icc, it) == 1)				tLength++;		return expl + " (Sequence length " + sLength + "; trimmable " + tLength + ")";   	}   	ColorRecord[] legend;   	public ColorRecord[] getLegendColors(){   		if (legend == null) {   			legend = new ColorRecord[10];	   		legend[0] = AceFile.colorHighQuality;	  		legend[1] = AceFile.colorMediumQuality;	  		legend[2] = AceFile.colorLowQuality;	  		legend[3] = AceFile.colorInapplicable;	  		legend[4] = AceFile.colorChromNoSeq;	  		legend[5] = AceFile.colorAmbiguous;	  		legend[6] = AceFile.colorTrimmable;	  		legend[7] = AceFile.colorTouched;	  		legend[8] = AceFile.colorPleaseRecheck;	  		legend[9] = AceFile.colorNoQuality;  		}   		return legend;   	}	/*.................................................................................................................*/   	public Color getCellColor(int ic, int it){   	   		if (it < 0)    			return Color.white;   		if (ic<0)  {   			//taxon; 			double seqQual = getQualityTaxon(it);   			return MesquiteColorTable.getDefaultColor(50, (int)(50-seqQual), MesquiteColorTable.GRAYSCALE);   		}    		int color = getFlag(ic, it);   		if (showMarks.getValue()){	   		if (color == 1) //trimmable	   			return AceFile.colorTrimmable.getColor();	   		else if (color == 2) //touched	   			return AceFile.colorTouched.getColor();	   		else if (color == 3) //please check	   			return AceFile.colorPleaseRecheck.getColor();   		}   		if (data.isInapplicable(ic, it)){    			if (hasChromatograms(it))   				return AceFile.colorChromNoSeq.getColor();  			return AceFile.colorInapplicable.getColor();   		}   		if (showAmbiguities.getValue()) {   			long st = ((CategoricalData)data).getState(ic, it);   			if (CategoricalState.isUnassigned(st) || CategoricalState.hasMultipleStates(st))   				return AceFile.colorAmbiguous.getColor();   		}  		int quality = getQuality(ic, it);   		   		if (!(data instanceof DNAData))   			return MesquiteColorTable.getDefaultColor(50, (100-quality)/2, MesquiteColorTable.GRAYSCALE);   		/*else if (CategoricalState.isLowerCase(((DNAData)data).getStateRaw(ic, it)) || data.isUnassigned(ic, it))     			return MesquiteColorTable.getDefaultColor(50, (100-quality)/2, MesquiteColorTable.BLUESCALE);   		else    			return MesquiteColorTable.getDefaultColor(50, (100-quality)/2, MesquiteColorTable.GREENSCALE);*/    		else     			return AceFile.getColorOfQuality(quality);  	}	/*.................................................................................................................*/	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */	public CompatibilityTest getCompatibilityTest(){		return new RequiresAnyDNAData();	}	/*.................................................................................................................*/	/*.................................................................................................................*/    	 public String getName() {		return "Quality from Phred/Phrap";   	 }	/*.................................................................................................................*/  	 public String getVersion() {		return null;   	 }   	 	/*.................................................................................................................*/  	 public String getExplanation() {		return "Colors cells by their quality scores from phred and phrap.";   	 }}