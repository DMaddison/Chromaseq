/* Mesquite chromaseq source code.  Copyright 2005-2008 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.lib; import java.awt.Color;import mesquite.categ.lib.*;import mesquite.cont.lib.*;import mesquite.lib.*;import mesquite.lib.characters.CharacterData;import mesquite.lists.lib.ListModule;public abstract class MatrixSequence implements MesquiteSequence, MesquiteListener { 	MolecularData edited;	MolecularData original;	ContinuousData quality;	ChromaseqBaseMapper chromMapper;	MolecularData data;	Contig contig;	int it;	int[] positionInSourceMatrix;	int[] positionInSequence;	int[] positionInSourceMatrixRev;	int[] positionInSequenceRev;	/*.................................................................................................................*/	public  MatrixSequence (MolecularData edited, MolecularData original, ContinuousData quality, Contig contig,  int it){		this.edited = edited;		this.original = original;		this.quality = quality;		this.contig = contig;		this.it = it;		chromMapper = new ChromaseqBaseMapper(edited);	//	calculateOriginalPositions();		edited.addListener(this);}	/** passes which object changed, along with optional Notification object with details (e.g., code number (type of change) and integers (e.g. which character))*/	public void changed(Object caller, Object obj, Notification notification){		int code = Notification.getCode(notification);		int[] parameters = Notification.getParameters(notification);		if (obj instanceof CharacterData) {			calculateMappingToSourceData();		}	}		public boolean isEditedMatrix() {		return false;	}	public Color getStandardColorOfBase(int i){		return null;	}	public boolean sourceReadIsLowerQuality(int i, int smallConflictThreshold, MesquiteBoolean higherReadConflicts, int largeConflictThreshold, MesquiteBoolean muchHigherReadConflicts) {		return contig.sourceReadIsLowerQuality(i,smallConflictThreshold, higherReadConflicts, largeConflictThreshold, muchHigherReadConflicts);	}		/** passes which object was disposed*/	public void disposing(Object obj){	}	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/	public boolean okToDispose(Object obj, int queryUser){			return true;	}	public void dispose(){			if (original != null)					original.removeListener(this);	}	public int getTaxonNumber(){		return it;	}	public MolecularData getData(){		return data;	}	public MolecularData getOriginalData(){		return original;	}	public String getSequence(){		StringBuffer seq = new StringBuffer(data.getNumChars());		boolean firstHit = false;		for (int ic = 0; ic< data.getNumChars(); ic++){			int oIC = data.findInvertedPositionOfOriginalSite(ic, it);			if (!(chromMapper.originalIsInapplicable(oIC, it) && data.isInapplicable(oIC, it))){				data.statesIntoStringBuffer(oIC, it, seq, true, true, true);				firstHit = true;			}			else if (!firstHit && !data.isInapplicable(oIC, it)){				data.statesIntoStringBuffer(oIC, it, seq, true, true, true);			}		}		calculateMappingToSourceData();		return seq.toString();	}	public long getSite(int icTarget, MesquiteInteger currentMatrixIC, MesquiteInteger currentSequenceIC, MesquiteBoolean firstHit){			for (int ic = currentMatrixIC.getValue(); ic< data.getNumChars(); ic++){			int oIC = data.findInvertedPositionOfOriginalSite(ic, it);			if (!(chromMapper.originalIsInapplicable(oIC, it) && data.isInapplicable(oIC, it))){				if (currentSequenceIC.getValue() == icTarget){					currentMatrixIC.setValue(ic);					return data.getState(ic, it);				}				currentSequenceIC.increment();				firstHit.setValue(true);			}			else if (!firstHit.getValue() && !data.isInapplicable(oIC, it)){				if (currentSequenceIC.getValue() == icTarget){					currentMatrixIC.setValue(ic);					return data.getState(ic, it);				}				currentSequenceIC.increment();			}		}		return CategoricalState.unassigned;	}	public boolean isNucleotides(){		return data instanceof DNAData;	}		public String getName(){		if (data==null)			return null;		return data.getName() + " (taxon " + data.getTaxa().getTaxonName(it) + ")";	}	public int getLength(){		int count = 0;		for (int ic = 0; ic< data.getNumChars(); ic++){			if (!data.isInapplicable(ic, it))  //doesn't need to find inverted because counting all				count++;		}		return count;	}	public int getQualityOfBase(int ic){  // using index of local sequence    		if (data == null)   			return 0;   		if (quality == null)    			return 100;   		return (int)(chromMapper.getQualityScore(matrixBaseFromSequenceBase(ic), it) + 0.01);	}	/*.................................................................................*/	public int sequenceBaseFromMatrixBase(int iMatrix){   // iMatrix is base in the edited matrix.		if (!isEditedMatrix()) {			if (chromMapper!=null) {				int originalBase = chromMapper.getOriginalPositionFromEditedMatrix(iMatrix, it);  				if (originalBase<0) {					return -1;				}				else if (originalBase>=original.getNumChars())					return original.getNumChars()-1;				else if (originalBase>=positionInSequence.length)					return positionInSequence.length-1; //return positionInSequence[positionInSequence.length-1];				else					return originalBase; //return positionInSequence[originalBase];			}		}		if (iMatrix<0)			return positionInSequence[0];		if (iMatrix>=positionInSequence.length)			return positionInSequence[positionInSequence.length-1];		return positionInSequence[iMatrix];	}	/*.................................................................................*/	public int sequenceBaseFromMatrixBaseCurrent(int iMatrix){		if (iMatrix<0) {			if (positionInSequence.length==0)				return 0;			else				return iMatrix + positionInSequence[0];		}		else if (iMatrix>=positionInSequence.length)			return iMatrix;		if (chromMapper!=null) {						// why shouldn't this just be based on sequencePositions?						return chromMapper.getOriginalPositionFromEditedMatrix(iMatrix, it);  //but this just returns the original position!		} 		return iMatrix;		}	/*.................................................................................*/	public int sequenceBaseFromMatrixBase2(int iMatrix){		if (iMatrix<0) {			if (positionInSequence.length==0)				return 0;			else				return iMatrix + positionInSequence[0];		}		else if (iMatrix>=positionInSequence.length)			return iMatrix;		if (chromMapper!=null) {			return chromMapper.getOriginalPositionFromEditedMatrix(positionInSequence[iMatrix], it);		} 		return positionInSequence[iMatrix];		}	/*.................................................................................*/	public int matrixBaseFromSequenceBase(int iSequence){		if (!isEditedMatrix()) {			int pos =0;			if (iSequence<0)				pos=0;			else if (iSequence>=positionInSourceMatrix.length)				pos=positionInSourceMatrix.length-1;			int originalPos = positionInSourceMatrix[pos];			if (chromMapper!=null) {				return chromMapper.getEditedMatrixPositionFromOriginal(originalPos, it);			} 		}		 if (iSequence<0)				return positionInSourceMatrix[0]+iSequence;		 if (iSequence>=positionInSourceMatrix.length)				return positionInSourceMatrix[positionInSourceMatrix.length-1]+iSequence-positionInSourceMatrix.length+1;		return positionInSourceMatrix[iSequence];	}	/*.................................................................................*/	public int matrixBaseFromSequenceBaseCurrent(int iSequence){		int pos = iSequence;		/*(if (originalPositions == null || originalPositions.length<1)			pos = iSequence;		else if (iSequence<0)			pos = iSequence + originalPositions[0];		else if (iSequence>=originalPositions.length)			pos = iSequence ;		else			pos = originalPositions[iSequence]; */		if (chromMapper!=null) {			int oldPos = pos;			pos = chromMapper.getEditedMatrixPositionFromOriginal(pos, it);		//	if (pos<0) Debugg.println("" + oldPos + "     " + pos);		} 		return pos;	}	/*.................................................................................*/	public void dumpFirstPositions(){			String s = "";			for (int i=0; i<30; i++)				s += " " + positionInSourceMatrix[i];			MesquiteMessage.println("originalPositions " + s);			s = "";			for (int i=0; i<30; i++)				s += " " + positionInSequence[i];			MesquiteMessage.println("sequencePositions " + s + "  this " + this);	}	/*.................................................................................*/	public abstract boolean inapplicableInSourceMatrixIndex(int ic, int it);		/*.................................................................................................................*/	public void calculateMappingToSourceDataCurrent(){				int numInSeq=0;		for (int ic = 0; ic< data.getNumChars(); ic++){			if (!data.isInapplicable(ic, it)) {				numInSeq++;			}		}		//Debugg.println("\n"+getName());		//Debugg.println("   calculateMappingToSourceData, numInSeq: " + numInSeq);		/*positionInSourceMatrix[i] is the position on the Source (i.e., originalData, editedData) matrix of site i in this sequence; 		note it assumes a one-to-one correspondence between the sequence and non-gaps in Source matrix		*/			if (positionInSourceMatrix == null || positionInSourceMatrix.length != numInSeq)			positionInSourceMatrix = new int[numInSeq];		if (positionInSourceMatrixRev == null || positionInSourceMatrixRev.length != numInSeq)			positionInSourceMatrixRev = new int[numInSeq];		for (int i=0; i<positionInSourceMatrix.length; i++) {			positionInSourceMatrix[i] = -1;			positionInSourceMatrixRev[i] = -1;		}				//	positionInSequence[i] is the position in this sequence of site i in the Source matrix, starting at firstBase of the original matrix (in case shifted right by gaps) 		if (positionInSequence == null || positionInSequence.length != data.getNumChars())			positionInSequence = new int[data.getNumChars()];		if (positionInSequenceRev == null || positionInSequenceRev.length != data.getNumChars())			positionInSequenceRev = new int[data.getNumChars()];		for (int i=0; i<positionInSequence.length; i++) {			positionInSequence[i] = MesquiteInteger.unassigned; //-1;			positionInSequenceRev[i] = MesquiteInteger.unassigned; //-1;		}		numInSeq=0;		for (int ic = 0; ic< data.getNumChars(); ic++){			if (!data.isInapplicable(ic, it)) {				positionInSourceMatrix[numInSeq] = ic;				positionInSequence[ic] = numInSeq;				numInSeq++;			}		}				numInSeq=0;		for (int ic = data.getNumChars()-1; ic>=0; ic--){			if (!data.isInapplicable(ic, it)) {				positionInSourceMatrixRev[numInSeq] = ic;				positionInSequenceRev[ic] = numInSeq;				numInSeq++;			}		}		}	/*.................................................................................................................*/	public void calculateMappingToSourceData(){		/*originalPositions[i] is the position on the original matrix of site i in this sequence; 		note it assumes sequence includes only sites Source to non-gaps in original, 		except for leading or trailing blocks.  The original may have had 		*/		if (positionInSourceMatrix == null || positionInSourceMatrix.length != data.getNumChars())			positionInSourceMatrix = new int[data.getNumChars()];		for (int i=0; i<positionInSourceMatrix.length; i++)				positionInSourceMatrix[i] = -1;		//	sequencePositions[i] is the position in this sequence of site i in the original matrix, starting at firstBase of the original matrix (in case shifted right by gaps) 		if (positionInSequence == null || positionInSequence.length != original.getNumChars())			positionInSequence = new int[data.getNumChars()];		for (int i=0; i<positionInSequence.length; i++)			positionInSequence[i] = MesquiteInteger.unassigned; //-1;		Debugg.println(getName());		Debugg.println("   calculateMappingToSourceData");		int count = -1;		int firstBase = data.getNumChars();		int lastIC = -1;		for (int ic = 0; ic< data.getNumChars(); ic++){			int oIC = data.findInvertedPositionOfOriginalSite(ic, it);						if (!inapplicableInSourceMatrixIndex(oIC, it)){				count++;				if (oIC < firstBase)						firstBase = oIC;				positionInSourceMatrix[count] = oIC;				if (oIC>=0 && oIC<positionInSequence.length)					positionInSequence[oIC] = count;				if (oIC>lastIC)					lastIC = oIC;			}		}		if (count<0) { //all gaps in original			for (int i=0; i<positionInSourceMatrix.length; i++) {				positionInSourceMatrix[i] = i;				positionInSequence[i] = i;			}		}		else { //trailing bit go above numbers present			for (int ic = 0; ic< firstBase; ic++){ //going from first original base to the right				positionInSequence[ic] = ic-firstBase;							}			for (int i=count+1; i<positionInSourceMatrix.length; i++)				positionInSourceMatrix[i] = ++lastIC;		}				//filling in trailing bit in case matrix was added to		int highestDefined = positionInSequence.length-1;		for (highestDefined = positionInSequence.length-1; highestDefined>=0; highestDefined--){			if (positionInSequence[highestDefined]>=0)				break;		}		int max = -1;		for (int ic = 0; ic<positionInSequence.length; ic++)			if (MesquiteInteger.isCombinable(positionInSequence[ic]) && max < positionInSequence[ic])				max = positionInSequence[ic];		for (int ic = highestDefined+1; ic<positionInSequence.length; ic++)			positionInSequence[ic] = ++max;				/* THIS is an experiment to see if it fixes forgetting where inserted bits go		for (int ic = 1; ic<sequencePositions.length; ic++) {			if (sequencePositions[ic]<0)				sequencePositions[ic] = sequencePositions[ic-1];		}		*/		}	/*.................................................................................................................*/	public void OLDcalculateOriginalPositions(){		/*originalPositions[i] is the position on the original matrix of site i in this sequence; 		note it assumes sequence includes only sites Source to non-gaps in original, 		except for leading or trailing blocks.  The original may have had 		*/		if (positionInSourceMatrix == null || positionInSourceMatrix.length != data.getNumChars())			positionInSourceMatrix = new int[data.getNumChars()];		for (int i=0; i<positionInSourceMatrix.length; i++)				positionInSourceMatrix[i] = -1;		int count = -1;		int firstBase = -1;		int lastIC = -1;		for (int ic = 0; ic< data.getNumChars(); ic++){			if (!inapplicableInSourceMatrixIndex(ic, it)){				count++;				if (firstBase <0)						firstBase = ic;				positionInSourceMatrix[count] = ic;				lastIC = ic;			}		}		if (count<0) { //all gaps in original			for (int i=0; i<positionInSourceMatrix.length; i++)				positionInSourceMatrix[i] = i;		}		else { //trailing bit go above numbers present			for (int i=count+1; i<positionInSourceMatrix.length; i++)				positionInSourceMatrix[i] = ++lastIC;		}		//	sequencePositions[i] is the position in this sequence of site i in the original matrix, starting at firstBase of the original matrix (in case shifted right by gaps) 		if (positionInSequence == null || positionInSequence.length != original.getNumChars())			positionInSequence = new int[data.getNumChars()];		for (int i=0; i<positionInSequence.length; i++)			positionInSequence[i] = -1;		count = -1;		for (int ic = 0; ic< firstBase; ic++){ //going from first original base to the right			positionInSequence[ic] = ic-firstBase;					}		for (int ic = firstBase; ic >= 0 && ic< data.getNumChars(); ic++){ //going from first original base to the right			if (!inapplicableInSourceMatrixIndex(ic, it))				count++;			positionInSequence[ic] = count;					}		if (count<0) { //all gaps in original			for (int i=0; i<positionInSequence.length; i++)				positionInSequence[i] = i;		}	}}