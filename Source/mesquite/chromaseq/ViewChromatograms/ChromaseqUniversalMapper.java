package mesquite.chromaseq.ViewChromatograms;

import mesquite.categ.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.*;
import mesquite.chromaseq.lib.*;
import mesquite.meristic.lib.*;

public class ChromaseqUniversalMapper implements MesquiteListener {
	public static final int PHREDPHRAPMESQUITESEQUENCE = 0;   // this is the "Phred.Phrap.Mesquite" sequence line in the chromatogram viewer
	public static final int EDITEDMATRIXSEQUENCE = 1;   // this is the edited in matrix sequence line in the chromatogram viewer
	public static final int EDITEDMATRIX = 2;    // this is the row in the editedMatrix in the actually editedData object, including gaps etc.
	public static final int ORIGINALIMPORTSEQUENCE = 3;    // this is the "OriginalImport" sequence line in the chromatogram viewer.
	public static final int ACEFILECONTIG = 4;    // this is the "Phred.Phrap.Mesquite" sequence line in the chromatogram viewer.
	static final int numMappings = 5;

	SequencePanel aceContigPanel;
	SequencePanel matrixSequencePanel;
	SequencePanel originalImportSequencePanel;
	int numBasesOriginallyTrimmedFromStartOfPhPhContig= 0;
	Contig contig;
	ContigDisplay contigDisplay;
	int totalUniversalBases=0;
	int totalNumAddedBases=0;
	int totalNumDeletedBases=0;
	DNAData editedData;
	MeristicData registryData=null;
	MeristicData reverseRegistryData=null;
	int it=0;
	boolean hasBeenSet = false;

	int[][] universalBaseFromOtherBase;
	int[][] otherBaseFromUniversalBase;

	//	int[] aceContigBaseFromUniversalBase;
	//	int[] UniversalBaseFromAceContigBase;



	public ChromaseqUniversalMapper (ContigDisplay contigDisplay, MolecularData data){
		this.contigDisplay = contigDisplay;
		this.aceContigPanel = contigDisplay.getAceContigPanel();
		this.matrixSequencePanel = contigDisplay.getMatrixSeqPanel();
		this.originalImportSequencePanel = contigDisplay.getOrigSeqPanel();

		this.contig = contigDisplay.getContig();
		it = contigDisplay.getTaxon().getNumber();
		editedData = contigDisplay.getEditedData();
		if (editedData!=null)
			editedData.addListener(this);
		registryData = ChromaseqUtil.getRegistryData(editedData);
		reverseRegistryData = ChromaseqUtil.getRegistryData(editedData);

		numBasesOriginallyTrimmedFromStartOfPhPhContig = contig.getNumBasesOriginallyTrimmedFromStartOfPhPhContig(editedData, it);

		init();
	}

	/*.................................................................................................................*/
	/** passes which object changed, along with optional Notification object with details (e.g., code number (type of change) and integers (e.g. which character))*/
	public void changed(Object caller, Object obj, Notification notification){
		int code = Notification.getCode(notification);
		int[] parameters = Notification.getParameters(notification);
		if (obj instanceof CharacterData) {
			reset();
		} 
	}
	/** passes which object was disposed*/
	public void disposing(Object obj){
	}
	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/
	public boolean okToDispose(Object obj, int queryUser){
		return true;
	}

	public boolean getHasBeenSet() {
		return hasBeenSet;
	}
	/*.................................................................................................................*/
	public void init() {
		createOtherBaseFromUniversalBase();
		createUniversalBaseFromOtherBase();
	}

