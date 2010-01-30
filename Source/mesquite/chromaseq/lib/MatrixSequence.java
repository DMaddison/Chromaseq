/* Mesquite chromaseq source code.  Copyright 2005-2009 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.lib; import java.awt.Color;import mesquite.categ.lib.*;import mesquite.chromaseq.ViewChromatograms.ChromaseqUniversalMapper;import mesquite.cont.lib.*;import mesquite.lib.*;import mesquite.lib.characters.CharacterData;import mesquite.meristic.lib.MeristicData;public abstract class MatrixSequence implements MesquiteSequence, MesquiteListener { 	MolecularData edited;	MolecularData original;	ContinuousData quality;	MolecularData sourceData;	MeristicData registryData;	MeristicData reverseRegistryData;	ContigDisplay contigDisplay;	ChromaseqUniversalMapper universalMapper;	Contig contig;	protected String sequenceString = "";	//	int it;	Taxon taxon;	protected int it = 0;	/*.................................................................................................................*/	public  MatrixSequence (ContigDisplay contigDisplay, MolecularData edited, MolecularData original, ContinuousData quality, Contig contig,  int it){		this.edited = edited;		registryData = ChromaseqUtil.getRegistryData(edited);		reverseRegistryData = ChromaseqUtil.getReverseRegistryData(edited);		this.original = original;		this.quality = quality;		this.contig = contig;		this.contigDisplay = contigDisplay;		if (contigDisplay!=null)			universalMapper = contigDisplay.getUniversalMapper();		//		this.it = it;		taxon = edited.getTaxa().getTaxon(it);		this.it = it;		//	calculateOriginalPositions();		edited.addListener(this);	}	/** passes which object changed, along with optional Notification object with details (e.g., code number (type of change) and integers (e.g. which character))*/	public void changed(Object caller, Object obj, Notification notification){		int code = Notification.getCode(notification);		int[] parameters = Notification.getParameters(notification);		if (obj instanceof CharacterData) {			if (ChromaseqUtil.validChromaseqMatrix((CharacterData)obj)) {				boolean recalcMappers = parameters==null;				if (!recalcMappers) {					if (parameters.length<3 || parameters[2]!=MesquiteListener.CELL_SUBSTITUTION || !ChromaseqUtil.isChromaseqEditedMatrix((CharacterData)obj))						recalcMappers=true;				}				if (recalcMappers){					calculateMappingToSourceData();				}			}		} 	}	public boolean isEditedMatrix() {		return false;	}	public Color getStandardColorOfBase(int i){		return null;	}	public boolean sourceReadIsLowerQuality(int i, int smallConflictThreshold, MesquiteBoolean higherReadConflicts, int largeConflictThreshold, MesquiteBoolean muchHigherReadConflicts) {		return contig.sourceReadIsLowerQuality(i,smallConflictThreshold, higherReadConflicts, largeConflictThreshold, muchHigherReadConflicts);	}	/** passes which object was disposed*/	public void disposing(Object obj){	}	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/	public boolean okToDispose(Object obj, int queryUser){		return true;	}	public void dispose(){		if (original != null)			original.removeListener(this);	}	public int getTaxonNumber(){		return taxon.getNumber();	}	public MolecularData getData(){		return sourceData;	}	public MolecularData getOriginalData(){		return original;	}	/*.................................................................................................................*/	protected boolean reversed() {		return false;	}	/*.................................................................................................................*/	protected boolean complemented() {		return false;	}	/*.................................................................................................................*	public int getLength(){		int it = getTaxonNumber();		int count = 0;		MeristicData registryData = ChromaseqUtil.getRegistryData(sourceData);		DNAData originalData = ChromaseqUtil.getOriginalData(sourceData);		int numChars = sourceData.getNumChars();		for (int ic = 0; ic<numChars; ic++){			int icOriginal = 0;			if (registryData!=null)				icOriginal = registryData.getState(ic, it);			if ((!sourceData.isInapplicable(ic, it) || icOriginal==ChromaseqUtil.ADDEDBASEREGISTRY || originalData.isValidAssignedState(icOriginal,it)))				count++;		}		return count;	}	/*.................................................................................................................*/	public int getLength(){		int 	it = getTaxonNumber();		int count = 0;		for (int ic = 0; ic< sourceData.getNumChars(); ic++){			if (!sourceData.isInapplicable(ic,it)){				count++;			}		}		return count;	}	/*.................................................................................................................*/	public long[] getSequenceAsLongs(){		int 	it = getTaxonNumber();		int count = 0;		for (int ic = 0; ic< sourceData.getNumChars(); ic++){			if (!sourceData.isInapplicable(ic,it))				count++;		}		long[] states = new long[count];		int i = 0;		if (reversed()) {			for (int ic = sourceData.getNumChars()-1; ic>=0; ic--){				if (!sourceData.isInapplicable(ic,it)){					long s = 0;					s = ((DNAData)sourceData).getStateRaw(ic, it);					if (complemented()) {						s = DNAState.complement(s);					} 						states[i] = s;					i++;				}			}		} else {			for (int ic = 0; ic< sourceData.getNumChars(); ic++){				if (!sourceData.isInapplicable(ic,it)){					long s = ((DNAData)sourceData).getStateRaw(ic, it);					if (complemented())						s = DNAState.complement(s);					states[i] = s;					i++;				}			}		}		return states;	}	/*.................................................................................................................*/	public String getSequence(){		int 	it = getTaxonNumber();		StringBuffer seq = new StringBuffer(sourceData.getNumChars());		if (reversed()) {			for (int ic = sourceData.getNumChars()-1; ic>=0; ic--){				if (!sourceData.isInapplicable(ic,it)){					long s = 0;					s = ((DNAData)sourceData).getStateRaw(ic, it);					if (complemented()) {						s = DNAState.complement(s);					} 						((DNAData)sourceData).statesIntoStringBufferCore(ic, s, seq, true, true, true);				}			}		} else {			for (int ic = 0; ic< sourceData.getNumChars(); ic++){				if (!sourceData.isInapplicable(ic,it)){					long s = ((DNAData)sourceData).getStateRaw(ic, it);					if (complemented())						s = DNAState.complement(s);					((DNAData)sourceData).statesIntoStringBufferCore(ic, s, seq, true, true, true);				}			}		}		calculateMappingToSourceData();		sequenceString = seq.toString();  //store for future use		return sequenceString;	}	/*.................................................................................................................*/	public boolean isNucleotides(){		return sourceData instanceof DNAData;	}	public String getName(){		if (sourceData==null)			return null;		int it = getTaxonNumber();		return sourceData.getName() + " (taxon " + sourceData.getTaxa().getTaxonName(it) + ")";	}	public double getQualityScore(int ic, int it){  // ic is the position in the edited matrix		int mapping = registryData.getState(ic, it);		return quality.getState(mapping, it, 0);	}	public int getQualityOfBase(int ic){  // using index of local sequence 		int it = getTaxonNumber();		if (sourceData == null)			return 0;		if (quality == null) 			return 100;		return (int)(ChromaseqUtil.getQualityScoreForEditedMatrixBase(sourceData,matrixBaseFromSequenceBase(ic), it) + 0.01);	}	/*..........................*/	public abstract int universalMapperOtherBaseValue();	/*.................................................................................*/	public int matrixBaseFromSequenceBase(int iSequence){		if (universalMapper==null) {			universalMapper = contigDisplay.getUniversalMapper();		}		return universalMapper.getEditedMatrixBaseFromOtherBase(universalMapperOtherBaseValue(), iSequence);	}	/*.................................................................................*/	public int sequenceBaseFromMatrixBase(int iMatrix){   // iMatrix is base in the edited matrix.		if (contigDisplay!=null && universalMapper==null)			universalMapper = contigDisplay.getUniversalMapper();		return universalMapper.getOtherBaseFromEditedMatrixBase(universalMapperOtherBaseValue(), iMatrix);	}	/*.................................................................................................................*/	public void calculateMappingToSourceData(){		if (universalMapper==null)			universalMapper = contigDisplay.getUniversalMapper();		if (universalMapper!=null && !universalMapper.getHasBeenSet()) {			universalMapper.reset(false);		}		 	}}