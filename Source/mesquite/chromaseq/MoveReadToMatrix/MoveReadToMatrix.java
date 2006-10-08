/* Mesquite chromaseq source code.  Copyright 2005-2006 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.MoveReadToMatrix; import java.awt.event.KeyEvent;import java.util.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.categ.lib.*;import mesquite.cont.lib.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.chromaseq.lib.*;/* ======================================================================== */public class MoveReadToMatrix extends ChromInit {	CMTable table;	Taxon taxon;	Contig contig;	Read[] reads;	DNAData matrixData, originalData;	ContinuousData qualityData;	ContigDisplay contigPanel;	SequencePanel[] sequences;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {  		return true;	}		public void setWindow(MesquiteWindow w){		ChromatogramTool moveToMatrixTool = new ChromatogramTool(this, "moveToMatrix", getPath(),"moveToMatrix.gif", 4,2,"Move to Matrix", "Move To Matrix tool", MesquiteModule.makeCommand("moveToMatrix",  this) , null,null);		moveToMatrixTool.setWorksOnAllPanels(false);		moveToMatrixTool.setWorksOnChromatogramPanels(true);		moveToMatrixTool.setWorksOnlyOnSelection(true);		w.addTool(moveToMatrixTool);		if (w instanceof ChromatWindow)			contigPanel = (ContigDisplay)((ChromatWindow)w).getMainContigPanel();	}   	 public void setContext(Taxon taxon, Contig contig, Read[] reads, SequencePanel[] sequences, DNAData matrixData, DNAData originalData, ContinuousData qualityData, MesquiteTable table){   	 	this.table = (CMTable)table;   	 	this.taxon = taxon;   	 	this.matrixData = matrixData;   	 	this.originalData = originalData;   	 	this.qualityData = qualityData;   	 	this.contig = contig;		this.reads = reads;		this.sequences = sequences;   	 }   	/*.................................................................................................................*/	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {	 	if (checker.compare(this.getClass(), "Move sequence in selected sites of panel to matrix", "[site][true = chromatogram panel][whichPanel][modifiers]", commandName, "moveToMatrix")) {	 		MesquiteInteger pos = new MesquiteInteger(0);	 		int site= MesquiteInteger.fromFirstToken(arguments, pos);		 	int localSite= MesquiteInteger.fromString(arguments, pos);	 		String s = ParseUtil.getToken(arguments, pos);	 	 	int whichPanel= MesquiteInteger.fromString(arguments, pos);	Debugg.println("arguments " + arguments + " whichPanel " + whichPanel);	 		if ("true".equalsIgnoreCase(s)){ //chromatogram	 	 		selectedFromReadToMatrix(whichPanel);	 		}	 		else {	 	 		selectedFromSequencePanelToMatrix(whichPanel);	 		}	 	}		else	 		return super.doCommand(commandName, arguments, commandRec, checker);	 	return null;	 }	 	 /** Returns whether consensus position ic is within read */	 boolean isPositionInRead(int ic, Read read){		 	if (read ==null)		 			return false;		 	int readBase= read.getReadBaseFromConsensusBase(ic);		 	return (readBase>=0 && readBase<read.getBasesLength());	 }	 /** Returns the state in the read at consensus position ic */	 long getReadStateAtConsensusPosition(int ic, Read read){		 if (read ==null)			 return DNAState.unassigned;		 int readBase= read.getReadBaseFromConsensusBase(ic);		 char readChar = read.getPhdBaseChar(readBase);Debugg.println("    readChar: " + readChar);		 return DNAState.fromCharStatic(readChar);	 }		 /** Returns whether consensus position ic is within read */	 boolean isPositionInSequence(int ic, SequencePanel panel, String sequence){		 if (sequence ==null)			 return false;		 int local = panel.getCanvas().getLocalIndexFromConsensus(ic);		 if (local>=0 && local < sequence.length()){			 return true;		 }		 return false;	 }	 long getSequenceStateAtConsensusPosition(int ic, SequencePanel panel, String sequence){		 if (sequence ==null)			 return DNAState.unassigned;		 int local = panel.getCanvas().getLocalIndexFromConsensus(ic);		 if (local>=0 && local < sequence.length()){			 char s = sequence.charAt(local);			 return DNAState.fromCharStatic(s);		 }		 return DNAState.unassigned;	 }	 /*--------------------------------------*/	 private void selectedFromReadToMatrix(int whichRead){		 if (taxon == null || contigPanel == null)						return;				//if needed expand matrix				//first, find min and max of selected				int it = taxon.getNumber();				int minSelected = -1;				int maxSelected = -1;				int selectOffset = 0;				boolean[] selected = new boolean[contigPanel.getTotalNumPeaks()];				for (int i=0; i<selected.length; i++)						selected[i]=contigPanel.getSelectedOverallBase(i);								for (int ic = 0; ic< contigPanel.getTotalNumPeaks(); ic++){					int consensusBase = contigPanel.getConsensusBaseFromOverallBase(ic);					if (selected[ic] && (isPositionInRead(consensusBase, reads[whichRead]))){ //selected, therefore move						if (minSelected == -1)								minSelected = consensusBase;						maxSelected = consensusBase;					}				}				/* to test, short circuit the above and use this just to expand the matrix on both sides*				maxSelected = contigPanel.getTotalNumPeaks();				minSelected = 0;				/**/								//second, find out if this is beyond matrix to see if need to expand matrix				int minSelectedInMatrix = contigPanel.getMatrixPositionOfConsensusPosition(minSelected, originalData); //this should return accurate -ve number or +ve number if byeond edge!				int maxSelectedInMatrix =  contigPanel.getMatrixPositionOfConsensusPosition(maxSelected, originalData);				boolean added = false;				int origNumChars = matrixData.getNumChars();				if (maxSelectedInMatrix>= origNumChars){ //need to extend at right					matrixData.addCharacters(origNumChars, maxSelectedInMatrix-origNumChars+1, false);					matrixData.addInLinked(origNumChars, maxSelectedInMatrix-origNumChars+1, false);					added = true;				}				if (minSelectedInMatrix<0){  //need to extend at left					matrixData.addCharacters(-1, -minSelectedInMatrix, false);					matrixData.addInLinked(-1, -minSelectedInMatrix, false);					added = true;					selectOffset = -minSelectedInMatrix;				}															for (int ic = 0; ic< contigPanel.getTotalNumPeaks(); ic++){					if (selected[ic]){ //selected, therefore move						int consensusBase = contigPanel.getConsensusBaseFromOverallBase(ic);						int matrixPosition = contigPanel.getMatrixPositionOfConsensusPosition(consensusBase, originalData);Debugg.println("  selected peak: " + ic + ", consensusBase: " + consensusBase + ", matrixPosition: " + matrixPosition);						matrixData.setState(matrixPosition+selectOffset, it, getReadStateAtConsensusPosition(consensusBase, reads[whichRead]));  					}				}							if (added){					matrixData.notifyListeners(this, new Notification(CharacterData.PARTS_ADDED, null));					originalData.notifyListeners(this, new Notification(CharacterData.PARTS_ADDED, null));					qualityData.notifyListeners(this, new Notification(CharacterData.PARTS_ADDED, null));				}				else {					matrixData.notifyListeners(this, new Notification(CharacterData.DATA_CHANGED, null));				}				contigPanel.repaintPanels();						} 	 /*--------------------------------------*/	 private void selectedFromSequencePanelToMatrix(int whichPanel){		 if (taxon == null || contigPanel == null)						return;				//if needed expand matrix				//first, find min and max of selected				int it = taxon.getNumber();				int minSelected = -1;				int maxSelected = -1;				int selectOffset = 0;				boolean[] selected = new boolean[contigPanel.getTotalNumPeaks()];				for (int i=0; i<selected.length; i++)						selected[i]=contigPanel.getSelectedOverallBase(i);				String sequence = sequences[whichPanel].getSequenceString();								for (int ic = 0; ic< contigPanel.getTotalNumPeaks(); ic++){					int consensusBase = contigPanel.getConsensusBaseFromOverallBase(ic);					if (selected[ic] && (isPositionInSequence(consensusBase, sequences[whichPanel], sequence))){ //selected, therefore move						if (minSelected == -1)								minSelected = consensusBase;						maxSelected = consensusBase;					}				}Debugg.println("minSelected " + minSelected + " maxSelected " + maxSelected);/* to test, short circuit the above and use this just to expand the matrix on both sides*				maxSelected = contigPanel.getTotalNumPeaks();				minSelected = 0;				/**/								//second, find out if this is beyond matrix to see if need to expand matrix				int minSelectedInMatrix = contigPanel.getMatrixPositionOfConsensusPosition(minSelected, originalData); //this should return accurate -ve number or +ve number if byeond edge!				int maxSelectedInMatrix =  contigPanel.getMatrixPositionOfConsensusPosition(maxSelected, originalData);Debugg.println("minSelectedInMatrix " + minSelectedInMatrix + " maxSelectedInMatrix " + maxSelectedInMatrix);				boolean added = false;				int origNumChars = matrixData.getNumChars();				if (maxSelectedInMatrix>= origNumChars){ //need to extend at right					matrixData.addCharacters(origNumChars, maxSelectedInMatrix-origNumChars+1, false);					matrixData.addInLinked(origNumChars, maxSelectedInMatrix-origNumChars+1, false);					added = true;				}				if (minSelectedInMatrix<0){  //need to extend at left					matrixData.addCharacters(-1, -minSelectedInMatrix, false);					matrixData.addInLinked(-1, -minSelectedInMatrix, false);					added = true;					selectOffset = -minSelectedInMatrix;				}															for (int ic = 0; ic< contigPanel.getTotalNumPeaks(); ic++){					if (selected[ic]){ //selected, therefore move						int consensusBase = contigPanel.getConsensusBaseFromOverallBase(ic);						int matrixPosition = contigPanel.getMatrixPositionOfConsensusPosition(consensusBase, originalData);						matrixData.setState(matrixPosition+selectOffset, it, getSequenceStateAtConsensusPosition(consensusBase, sequences[whichPanel], sequence));  					}				}							if (added){					matrixData.notifyListeners(this, new Notification(CharacterData.PARTS_ADDED, null));					originalData.notifyListeners(this, new Notification(CharacterData.PARTS_ADDED, null));					qualityData.notifyListeners(this, new Notification(CharacterData.PARTS_ADDED, null));				}				else {					matrixData.notifyListeners(this, new Notification(CharacterData.DATA_CHANGED, null));				}				contigPanel.repaintPanels();						} 	/*.................................................................................................................*/    	 public String getName() {		return "Move Read to Matrix";   	 }	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Moves the selected portion of the read that was touched to the sequence in the matrix" ;   	 }   	 }