	/*.................................................................................................................*/
	public void createOtherBaseFromUniversalBase() {
		otherBaseFromUniversalBase = new int[numMappings][contigDisplay.getTotalNumOverallBases()];
	}
	/*.................................................................................................................*/
	public void createUniversalBaseFromOtherBase() {
		if (universalBaseFromOtherBase==null)
			universalBaseFromOtherBase = new int[numMappings][];
		if (universalBaseFromOtherBase[PHREDPHRAPMESQUITESEQUENCE]==null || universalBaseFromOtherBase[PHREDPHRAPMESQUITESEQUENCE].length!=aceContigPanel.getLength())
			universalBaseFromOtherBase[PHREDPHRAPMESQUITESEQUENCE] = new int[aceContigPanel.getLength()];
		if (universalBaseFromOtherBase[EDITEDMATRIXSEQUENCE]==null || universalBaseFromOtherBase[EDITEDMATRIXSEQUENCE].length!=matrixSequencePanel.getLength())
			universalBaseFromOtherBase[EDITEDMATRIXSEQUENCE] = new int[matrixSequencePanel.getLength()];
		if (universalBaseFromOtherBase[EDITEDMATRIX]==null || universalBaseFromOtherBase[EDITEDMATRIX].length!=editedData.getNumChars())
			universalBaseFromOtherBase[EDITEDMATRIX] = new int[editedData.getNumChars()];
		if (universalBaseFromOtherBase[ORIGINALIMPORTSEQUENCE]==null || universalBaseFromOtherBase[ORIGINALIMPORTSEQUENCE].length!=originalImportSequencePanel.getLength())
			universalBaseFromOtherBase[ORIGINALIMPORTSEQUENCE] = new int[originalImportSequencePanel.getLength()];
		if (universalBaseFromOtherBase[ACEFILECONTIG]==null || universalBaseFromOtherBase[ACEFILECONTIG].length!=contig.getNumBases())
			universalBaseFromOtherBase[ACEFILECONTIG] = new int[contig.getNumBases()];
	}
	/*.................................................................................................................*/


