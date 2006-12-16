/* Mesquite chromaseq source code.  Copyright 2005-2006 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.lib; import mesquite.lib.*;import mesquite.categ.lib.*;import mesquite.cont.lib.*;import java.awt.*;/* ======================================================================== */public  class Contig implements MesquiteSequence { 	String contigName;	int numBases;	int numReadsInContig;	int numBaseSegmentsInContig;	int numPadded;	int mixedEndThreshold;	int mixedEndWindow;	int qualThresholdForTrim;	private String bases, polyBases, trimmedBases, trimmedPolyBases;	int numTrimmedBases;	boolean[] isPadding;	int[] paddedBefore;	Read[] reads;	int[] quality;	int[] conflict;	int[] trimmedQuality;	int[] baseSource;	int currentRead;	int currentAF = -1;	int startTrim = 0;	int conflictLevelThreshold = 30;  	boolean dimLowQualityConflicts = true; 	boolean originalDirection=true;	public static NameReference paddingRef = NameReference.getNameReference("paddingBefore");		RegistryCoordinator registries;	/*.................................................................................................................*/	public  Contig (String contigName, int numBases, int numReadsInContig, int numBaseSegmentsInContig, String bases){		//this numBases is the number of bases as reported in the CO line of the ACE file.  it includes pads		this.contigName = contigName;		this.numBases = numBases;		this.numReadsInContig = numReadsInContig;				reads = new Read[numReadsInContig];		currentRead = 0;		this.numBaseSegmentsInContig = numBaseSegmentsInContig;		this.bases = bases;		trimmedBases = bases;		if (numBases<bases.length())			numBases = bases.length();		numTrimmedBases = numBases;		numPadded = 0;		isPadding = new boolean[numBases];		paddedBefore = new int[numBases];		baseSource = new int[numBases];		resetPadding(startTrim, true);				quality = new int[numBases];		conflict = new int[numBases];		for (int i=0; i<numBases; i++)  {			quality[i] = 80;  //DRM:			conflict[i] = 0;		}		trimmedQuality = null;		registries = new RegistryCoordinator(this, numBases, bases);	}		//this should be during phred phrap processing and on file re-read; returns number of pads BEFORE the trim	public int resetPadding(int startTrim, boolean resetBaseSource){		numPadded = 0;		int numPaddedBeforeTrim = 0;		this.startTrim = startTrim;		for (int i=0; i<bases.length(); i++)  {			isPadding[i] = (bases.charAt(i)=='*');			if (isPadding[i]){				//if (i>=startTrim) //don't count padding in trimmed area					numPadded++;				if (i<startTrim)					numPaddedBeforeTrim++;			}						//else			if (i+1< paddedBefore.length)				paddedBefore[i+1] = numPadded;			if (resetBaseSource)				baseSource[i] = -1;		}		return numPaddedBeforeTrim;	}	/*.................................................................................................................*/	public  void setDimLowQualityConflicts (boolean dimLowQualityConflicts){		this.dimLowQualityConflicts = dimLowQualityConflicts;	}	/*.................................................................................................................*/	public  void setDimConflictMarkerThreshold (int conflictLevelThreshold){		this.conflictLevelThreshold = conflictLevelThreshold;	}	/*.................................................................................................................*/	public  void setName (String s){		contigName = s;	}	/*.................................................................................................................*/	public  String getName (){		return contigName;	}	/*.................................................................................................................*/	public  void postReadProcessing (){		if (reads != null && reads.length>0){			for (int i=0;i<getLength(); i++) {				int conflictLevel = 0;  // this will store the quality score of conflicting bases, 								//in particular the score of the second-highest-quality read that conflicts with a higher-quality read				for (int r1= 0; r1 < reads.length; r1++){					int readBase1 = reads[r1].getReadBaseFromConsensusBase(i);					char cr1 = reads[r1].getPhdBaseChar(readBase1);					for (int r2= r1+1; r2 < reads.length; r2++){						int readBase2 = reads[r2].getReadBaseFromConsensusBase(i);						char cr2 = reads[r2].getPhdBaseChar(readBase2);						if (cr1 == ' ')							cr1 = cr2;						else if (cr1 != cr2 && cr2 != ' ') {  //David:  this is picking up ghost terminal N's right at the end of reads; they don't appear in read as drawn							int minQuality = MesquiteInteger.minimum(reads[r1].getPhdBaseQuality(readBase1),reads[r2].getPhdBaseQuality(readBase2));							conflictLevel = MesquiteInteger.maximum(minQuality,conflictLevel);//		Debugg.println("RED (" + cr + ") vs (" + c + ")");//Debugg.println("conflict level:  " + conflictLevel);						}					}				}				conflict[i]=conflictLevel;			}		}			}		public Color getHighlightColor(int iSequence, int iConsensus){			if (conflict!=null&& iConsensus>=0 && iConsensus<conflict.length)				if (dimLowQualityConflicts) {					if (conflict[iConsensus]==0)						return null;					else if (conflict[iConsensus]>=conflictLevelThreshold)						return Color.red;					else						return (ColorDistribution.brighter(Color.red, (float)(conflict[iConsensus]/(1.0*conflictLevelThreshold))));				}				else if (conflict[iConsensus]>0)						return Color.red;			return null;	}	/*.................................................................................................................*/	public  long getBase (int i){				char b;		if (polyBases!=null) {			b = polyBases.charAt(i);		}		else {			b = bases.charAt(i);  		}			return DNAState.fromCharStatic(b);	}	/*.................................................................................................................*/	public  String getSequence (){		StringBuffer sb = new StringBuffer(numBases+20);				if (polyBases!=null) {			for (int i = 0; i<polyBases.length(); i++) {				sb.append(polyBases.charAt(i));  			}		}		else for (int i = 0; i<bases.length(); i++) {			sb.append(bases.charAt(i));  		}			return sb.toString();	}	/*.................................................................................................................*/	public  int getLength (){		if (polyBases!=null)			return polyBases.length();		else 			return bases.length();	}	public Color getQualityColorOfBase(int i){		int qual = getQualityOfBase(i);		if (qual==0)			return ColorDistribution.brighter(AceFile.getColorOfQuality(qual),0.2);		else			return ColorDistribution.brighter(AceFile.getColorOfQuality(qual),0.5);	}	/*.................................................................................................................*/	public int getQualityOfBase(int i){		return getUntrimmedQuality(i);	}	/*.................................................................................................................*/	public int getUntrimmedQuality(int i){		if (quality==null || i<0 || i>quality.length)			return 0;		else			return quality[i];	}	/*.................................................................................................................*/	public int getBaseSource(int i){		if (baseSource==null || i<0 || i>baseSource.length-1)			return -1;		else			return baseSource[i];	}	/*.................................................................................................................*/ 	public boolean sourceReadIsLowerQuality(int i, int smallConflictThreshold, MesquiteBoolean higherReadConflicts, int largeConflictThreshold, MesquiteBoolean muchHigherReadConflicts) {		int source = getBaseSource(i);		Read read = getRead(source);		if (higherReadConflicts!=null)			higherReadConflicts.setValue(false);		if (muchHigherReadConflicts!=null)			muchHigherReadConflicts.setValue(false);		if (read==null)			return false;		int readBase = read.getReadBaseFromConsensusBase(i);		int sourceQuality = read.getPhdBaseQuality(readBase);		char sourceChar = read.getPhdBaseChar(readBase);		boolean lower = false;				for (int r = 0; r<getNumReadsToShow(); r++){			if (r!=source){				read = getRead(r);				readBase = read.getReadBaseFromConsensusBase(i);				int quality = read.getPhdBaseQuality(readBase);				char c = read.getPhdBaseChar(readBase);				if (quality>=sourceQuality+smallConflictThreshold && c!=' ') {					lower = true;					if (higherReadConflicts!=null && sourceChar != c)						higherReadConflicts.setValue(true);					if (muchHigherReadConflicts!=null && sourceChar != c && quality>=sourceQuality+largeConflictThreshold)						muchHigherReadConflicts.setValue(true);				}			}		}		return lower;	}	/*.................................................................................................................*/	public int getConflictLevel(int i){		if (conflict==null || i<0 || i>conflict.length-1)			return 0;		else			return conflict[i];	}	/*.................................................................................................................*/	public boolean isNucleotides (){		return true;	}	/*.................................................................................................................*/	public  void processBQ (Parser aceParser){		String s;		for (int i=0; i<numBases; i++) {			if (bases.charAt(i)!='*') {				s  = aceParser.getNextToken();				quality[i] = MesquiteInteger.fromString(s);			}			else				quality[i] = -1;		}	}	/*.................................................................................................................*/	public  int getNumPaddedBefore (int i){		if (i<0 || paddedBefore.length == 0)			return 0;		if (i>= paddedBefore.length)			return paddedBefore[paddedBefore.length-1];		return paddedBefore[i];	}	/*Returns the where the ith consensus base would be if the padding were expanded into the sequence*/	public int getPaddedSiteFromUnpaddedSite(int i){		if (i<0)			return 0;		int count =-1;		int j = 0;		int answer= i;		while (count< i && count<isPadding.length  && j< isPadding.length){			if (!isPadding[j]){				count++;				answer = j;			}			j++;		}		if (count == i)			return answer;		return i;	}	/*.................................................................................................................*/	public  boolean getIsPadding (int i){		return isPadding[i];	}	/*.................................................................................................................*/	public  int getNumPadded (){		return numPadded;	}		/*.................................................................................................................*/	public  int getNumBases (){		return numBases;	}	/*.................................................................................................................*/	public  int getReadExcessAtStart (){		int firstRead = 0;		for (int i=0; i<numReadsInContig && i<=currentAF; i++) {			int startOfRead;			startOfRead = reads[i].getConsensusBaseFromReadBase(0);							if (startOfRead<firstRead)				firstRead = startOfRead;		}		return Math.abs(firstRead);			}	/*.................................................................................................................*/	public  int getReadExcessAtEnd (){		int lastRead = 0;		for (int i=0; i<numReadsInContig && i<=currentAF; i++) {			int endOfRead;			endOfRead = reads[i].getConsensusPositionOfLastBase()-getNumBases();			if (endOfRead>lastRead)				lastRead = endOfRead;		}		return Math.abs(lastRead);			}	/*.................................................................................................................*/	public  int getNumUnpadded (){		return numBases-numPadded;	}	/*.................................................................................................................*/	public  void createRead(int readNumber, String readName, boolean complemented, int frameStart){				if (readNumber<reads.length)			reads[readNumber] = new Read (this, readName,complemented,frameStart, registries);	}	/*.................................................................................................................*/	public  void processAF(String readName, boolean complemented, int frameStart){		currentAF++;		createRead(currentAF,readName,complemented,frameStart);	}	/*.................................................................................................................*/	public  void processBS(String name, int firstBase, int lastBase){		int r = getReadNumber(name);		for (int i=firstBase-1; i<lastBase; i++)			baseSource[i] = r;			}	NameReference chromatogramReadsRef = NameReference.getNameReference("chromatogramReads");	NameReference origReadFileNamesRef= NameReference.getNameReference("readFileNames");	NameReference origTaxonNameRef= NameReference.getNameReference("origName");	/*.................................................................................................................*/   	 public void importSequence(Taxa taxa, long whichContig, DNAData data, DNAData originalData, ContinuousData qualityData, boolean useExistingTaxonIfFound, String relativeAceFilePath, String baseName, NameReference aceRef, NameReference qualityNameRef, boolean usePolyBases,MesquiteInteger maxChar){		int it = 0;		Taxon taxon = null;		Associable tInfoData = data.getTaxaInfo(true);		Associable tInfoOriginalData = originalData.getTaxaInfo(true);		if (useExistingTaxonIfFound){			it = taxa.whichTaxonNumber(baseName);			if (it<0) {				it = taxa.getNumTaxa();				taxa.addTaxa(it, 1, true);				//data.addTaxa(it, 1);				//qualityData.addTaxa(it, 1);				taxon = taxa.getTaxon(it);				if (taxon!=null){					taxon.setName(baseName);					taxa.setAssociatedObject(origTaxonNameRef, it, baseName);				}			}			else				taxon = taxa.getTaxon(it);				if (tInfoData != null){					tInfoData.setAssociatedObject(aceRef, it, relativeAceFilePath);					tInfoData.setAssociatedLong(AceFile.WHICHCONTIGREF, it, whichContig);				}				if (tInfoOriginalData != null){					tInfoOriginalData.setAssociatedObject(aceRef, it, relativeAceFilePath);					tInfoOriginalData.setAssociatedLong(AceFile.WHICHCONTIGREF, it, whichContig);				}		//	taxa.setAssociatedObject(aceRef, it, relativeAceFilePath);  //here need to set path as relative to original directory!		}		else {			it = taxa.getNumTaxa();			taxa.addTaxa(it, 1, false);			data.addTaxa(it, 1);			originalData.addTaxa(it, 1);			qualityData.addTaxa(it, 1);			if (tInfoData != null){				tInfoData.setAssociatedObject(aceRef, it, relativeAceFilePath);				tInfoData.setAssociatedLong(AceFile.WHICHCONTIGREF, it, whichContig);			}			if (tInfoOriginalData != null){				tInfoOriginalData.setAssociatedObject(aceRef, it, relativeAceFilePath);				tInfoOriginalData.setAssociatedLong(AceFile.WHICHCONTIGREF, it, whichContig);			}//			taxa.setAssociatedObject(aceRef, it, relativeAceFilePath);  //here need to set path as relative to original directory!			taxon = taxa.getTaxon(it);			//taxa.setAnnotation(it, s);			taxon.setName(baseName);			taxa.setAssociatedObject(origTaxonNameRef, it, baseName);		}		String s ="Sequence from following chromatograms: ";		String[] fileNames = new String[numReadsInContig*2];		for (int i=0; i<numReadsInContig && i<=currentAF; i++){			s += " " + reads[i].getName();  //Wayne: at this point, if you call reads[i].getOriginalName(), you should have the name of the original ABI file			fileNames[i*2] = reads[i].getName();			fileNames[i*2+1] = reads[i].getOriginalName();		}		Associable as = data.getTaxaInfo(true);		as.setAssociatedObject(chromatogramReadsRef, it, s);		as.setAssociatedObject(origReadFileNamesRef, it, fileNames);		 				int currNumChars = data.getNumChars();				if (tInfoData != null){			tInfoData.setAssociatedLong(NameReference.getNameReference("startTrim"), it, startTrim);		}		if (tInfoOriginalData != null){			tInfoOriginalData.setAssociatedLong(NameReference.getNameReference("startTrim"), it, startTrim);		}				if (currNumChars<getNumUnpadded()) {			data.addCharacters(currNumChars, getNumUnpadded()-currNumChars, true);			data.addInLinked(currNumChars, getNumUnpadded()-currNumChars, true);		}		currNumChars = data.getNumChars();		long seqQual = 0;		String b;		int[] qualityToUse = quality;		if (usePolyBases) {			if (trimmedQuality!=null)  {				b = trimmedPolyBases;				qualityToUse = trimmedQuality;			}			else				b = polyBases;		} else {			if (trimmedQuality!=null) {				b = trimmedBases;				qualityToUse = trimmedQuality;			}			else				b = bases;		}		//	Debugg.println("in contig, usePolyBases: " + usePolyBases);//	Debugg.println("   bases to import: " + b);//	Debugg.println("   polyBases: " + polyBases);//	Debugg.println("   trimmedPolyBases: " + trimmedPolyBases);			CategoricalState cs = new CategoricalState();		cs.setToInapplicable();		for (int ic=0; ic<data.getNumChars(); ic++) {  // here we are going through the sequence and do initial assignment			data.setState(ic, it, cs);			originalData.setState(ic, it, cs);		}		int seqLength = -1;		int iPrev = -1;		if(!StringUtil.blank(b))			for (int i=0; i<b.length(); i++) {  // here we are going through the sequence				if (b.charAt(i)!='*') {					seqLength++;					data.setState(seqLength, it, b.charAt(i));					originalData.setState(seqLength, it, b.charAt(i));					int starsBefore = i-iPrev-1;					if (starsBefore >0) {						originalData.setCellObject(paddingRef, seqLength, it, new MesquiteInteger(starsBefore));					}					seqQual += qualityToUse[i];										qualityData.setState(seqLength, it, 0, (double)qualityToUse[i]);					if (maxChar.getValue()<seqLength)						maxChar.setValue(seqLength);					iPrev = i;				}		}		else {			if (tInfoData != null)				tInfoData.setAssociatedLong(NameReference.getNameReference("chromatogramsExist"), it, numReadsInContig);			if (tInfoOriginalData != null)				tInfoOriginalData.setAssociatedLong(NameReference.getNameReference("chromatogramsExist"), it, numReadsInContig);		}		//taxa.setAssociatedDouble(qualityNameRef, it, seqQual*1.0/seqLength);		if (tInfoData != null)			tInfoData.setAssociatedDouble(qualityNameRef, it, seqQual*1.0/seqLength);		if (tInfoOriginalData != null)			tInfoOriginalData.setAssociatedDouble(qualityNameRef, it, seqQual*1.0/seqLength);	}	/*.................................................................................................................*/	public  void setLowQualityToLowerCase(int qualThreshold){		StringBuffer sb = new StringBuffer(numBases);		String lcBases = bases.toLowerCase();		for (int i=0; i<numBases; i++) { 			if (quality[i]<=qualThreshold && quality[i]>=0)				sb.append(lcBases.charAt(i));			else 				sb.append(bases.charAt(i));		}		bases = sb.toString();		trimmedBases = bases;						sb = new StringBuffer(numBases);		lcBases = polyBases.toLowerCase();		for (int i=0; i<numBases; i++) { 			if (quality[i]<=qualThreshold && quality[i]>=0)				sb.append(lcBases.charAt(i));			else 				sb.append(polyBases.charAt(i));		}		polyBases = sb.toString();		trimmedPolyBases = polyBases;	}	/*.................................................................................................................*/	public  void processQA(int read, int qualClipStart, int qualClipEnd, int alignClipStart, int alignClipEnd){		if (read>=0)			reads[read].processQA(qualClipStart, qualClipEnd, alignClipStart, alignClipEnd);	}	/*.................................................................................................................*/	public  void processRD(String name, int numPaddedBases, int numWholeReadInfoItems, int numReadTags, String bases){		int r = getReadNumber(name);		if (r>=0) {					reads[r].processRD(numPaddedBases,bases,name);		}	}	/*.................................................................................................................*/	public  void processDS(int read, String abiFile, String directoryPath, String phdFile, String DSRemainder,boolean processPolymorphisms, double polyThreshold){		if (read>=0)			reads[read].processDS(abiFile, directoryPath, phdFile, DSRemainder, processPolymorphisms, polyThreshold);	}	/*.................................................................................................................*/	public String getPolyBaseString(int i) {		if (i<0|| i>bases.length()-1)			return "";		char b = bases.charAt(i);		int r=baseSource[i];		long state = reads[r].getPolyBase(i);		if (state>=0) {			boolean lowerCase = Character.isLowerCase(b);  //this is a lower case base			String s = DNAData.getIUPACSymbol(state);			if (lowerCase)				s = s.toLowerCase();//Debugg.println("baseSource i: " + i + ", r: " + r + ", b: " +b +", lowerCase: " + lowerCase + ", state: " + state + ", s: " + s);			return s;		}		else {//Debugg.println("baseSource i: " + i + ", r: " + r + ", b: " +b +", state: " + state);			return ""+b;		}					}	/*.................................................................................................................*/   	 public void processPolys(CommandRecord commandRec, double polyThreshold){		int r;		boolean lowerCase;		String s;		StringBuffer sb = new StringBuffer(numBases);		for (int i=0; i<numBases; i++) { //go through the PADDED contig's bases and see whether any are polymorphic			r= baseSource[i];			char b = bases.charAt(i);			if (b!='*' && r>=0 && reads[r].getPolyExists()){  //this base comes from read r and there is a .poly file for it				long state = reads[r].getPolyBase(i);				if (state>=0) {					lowerCase = Character.isLowerCase(b);  //this is a lower case base					s = DNAData.getIUPACSymbol(state);					if (lowerCase)						s = s.toLowerCase();//Debugg.println("baseSource i: " + i + ", r: " + r + ", b: " +b +", lowerCase: " + lowerCase + ", state: " + state + ", s: " + s);					sb.append(s);				}				else {//Debugg.println("baseSource i: " + i + ", r: " + r + ", b: " +b +", state: " + state);					sb.append(b);				}			}			else				sb.append(b);		}		polyBases = sb.toString();		trimmedPolyBases = polyBases;   	 }	/*.................................................................................................................*/	public  int getNumReadsToShow (){		return MesquiteInteger.minimum(numReadsInContig, currentAF + 1);	}	/*.................................................................................................................*/	public Read getRead (int i){		if ( i<numReadsInContig && i<=currentAF && i>=0)			return reads[i];		return null;	}	/*.................................................................................................................*/	public  int getReadNumber (String name){		for (int i=0; i<numReadsInContig && i<=currentAF; i++)			if (name.equalsIgnoreCase(reads[i].getName()))				return i;		return -1;	}	/*.................................................................................................................*/	public  String getReadListForLog (){		StringBuffer sb = new StringBuffer();		for (int i=0; i<numReadsInContig && i<=currentAF; i++) {			sb.append("      " + reads[i].getOriginalName());			if (reads[i].getComplemented())				sb.append(" (rev)");			sb.append(StringUtil.lineEnding());		}		return sb.toString();	}	/*.................................................................................................................*/   	 public void unTrimQA(){		for (int i=0; i<numReadsInContig && i<=currentAF; i++)			reads[i].unTrimQA();   	 }	/*.................................................................................................................*/   	 public int numBadInWindow(boolean up, int windowBoundary, int windowLength, int qualThresholdForTrim, MesquiteInteger endOfWindow){   	 	if (endOfWindow==null)   	 		return 0;   	 	int startBase = windowBoundary;   	 	int bad=0;   	 	int total = 0;   	 	if (up) {  // then the boundary is the lower boundary   	 		if (windowBoundary<0)   	 			startBase = 0;			for (int i = startBase; i<bases.length(); i++) {				if (bases.charAt(i)!='*') {					if (quality[i]<=qualThresholdForTrim)						bad++;					endOfWindow.setValue(i);					total ++;					if (total>=windowLength)						break;				}			}   	 	}   	 	else {    	 		if (windowBoundary>bases.length()-1)    	 			startBase = bases.length() -1;			for (int i = startBase; i>=0; i--) {				if (bases.charAt(i)!='*') {					if (quality[i]<=qualThresholdForTrim)						bad++;					endOfWindow.setValue(i);					total ++;					if (total>=windowLength)						break;				}			}		}		return bad;   	 }	/*.................................................................................................................*/   	 public void trimMixedEnds(int mixedEndThreshold, int mixedEndWindow, int qualThresholdForTrim){ 		this.mixedEndThreshold= mixedEndThreshold;  //store these for next time; would need for reverse complement.		this.mixedEndWindow=mixedEndWindow;		this.qualThresholdForTrim=qualThresholdForTrim;		int startBase = 0;		int endBase =bases.length()-1;		MesquiteInteger lastInWindow = new MesquiteInteger();  //will record the last character in the starting bad window		MesquiteInteger firstInWindow = new MesquiteInteger();  // will record the first character in the ending bad window				int badWindowStart = -1;		for (int i = 0; i<bases.length(); i++) {			int bad = numBadInWindow(true, i, mixedEndWindow, qualThresholdForTrim, lastInWindow);			if (bad >=mixedEndThreshold)   // this window has enough bad ones, reset the value of badWindowStart				badWindowStart = i; 			else				break;		}		startBase = lastInWindow.getValue()+1;		if (badWindowStart>=0)    // looking for lower end to keep			for (int i = lastInWindow.getValue(); i>=0; i--) {				if (quality[i]>qualThresholdForTrim) {  //still high enough					startBase = i;				} else					break;			}		if (startBase>bases.length()-1)			startBase = bases.length();		int badWindowEnd = -1;		for (int i = bases.length()-1; i>=0; i--) {			int bad = numBadInWindow(false, i, mixedEndWindow, qualThresholdForTrim, firstInWindow);			if (bad >=mixedEndThreshold)				badWindowEnd = i;			else				break;		}		endBase = firstInWindow.getValue()-1;		if (badWindowEnd>=0)    // looking for upper end to keep			for (int i = firstInWindow.getValue(); i<bases.length(); i++) {				if (quality[i]>qualThresholdForTrim) {  //still high enough					endBase = i;				} else					break;			}		if (endBase<0)			endBase = 0;				registries.cloneRegistry(RegistryCoordinator.ORIGINAL_CONTIG, RegistryCoordinator.TRIMMED_CONTIG);		int count = 0;		if (endBase>=startBase) {			registries.trimLinkedInRegistry(RegistryCoordinator.TRIMMED_CONTIG,startBase, bases.length()-1-endBase);			trimmedQuality = new int[endBase-startBase+1];			for (int i = startBase; i<=endBase; i++) {				trimmedQuality[count] = quality[i];				count++;			}			trimmedBases = bases.substring(startBase,endBase+1);			trimmedPolyBases = polyBases.substring(startBase,endBase+1);		}		else {			trimmedQuality = new int[1];			trimmedBases = "";			trimmedPolyBases = "";		}		numTrimmedBases = trimmedBases.length();		resetPadding(startBase, false);				   	 }	/*.................................................................................................................*/	public  String toFASTAString (boolean usePolyBases, int qualThresholdForTrim, String baseName){		StringBuffer sb = new StringBuffer(numBases+20);		if (baseName!=null)			sb.append(">" + baseName);		else 			sb.append(">" + contigName);		sb.append(StringUtil.lineEnding());		String b;		if (usePolyBases && trimmedPolyBases!=null) 			b = trimmedPolyBases;		else			b = trimmedBases;		for (int i = 0; i<b.length(); i++) {			if ((i!=0) && (i%50==0))				sb.append(StringUtil.lineEnding());			if (b.charAt(i)!='*') {				sb.append(b.charAt(i));  			}		}			return sb.toString();	}	/*.................................................................................................................*/	public  String toString (boolean usePolyBases){		StringBuffer sb = new StringBuffer(numBases+20);		sb.append("CO");		sb.append(" " + StringUtil.blanksToUnderline(contigName));    		sb.append(" " + numBases);		sb.append(" " + numReadsInContig);		sb.append(" " + numBaseSegmentsInContig);		sb.append(" U"+StringUtil.lineEnding());				if (usePolyBases && polyBases!=null) {			for (int i = 0; i<polyBases.length(); i++) {				if ((i!=0) && (i%50==0))					sb.append(StringUtil.lineEnding());				sb.append(polyBases.charAt(i));  			}		}		else for (int i = 0; i<bases.length(); i++) {			if ((i!=0) && (i%50==0))				sb.append(StringUtil.lineEnding());			sb.append(bases.charAt(i));  		}		sb.append(StringUtil.lineEnding());		sb.append(StringUtil.lineEnding());			return sb.toString();	}	/*.................................................................................................................*/	public  String extrasToString (){		StringBuffer sb = new StringBuffer(numBases*3);		//first do BQ		sb.append("BQ"+StringUtil.lineEnding());		int count = 0;		for (int i=0; i<numBases; i++) {			if (quality[i]>=0) {				if (count!=0 && count%50==0)					sb.append(StringUtil.lineEnding());				sb.append(" " + quality[i]);				count++;			}		}		sb.append(StringUtil.lineEnding() + StringUtil.lineEnding());	//now do AF		for (int i=0; i<numReadsInContig && i<=currentAF; i++)			sb.append(reads[i].getAFString());				//now do BS		int b = 0;		while (b<numBases) {			int r = baseSource[b];			if (r>=0) {				String s = "BS "+(b+1);				while(b<numBases && baseSource[b]==r)					b++;				b--;				sb.append(s + " " + (b+1) + " " + StringUtil.blanksToUnderline(reads[r].getName()) + StringUtil.lineEnding());			}			b++;		}		sb.append(StringUtil.lineEnding());			//now do RD, QA, DS sets		for (int i=0; i<numReadsInContig && i<=currentAF; i++)			sb.append(reads[i].toString());					return sb.toString();	}		/*.................................................................................................................*/	public boolean getOriginalDirection(){		return originalDirection;	}	/*.................................................................................................................*/	public void reverseComplement(){		originalDirection = !originalDirection;		for (int i=0; i<numReadsInContig && i<=currentAF; i++)			reads[i].reverseComplement(numBases);		baseSource = IntegerArray.reverse(baseSource);		trimmedQuality = IntegerArray.reverse(trimmedQuality);		quality = IntegerArray.reverse(quality);		isPadding = Bits.reverseBooleanArray(isPadding);		paddedBefore = IntegerArray.reverse(paddedBefore);		bases=StringUtil.reverse(bases);		polyBases = StringUtil.reverse(polyBases);		bases=DNAData.complementString(bases);		polyBases = DNAData.complementString(polyBases);				trimMixedEnds(mixedEndThreshold, mixedEndWindow, qualThresholdForTrim); // this will reset paddedBefore, trimmedBases, trimmedPolyBase					conflict = IntegerArray.reverse(conflict);			}	/*.................................................................................................................*/	public RegistryCoordinator getRegistry(){		return registries;	}	/*.................................................................................................................*/	public void reportRegistry(){		getRegistry().report();	}	/*.................................................................................................................*/	public void dispose(){   		if (reads!=null)   			for (int i=0; i<numReadsInContig && i<=currentAF; i++){  				if (reads[i]!=null)   					reads[i].dispose();   				reads[i] = null;   			}   		reads=null;		}}