	int resetCount = 0;
	/*.................................................................................................................*/
	/* this method recalculates all mappings */
	public synchronized void reset() {
		Debugg.println("======= Resetting Universal Base Registry ======= " + (resetCount++));
		//		Debugg.printStackTrace("\n\nuniversalMapper reset: " + Thread.currentThread()+"\n\n");

		// =========== Calculate total number of universal bases ===========

		//	totalNumAddedBases=ChromaseqUtil.getTotalNumBasesAddedBeyondPhPhBases(editedData, it);
		//	totalNumDeletedBases=ChromaseqUtil.getTotalNumOriginalBasesTurnedToGaps(editedData, it);

		numBasesOriginallyTrimmedFromStartOfPhPhContig = contigDisplay.getNumBasesOriginallyTrimmedFromStartOfPhPhContig();
		totalNumAddedBases=0;
		totalNumDeletedBases=0;
		for (int ic = 0; ic< editedData.getNumChars(); ic++){  // going through the sourceData object.  This is either the edited matrix or the original matrix
			int positionInOriginal = registryData.getState(ic, it);
			if (registryData!=null){
				if (registryData.isUnassigned(ic, it)) {  //this must be an added base
					totalNumAddedBases++;
				} //else if (positionInOriginal>=0 && reverseRegistryData.getState(positionInOriginal,it)==ChromaseqUtil.DELETEDBASEREGISTRY) {  // this must be a deleted base
				//	totalNumDeletedBases++;
				//}
			}
		}


		/* contigDisplay.getTotalNumOverallBases() is the number of bases according to the contig 
		 * - it's the number of bases in the contig plus the extra bases in front and at the end (as found in individual reads
		 * that extend beyond the contig.  So, it's the length of overall bases according to the PhredPhrap cloud.
		 */
		totalUniversalBases = contigDisplay.getTotalNumOverallBases() + contig.getNumPadded();

		/* Now let's add to this the bases that 
		 */
		totalUniversalBases += totalNumAddedBases-totalNumDeletedBases;

		Debugg.println("  totalUniversalBases: " +totalUniversalBases);
		Debugg.println("  totalNumAddedBases: " +totalNumAddedBases + ", totalNumDeletedBases: " + totalNumDeletedBases);

		if (otherBaseFromUniversalBase==null || otherBaseFromUniversalBase[PHREDPHRAPMESQUITESEQUENCE].length!=totalUniversalBases)
			createOtherBaseFromUniversalBase();
		for(int mapping=0; mapping<numMappings; mapping++) 
			for (int i=0; i<otherBaseFromUniversalBase[mapping].length; i++) 
				otherBaseFromUniversalBase[mapping][i]=-1;

		MolecularData originalData = ChromaseqUtil.getOriginalData(editedData);

		createUniversalBaseFromOtherBase();
		for(int mapping=0; mapping<numMappings; mapping++) 
			for (int i=0; i<universalBaseFromOtherBase[mapping].length; i++) 
				universalBaseFromOtherBase[mapping][i]=-1;


		// =========== Calculate mappings for the ace contig panel (i.e., the "Original Untrimmed" one) ===========

		SequenceCanvas sequenceCanvas = aceContigPanel.getCanvas();
		MesquiteSequence sequence = aceContigPanel.getSequence();
		if (sequenceCanvas!=null && sequence!=null){
			int[] addedBases = new int[sequence.getLength()];//+contig.getReadExcessAtStart()];
			for (int sequenceBase=0; sequenceBase<numBasesOriginallyTrimmedFromStartOfPhPhContig; sequenceBase++){
				addedBases[sequenceBase] = 0;
			}
			int totalAddedBases = 0;
			int sequenceBases = numBasesOriginallyTrimmedFromStartOfPhPhContig-1;
			for (int ic=0; ic<registryData.getNumChars(); ic++){
				int icOriginal = registryData.getState(ic, it);
				if (icOriginal==ChromaseqUtil.ADDEDBASEREGISTRY) { //
					totalAddedBases++;
				} else if (originalData.isValidAssignedState(icOriginal,it)) {
					sequenceBases++;
					if (sequenceBases>=0 && sequenceBases<addedBases.length)
						addedBases[sequenceBases] = totalAddedBases;
				}
			}
			for (int sequenceBase=0; sequenceBase<sequence.getLength(); sequenceBase++){
				int universalBase = sequenceBase + contig.getReadExcessAtStart()+ addedBases[sequenceBase];
				 otherBaseFromUniversalBase[PHREDPHRAPMESQUITESEQUENCE][universalBase] = sequenceBase;
				 universalBaseFromOtherBase[PHREDPHRAPMESQUITESEQUENCE][sequenceBase] = universalBase;
				 otherBaseFromUniversalBase[ACEFILECONTIG][universalBase] = sequenceBase;
				 universalBaseFromOtherBase[ACEFILECONTIG][sequenceBase] = universalBase;
			}
		}

		// =========== Calculate mappings for the original import panel (i.e., the "Original.Trimmed" one - just like Original.Untrimmed but trimmed) ===========
		sequenceCanvas = originalImportSequencePanel.getCanvas();
		sequence = originalImportSequencePanel.getSequence();
		if (sequenceCanvas!=null && sequence!=null){
			int[] addedBases = new int[sequence.getLength()];//+contig.getReadExcessAtStart()];
			int totalAddedBases = 0;
			int sequenceBases = -1;
			for (int ic=0; ic<registryData.getNumChars(); ic++){
				int icOriginal = registryData.getState(ic, it);
				if (icOriginal==ChromaseqUtil.ADDEDBASEREGISTRY) { //
					totalAddedBases++;
				} else if (originalData.isValidAssignedState(icOriginal,it)) {
					sequenceBases++;
					if (sequenceBases>=0 && sequenceBases<addedBases.length)
						addedBases[sequenceBases] = totalAddedBases;
				}
			}
			for (int sequenceBase=0; sequenceBase<sequence.getLength(); sequenceBase++){
				int universalBase = sequenceBase + contig.getReadExcessAtStart()+ numBasesOriginallyTrimmedFromStartOfPhPhContig+addedBases[sequenceBase];
				universalBase+=contig.getNumPaddedBefore(otherBaseFromUniversalBase[ACEFILECONTIG][universalBase]);  // account for padding
				 otherBaseFromUniversalBase[ORIGINALIMPORTSEQUENCE][universalBase] = sequenceBase;
				 universalBaseFromOtherBase[ORIGINALIMPORTSEQUENCE][sequenceBase] = universalBase;
			}
		}

		// =========== Calculate mappings for edited sequence panel ===========

		int numBasesFound = -1;
		int numOriginalBasesFound=-1;
		int firstBase = editedData.getNumChars();
		int lastIC=-1;
		int numAddedBases = 0;
		int numDeletedBases = 0;
		int count = 0;
		int startingUniversalBase = contigDisplay.getUniversalBaseFromContigBase(numBasesOriginallyTrimmedFromStartOfPhPhContig-contigDisplay.getNumBasesAddedToStart());
		Debugg.println("++++startingUniversalBase: " + startingUniversalBase);
		Debugg.println("contigDisplay.getNumBasesOriginallyTrimmedFromStartOfPhPhContig()   " + contigDisplay.getNumBasesOriginallyTrimmedFromStartOfPhPhContig() + ", contigDisplay.getNumBasesAddedToStart(): " + contigDisplay.getNumBasesAddedToStart());

		boolean firstTimeThrough = true;
		int firstUniversalBase = 0;

		for (int ic = 0; ic< editedData.getNumChars(); ic++){  // going through the sourceData object.  This is either the edited matrix or the original matrix

			int positionInOriginal = registryData.getState(ic, it);
			boolean addedBase = false;
			if (registryData!=null){
				if (registryData.getState(ic, it)==ChromaseqUtil.ADDEDBASEREGISTRY) {  //this must be an added base
					positionInOriginal=-1;
					numAddedBases++;
					addedBase = true;
				} else if (positionInOriginal>=0 && reverseRegistryData.getState(positionInOriginal,it)==ChromaseqUtil.DELETEDBASEREGISTRY) {  // this must be a deleted base
					numDeletedBases++;
				}
			}
			if (ChromaseqUtil.isUniversalBase(editedData,ic,it)){
				if (editedData.isInapplicable(ic, it))
					count++;

				numBasesFound++;
				//if (positionInOriginal<0) {  // but it wasn't in the original
				//} else {
				numOriginalBasesFound++;
				if (ic < firstBase)
					firstBase = ic;

				int sequenceBase = numBasesFound;
				int matrixBase = ic;
				int universalBase = startingUniversalBase+numBasesFound;
				universalBase+=contig.getNumPaddedBefore(otherBaseFromUniversalBase[ACEFILECONTIG][universalBase]);  // account for padding

				if (firstTimeThrough)  {
					firstTimeThrough=false;
					firstUniversalBase = universalBase;
				}

				if (sequenceBase>=0 && sequenceBase<universalBaseFromOtherBase[EDITEDMATRIXSEQUENCE].length)
					universalBaseFromOtherBase[EDITEDMATRIXSEQUENCE][sequenceBase] = universalBase;
				if (universalBase>=0 && universalBase<otherBaseFromUniversalBase[EDITEDMATRIXSEQUENCE].length)
					otherBaseFromUniversalBase[EDITEDMATRIXSEQUENCE][universalBase] = sequenceBase;


				if (matrixBase>=0 && matrixBase<universalBaseFromOtherBase[EDITEDMATRIX].length)
					universalBaseFromOtherBase[EDITEDMATRIX][matrixBase] = universalBase;
				if (universalBase>=0 && universalBase<otherBaseFromUniversalBase[EDITEDMATRIX].length)
					otherBaseFromUniversalBase[EDITEDMATRIX][universalBase] = matrixBase;


				if (ic>lastIC)
					lastIC = ic;

				//}
			} 

		}

		int negativeBase = -1;
		for (int universalBase = firstUniversalBase-1; universalBase>=0; universalBase--) {
			otherBaseFromUniversalBase[EDITEDMATRIX][universalBase] = negativeBase;
			negativeBase--;
		}

		Debugg.println("******** inapplicable *******  "   + count);

		int startToShow = 760;
		int numToShow = 3;
		Debugg.println("   otherBaseFromUniversalBase[PHREDPHRAPMESQUITESEQUENCE] ");
		for (int universalBase = startToShow; universalBase<startToShow+numToShow; universalBase++) {
			Debugg.println("   " + universalBase + ": " + otherBaseFromUniversalBase[PHREDPHRAPMESQUITESEQUENCE][universalBase]);
		}
		Debugg.println("   otherBaseFromUniversalBase[EDITEDMATRIXSEQUENCE] ");
		for (int universalBase = startToShow; universalBase<startToShow+numToShow; universalBase++) {
			Debugg.println("   " + universalBase + ": " + otherBaseFromUniversalBase[EDITEDMATRIXSEQUENCE][universalBase]);
		}
		Debugg.println("   otherBaseFromUniversalBase[EDITEDMATRIX] ");
		for (int universalBase = startToShow; universalBase<startToShow+numToShow; universalBase++) {
			Debugg.println("   " + universalBase + ": " + otherBaseFromUniversalBase[EDITEDMATRIX][universalBase]);
		}

		/*
	if (numBasesFound<0) { //all gaps in original
			for (int i=0; i<universalBaseFromOtherBase[MATRIXSEQUENCE].length; i++) {
				universalBaseFromOtherBase[MATRIXSEQUENCE][i] = i;
				otherBaseFromUniversalBase[MATRIXSEQUENCE][i] = i;
			}
		}
		else { //trailing bit go above numbers present
			for (int ic = 0; ic< firstBase; ic++){ //going from first original base to the right
				otherBaseFromUniversalBase[MATRIXSEQUENCE][ic] = ic-firstBase;

			}
			for (int i=numBasesFound+1; i<universalBaseFromOtherBase[MATRIXSEQUENCE].length; i++)
				universalBaseFromOtherBase[MATRIXSEQUENCE][i] = ++lastIC;
		}

		//filling in trailing bit in case matrix was added to
		int highestDefined = otherBaseFromUniversalBase[MATRIXSEQUENCE].length-1;
		for (highestDefined = otherBaseFromUniversalBase[MATRIXSEQUENCE].length-1; highestDefined>=0; highestDefined--){
			if (otherBaseFromUniversalBase[MATRIXSEQUENCE][highestDefined]>=0)
				break;
		}
		int max = -1;
		for (int ic = 0; ic<otherBaseFromUniversalBase[MATRIXSEQUENCE].length; ic++)
			if (MesquiteInteger.isCombinable(otherBaseFromUniversalBase[MATRIXSEQUENCE][ic]) && max < otherBaseFromUniversalBase[MATRIXSEQUENCE][ic])
				max = otherBaseFromUniversalBase[MATRIXSEQUENCE][ic];
		for (int ic = highestDefined+1; ic<otherBaseFromUniversalBase[MATRIXSEQUENCE].length; ic++)
			otherBaseFromUniversalBase[MATRIXSEQUENCE][ic] = ++max;
		 */

		hasBeenSet = true;
	}
	/*.................................................................................................................*/

	public int getUniversalBaseFromOtherBase(int otherBaseSystem, int otherBase) {
		if (otherBase<0 || otherBase>=universalBaseFromOtherBase[otherBaseSystem].length)
			return -1;
		return universalBaseFromOtherBase[otherBaseSystem][otherBase];
	}
	/*.................................................................................................................*/

	public int getOtherBaseFromUniversalBase(int otherBaseSystem, int universalBase) {
		if (universalBase<0 || universalBase>=otherBaseFromUniversalBase[otherBaseSystem].length)
			return -1;
		return otherBaseFromUniversalBase[otherBaseSystem][universalBase];
	}
	/*.................................................................................................................*/

	public int getEditedMatrixBaseFromUniversalBase(int universalBase) {
		if (universalBase<0 || universalBase>=otherBaseFromUniversalBase[EDITEDMATRIX].length)
			return -1;
		return otherBaseFromUniversalBase[EDITEDMATRIX][universalBase];
	}
	/*.................................................................................................................*/

	public int getEditedMatrixBaseFromOtherBase(int otherBaseSystem, int otherBase) {
		if (otherBase<0 || otherBase>=universalBaseFromOtherBase[otherBaseSystem].length)
			return -1;
		int universalBase = universalBaseFromOtherBase[otherBaseSystem][otherBase];
		if (universalBase<0 || universalBase>=otherBaseFromUniversalBase[EDITEDMATRIX].length)
			return -1;
		return otherBaseFromUniversalBase[EDITEDMATRIX][universalBase];
	}
	/*.................................................................................................................*/

	public int getOtherBaseFromEditedMatrixBase(int otherBaseSystem, int matrixBase) {
		if (matrixBase<0 || matrixBase>=universalBaseFromOtherBase[EDITEDMATRIX].length)
			return -1;
		int universalBase = universalBaseFromOtherBase[EDITEDMATRIX][matrixBase];
		if (universalBase<0 || universalBase>=otherBaseFromUniversalBase[otherBaseSystem].length)
			return -1;
		return otherBaseFromUniversalBase[otherBaseSystem][universalBase];
	}
	/*.................................................................................................................*/

	public int getNumUniversalBases() {
		return totalUniversalBases;
	}


	/*.................................................................................................................*/


	public String toString() {
		StringBuffer b = new StringBuffer();
		int newTotalUniversalBases = contigDisplay.getTotalNumOverallBases();


		for (int displayBase=0; displayBase<newTotalUniversalBases; displayBase++){
			b.append(" "+otherBaseFromUniversalBase[PHREDPHRAPMESQUITESEQUENCE][displayBase]);
		}
		b.append("\n\n");
		for (int i=0; i<aceContigPanel.getSequence().getLength(); i++){
			b.append(" "+universalBaseFromOtherBase[PHREDPHRAPMESQUITESEQUENCE][i]);
		}
		return b.toString();
	}


}