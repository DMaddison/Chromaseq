/* Mesquite chromaseq source code.  Copyright 2005-2009 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.lib; import java.awt.*;import java.awt.event.*;import mesquite.lib.*;import mesquite.categ.lib.*;import mesquite.chromaseq.ViewChromatograms.ChromaseqUniversalMapper;public abstract class SequenceCanvas extends MousePanel implements KeyListener, FocusListener { 	protected MesquiteSequence sequence;//	int[] A,C,G,T;	protected int maxValue;	protected int centerBase, centerPixel;	protected boolean colorByQuality = true;//	int startOffset=0;	protected ContigDisplay contigDisplay;	protected boolean[] selected;  // uses local read as index	protected SequencePanel sequencePanel;	protected int id = -1;  	int contigID;	/*..........................*/	public SequenceCanvas(SequencePanel sequencePanel, MesquiteSequence sequence, ContigDisplay contigDisplay, int contigID) {		super();		this.sequence = sequence;		this.contigID = contigID;		setBackground(sequencePanel.getBackground());		this.sequencePanel = sequencePanel;		this.contigDisplay = contigDisplay;		String sequenceString = sequence.getSequence();		selected = new boolean[sequenceString.length()];   		for (int i=0;i<selected.length;i++) {			selected[i] = false;		}//		startOffset = window.getContig().getReadExcessAtStart();		reCalcCenterBase();		addKeyListener(this);   // we need to add these listeners for all SequenceCanvases, not just the editable ones, so that they can pick up arrow keys and the like 		addFocusListener(this); 		requestFocusInWindow();	}	public boolean isShownReversed(){		return contigDisplay.isShownReversed();	}	public boolean isShownComplemented(){		return contigDisplay.isShownComplemented();	}	public abstract int getConsensusFromLocalIndex(int i);	public abstract int getLocalIndexFromConsensus(int i);	public abstract int matrixBaseFromSequenceBase(int i);		public MesquiteSequence getSequence() {		return sequence;	}	/*	public int getMatrixFromLocalIndex(int i){		return i;	}	/* Returns the number of padded bases that are missing from this sequence (i.e. are in the consensus but not in this sequence) in front of the given consensus position */	//	public abstract  int paddedMissingBeforeConsensus(int ic);	public boolean getEditable(){ 		return true; //override if not editable	}	boolean hasFocus = false; 	public void focusLost(FocusEvent e){		hasFocus = false;		repaint();	}	public void focusGained(FocusEvent e){		hasFocus = true;		repaint();	}	public void keyReleased(KeyEvent e){  	}	public void keyTyped(KeyEvent e){		if (!getEditable()){			contigDisplay.keyTyped(e);			return;		}		int mod = MesquiteEvent.getModifiers(e);		if (MesquiteEvent.optionKeyDown(mod) || MesquiteEvent.commandOrControlKeyDown(mod)){			contigDisplay.keyTyped(e);			return;		}		int sel = oneSelected();		char k = e.getKeyChar();		if (hasFocus && sel>=0) {			enterState(sel, k);		}		else 			contigDisplay.keyTyped(e);	}	public void keyPressed(KeyEvent e){		contigDisplay.keyPressed(e);  //pass this off to the window so that it can deal with arrow keys, etc.	}	public void enterState(int ic, char k){	}	/*.................................................................................................................*	public int nextLowQuality(boolean right, int threshold) {		reCalcCenterBase();		for (int i=centerBase+1;i < sequence.getLength();i++) {			if (i>=0) {				int qual = sequence.getQualityOfBase(i); // using index of local sequence 				if (qual<threshold)					return getConsensusFromLocalIndex(i);			}		}		return -1;	}	/*.................................................................................................................*	public void goToNextLowQuality(boolean right) {		int nextLow = nextLowQuality(right, 30);		if (nextLow>=0 && nextLow<sequence.getLength())			window.scrollToConsensusBase(nextLow);	}	/*.................................................................................................................*/	public void setColorByQuality(boolean colorByQuality) {		this.colorByQuality = colorByQuality;	}	/*.................................................................................................................*/	public int oneSelected(){		int sel = -1;		if (hasFocus){			int count = 0;			for (int i=0; i<selected.length; i++) {				if (selected[i]) {					count++;					sel = i;				}			}			if (count!=1)				sel = -1;		}		return sel;	}	public abstract String getName();	/*...........................................................................*/	protected int getBaseCenterPixel(char c, Graphics g){		return getFontMetrics(g.getFont()).stringWidth(""+c) / 2;	}	/*...........................................................................*/	protected void reCalcCenterBase(){		centerBase = sequencePanel.centerBase;   //number of centered base		centerPixel = contigDisplay.getCenterPixelFromCenterBase(centerBase);   //number of pixels over this scrolled base is	}	long countFLP = 0;	/*..........................*/	private int findLocalPosition(int x, MesquiteInteger consensusPos, boolean inBetween) {		//	Debugg.println("%FLP =====" + countFLP);		int cwidth = getBounds().width;		if (isShownReversed())			x = cwidth-x;		reCalcCenterBase();		int leftPixel=centerPixel-cwidth/2;		int length = sequence.getLength();		int halfPeaks = contigDisplay.getApproximateNumberOfPeaksVisible()/2;		int centerConsensusBase = centerBase-contigDisplay.getContig().getReadExcessAtStart();		int firstConsensusBase = centerConsensusBase-halfPeaks;		int firstSequenceBase = getLocalIndexFromConsensus(firstConsensusBase);		int count = 0;		while (contigDisplay.getFullPixelValueOfConsensusBase(getConsensusFromLocalIndex(firstSequenceBase))-leftPixel >0 && firstSequenceBase>0 && count++<200)			firstSequenceBase--; //correcting for error in numpeaksvisible for this sequence		firstSequenceBase--;		if (consensusPos != null)			consensusPos.setValue(MesquiteInteger.unassigned);		int offsetForInserted = 0;		int lastGoodCons = -1;//		Debugg.println("%FLP " + countFLP + " " + length + " fsb " + firstSequenceBase);		countFLP++;		for (int i=firstSequenceBase;i < length;i++) {			if (i>=0) {				int cons = getConsensusFromLocalIndex(i);				if (MesquiteInteger.isCombinable(cons))					lastGoodCons = cons;				else {					cons = lastGoodCons;				}				offsetForInserted += contigDisplay.getSpaceInsertedBeforeDisplayBase(cons);				int pixels = contigDisplay.getFullPixelValueOfConsensusBase(cons)-leftPixel + offsetForInserted;				int halfSpace = (int)(contigDisplay.getAveragePeakDistance()/2.0);				int fullSpace = (int)(contigDisplay.getAveragePeakDistance());				int pixelsNextCons = contigDisplay.getFullPixelValueOfConsensusBase(cons+1)-leftPixel+ offsetForInserted + contigDisplay.getSpaceInsertedBeforeDisplayBase(cons+1);				if (!inBetween){					if (x < pixels+halfSpace){						if (consensusPos != null)							consensusPos.setValue(cons);						return i;					}					else if (x< pixelsNextCons-halfSpace){ //within region of inserted bases; find which one and return						int insert = 0;						while (x >= pixels+halfSpace){							insert++;							pixels += fullSpace;						}						if (consensusPos != null)							consensusPos.setValue(MesquiteInteger.unassigned);						return insert + i;					}				}				else {					if (x < pixels){						if (consensusPos != null)							consensusPos.setValue(cons);						return i;					}					else if (x< pixelsNextCons){ //within region of inserted bases						int insert = 0;						while (x >= pixels){							insert++;							pixels += fullSpace;						}						if (consensusPos != null)							consensusPos.setValue(MesquiteInteger.unassigned);						return insert + i;					}				}			}		}//		Debugg.println("done %FLP " + countFLP + " " + length + " fsb " + firstSequenceBase);		return MesquiteInteger.unassigned;	}	/*..........................*/	int topOfBase = 15;	int baseHeight = 16;	int notesHeight = 12;	private void fillRect(Graphics g, int width, int x, int y, int w, int h){		if (isShownReversed()){			g.fillRect(width-(x+w), y, w, h);		}		else {			g.fillRect(x, y, w, h);		}	}	private void drawRect(Graphics g, int width, int x, int y, int w, int h){		if (isShownReversed()){			g.drawRect(width-(x+w), y, w, h);		}		else {			g.drawRect(x, y, w, h);		}	}	private void drawLine(Graphics g, int width, int x, int y, int x2, int y2){		if (isShownReversed()){			g.drawLine(width-x, y, width-x2, y2);		}		else {			g.drawLine(x, y, x2, y2);		}	}	private void drawString(Graphics g, int width, String s, int x, int y){		if (isShownReversed()){			int sw = StringUtil.getStringDrawLength(g, s);			g.drawString(s, width-(x+sw), y);		}		else {			g.drawString(s, x, y);		}	}	/*..........................*/	public void paint(Graphics g) {			paint(g, false, null);	}	int count = 0;  //used for debugging	/*..........................*/	boolean continuingCondition(int i, String sequenceString, int lastSequenceBase) {		return i < sequenceString.length() && (i< lastSequenceBase || lastSequenceBase<0);	}	/*..........................*/	int firstBase(int firstSequenceBase) {		return firstSequenceBase;	}	/*..........................*/	public void paint(Graphics g, boolean colorByArray, SequenceMatchCalc suppliedColors) {			ChromaseqUniversalMapper graphicsMapper =contigDisplay.getUniversalMapper();		int sel = oneSelected();		Font curFont = g.getFont();		Font boldFont12 = new Font (curFont.getName(), Font.BOLD, 12);		Font plainFont10 = new Font (curFont.getName(), Font.PLAIN, 10);		FontMetrics fMB12 = getFontMetrics(boldFont12);		FontMetrics fMP10 = getFontMetrics(plainFont10);		setBackground(contigDisplay.getBackgroundColor());		int cheight = getBounds().height;		int cwidth = getBounds().width;		reCalcCenterBase();		int leftPixel=centerPixel-cwidth/2;		int halfPeaks = contigDisplay.getApproximateNumberOfPeaksVisible()/2;		int centerConsensusBase = centerBase-contigDisplay.getContig().getReadExcessAtStart();		int firstConsensusBase = centerConsensusBase-halfPeaks;		int lastConsensusBase = centerConsensusBase+halfPeaks;		int firstSequenceBase = getLocalIndexFromConsensus(firstConsensusBase);		int lastSequenceBase = getLocalIndexFromConsensus(lastConsensusBase);		if (!MesquiteInteger.isCombinable(firstSequenceBase))			firstSequenceBase=0;		if (!MesquiteInteger.isCombinable(lastSequenceBase))			lastSequenceBase=0;//Debugg.println("\n" + getName() + "  contig.getReadExcessAtStart: "+window.getContig().getReadExcessAtStart());//		Debugg.println("\n****\ninitial firstSequenceBase: " + firstSequenceBase + ", lastSequenceBase: " + lastSequenceBase);//   	Debugg.println("initial firstConsensusBase: " + firstConsensusBase + ", lastConsensusBase: " + lastConsensusBase);//   	Debugg.println("       getNumBasesAddedToStart: " + window.getNumBasesAddedToStart()); //  	Debugg.println("       getNumBasesOriginallyTrimmedFromStartOfPhPhContig: " + window.getNumBasesOriginallyTrimmedFromStartOfPhPhContig());		String sequenceString = sequence.getSequence();//		Debugg.println("     PAINT " + sequenceString);		if (selected.length != sequenceString.length()){			boolean[] newSel = new boolean[sequenceString.length()];			for (int i=0; i<newSel.length; i++)				newSel[i] = false;			for (int i=0; i<newSel.length && i<selected.length; i++)				newSel[i] = selected[i];			selected = newSel;		}		int count = 0;		int pix = contigDisplay.getFullPixelValueOfConsensusBase(getConsensusFromLocalIndex(firstSequenceBase))-leftPixel;		while (pix >0 && pix <cwidth&& firstSequenceBase>0 && count++<400) {			firstSequenceBase--; //correcting for error in numpeaksvisible for this sequence			pix = contigDisplay.getFullPixelValueOfConsensusBase(getConsensusFromLocalIndex(firstSequenceBase))-leftPixel;		}		count = 0;		pix = contigDisplay.getFullPixelValueOfConsensusBase(getConsensusFromLocalIndex(lastSequenceBase))-leftPixel;		while (pix>0 && pix <cwidth && count++<400) {			lastSequenceBase++; //correcting for error in numpeaksvisible for this sequence			pix = contigDisplay.getFullPixelValueOfConsensusBase(getConsensusFromLocalIndex(lastSequenceBase))-leftPixel;		}		firstSequenceBase--;		lastSequenceBase++;//	Debugg.println("firstSequenceBase: " + firstSequenceBase + ", lastSequenceBase: " + lastSequenceBase);				//Drawing the top line		g.setColor(Color.lightGray);		g.drawLine(0,0,cwidth,0); //^^^		// Draw center gray line		g.setColor(Color.lightGray);		g.drawLine(cwidth/2,0,cwidth/2,cheight);//^^^		int pixels = 0;		int offsetForInserted = 0;		int prevPixels = 0;		int prevPixels2 = 0;		int prevPixelsUsed = 0;		int prevIC = 0;		int halfSpace = (int)(contigDisplay.getAveragePeakDistance()/2.0);		int lastGoodCons = -1;				/* NOTE: in all of the following loops, there used to be a continuation condition added of "&& (i< lastSequenceBase || lastSequenceBase<0)" 		 * However, this caused the last few bases to be not drawn in several circumstances, so this condition was removed		 * DRM 3 July 2008		 * */		// Now to color the bases ==========================		if ((colorByQuality && getColorBaseBackground()) || (colorByArray && suppliedColors != null) || getHasSpecialStandardBaseColors()) {			for (int i=firstBase(firstSequenceBase); continuingCondition(i,sequenceString, lastSequenceBase) ;i++) {  				// if (i>=0){				if (i>=0){					int cons = getConsensusFromLocalIndex(i);										//if (i<25) Debugg.println("            cons: " + cons + ", i " + i);					if (MesquiteInteger.isCombinable(cons))						lastGoodCons = cons;					else						cons = lastGoodCons;					offsetForInserted +=  contigDisplay.getSpaceInsertedBeforeDisplayBase(cons);					pixels = contigDisplay.getFullPixelValueOfConsensusBase(cons)-leftPixel + offsetForInserted;					if (prevPixels == pixels){						pixels += contigDisplay.getAveragePeakDistance()*(i-prevIC);					}					else {						prevPixels = pixels;						prevIC = i;					}					int pixels2 = pixels + (int)contigDisplay.getAveragePeakDistance();					int nmid = halfSpace;					Color c = null;					if (colorByArray){						if (suppliedColors != null)							c = suppliedColors.getBaseMatchColor(i);					}					else if (colorByQuality)						c = sequence.getQualityColorOfBase(i);					else if (getHasSpecialStandardBaseColors())						c = sequence.getStandardColorOfBase(i);					if (c != null){						g.setColor(c);						fillRect(g, cwidth, pixels - nmid, topOfBase, pixels2 - pixels+1, baseHeight);//^^^					}				}			}		}				offsetForInserted = 0; 				//Now to draw a bar beneath the highlighted bases  ==========================		if (sequencePanel.getShowReadReadConflict())			for (int i=firstBase(firstSequenceBase); continuingCondition(i,sequenceString, lastSequenceBase) ;i++) {  				if (i>=0){					int cons = getConsensusFromLocalIndex(i);					if (MesquiteInteger.isCombinable(cons))						lastGoodCons = cons;					else						cons = lastGoodCons;					Color c = sequence.getHighlightColor(i, cons);					offsetForInserted +=  contigDisplay.getSpaceInsertedBeforeDisplayBase(cons);					pixels = contigDisplay.getFullPixelValueOfConsensusBase(cons)-leftPixel + offsetForInserted;					if (prevPixels == pixels){						pixels += contigDisplay.getAveragePeakDistance()*(i-prevIC);					}					else {						prevPixels = pixels;						prevIC = i;					}					int pixels2 = pixels + (int)contigDisplay.getAveragePeakDistance();					int nmid = getBaseCenterPixel(sequenceString.charAt(i),g);					if (c != null){						g.setColor(c);						fillRect(g, cwidth, pixels - nmid - 2, topOfBase+ baseHeight-1, pixels2 - pixels+1, 6);//^^^					}				}			}				//Now draw box around those bases that have source with relatively low quality  ==========================		if (sequencePanel.getShowLowerQualSourceConflictsWithHigherQualRead()) {			MesquiteBoolean higherReadConflicts = new MesquiteBoolean(false);			MesquiteBoolean muchHigherReadConflicts = new MesquiteBoolean(false);			for (int i=firstBase(firstSequenceBase); continuingCondition(i,sequenceString, lastSequenceBase) ;i++) {  				if (i>=0){					int cons = getConsensusFromLocalIndex(i);					if (MesquiteInteger.isCombinable(cons))						lastGoodCons = cons;					else						cons = lastGoodCons;					if (sequence.sourceReadIsLowerQuality(cons, 10, higherReadConflicts, 20, muchHigherReadConflicts)) {						offsetForInserted +=  contigDisplay.getSpaceInsertedBeforeDisplayBase(cons);						pixels = contigDisplay.getFullPixelValueOfConsensusBase(cons)-leftPixel + offsetForInserted;						if (prevPixels == pixels){							pixels += contigDisplay.getAveragePeakDistance()*(i-prevIC);						}						else {							prevPixels = pixels;							prevIC = i;						}						int nmid = halfSpace;						if (muchHigherReadConflicts.getValue()) {							g.setColor(Color.blue);							drawRect(g, cwidth, pixels-nmid, topOfBase, nmid*2, baseHeight);//^^^							drawRect(g, cwidth, pixels-nmid-1, topOfBase-1, nmid*2+2, baseHeight+2);//^^^						}						else {							g.setColor(Color.black);							drawRect(g, cwidth, pixels-nmid, topOfBase, nmid*2, baseHeight);//^^^						}					}				}			}		}		offsetForInserted = 0;		g.setFont(boldFont12);		// Now to show the selection  ==========================			int firstSel = MesquiteInteger.unassigned;	 		Composite composite = ColorDistribution.getComposite(g);			ColorDistribution.setTransparentGraphics(g);					g.setColor(Color.gray);			int lastSel = MesquiteInteger.unassigned;			for (int i=firstBase(firstSequenceBase); continuingCondition(i,sequenceString, lastSequenceBase)  ;i++) { 				if (i>=0){					int cons = getConsensusFromLocalIndex(i);					if (MesquiteInteger.isCombinable(cons))						lastGoodCons = cons;					else						cons = lastGoodCons;					offsetForInserted +=  contigDisplay.getSpaceInsertedBeforeDisplayBase(cons);					pixels = contigDisplay.getFullPixelValueOfConsensusBase(cons)-leftPixel + offsetForInserted;					if (prevPixels == pixels){						pixels += contigDisplay.getAveragePeakDistance()*(i-prevIC);					}					else {						prevPixels = pixels;						prevIC = i;					}					int nmid = halfSpace;					if (i< selected.length && selected[i]){						if (firstSel == MesquiteInteger.unassigned)							firstSel = pixels-nmid;						lastSel = pixels + nmid;					}					else if (firstSel != MesquiteInteger.unassigned){						fillRect(g, cwidth, firstSel, 0, lastSel - firstSel, cheight);//^^^						firstSel = MesquiteInteger.unassigned;					}				}			}			if (firstSel != MesquiteInteger.unassigned){  //uncompleted selection; select to end 				fillRect(g, cwidth, firstSel, 0, lastSel - firstSel, cheight);//^^^				firstSel = MesquiteInteger.unassigned;			}			ColorDistribution.setComposite(g,composite);						offsetForInserted = 0;		prevPixels = 0;				// Now to draw the text for bases  ==========================		for (int i=firstBase(firstSequenceBase); continuingCondition(i,sequenceString, lastSequenceBase)  ;i++) {  			if (i>=0){				int cons = getConsensusFromLocalIndex(i);				//int cons2 = graphicsMapper.getGraphicsBaseForSequencePanel(sequencePanel,i);				//Debugg.println("" + cons + "  " + cons2);				if (MesquiteInteger.isCombinable(cons))					lastGoodCons = cons;				else					cons = lastGoodCons;				offsetForInserted +=  contigDisplay.getSpaceInsertedBeforeDisplayBase(cons);				pixels = contigDisplay.getFullPixelValueOfConsensusBase(cons)-leftPixel + offsetForInserted;				if (prevPixels == pixels){					pixels += contigDisplay.getAveragePeakDistance()*(i-prevIC);				}				else {					prevPixels = pixels;					prevIC = i;				}				char c = sequenceString.charAt(i);				if (isShownComplemented()){					c = DNAData.complementChar(c);				}				Color textC = contigDisplay.getBaseColor(c, contigDisplay.getBackgroundColor());				g.setColor(textC);				g.setFont(boldFont12);				int nmid = fMB12.stringWidth(""+c) / 2;				if (sel== i && getEditable()){ //showing text edit box					g.setColor(Color.yellow);					int top = 2 + topOfBase;					int h = baseHeight;					drawRect(g, cwidth, pixels-nmid-1, top -3, nmid*2 +3, h+2);//^^^					g.setColor(Color.blue);					drawRect(g, cwidth, pixels-nmid-2, top -4, nmid*2 +5, h+4);//^^^					drawRect(g, cwidth, pixels-nmid-3, top -5, nmid*2 +7, h+6);//^^^					g.setColor(Color.yellow);					drawRect(g, cwidth, pixels-nmid-4, top -6, nmid*2 +9, h+8);//^^^					drawRect(g, cwidth, pixels-nmid-5, top -7, nmid*2 +11, h+10);//^^^				}				drawString(g, cwidth, ""+c,pixels-nmid, topOfBase + 12);//^^^				g.setColor(Color.black);				if ((i+1) % 10 == 0 && pixels - nmid > 100) {					g.setColor(Color.lightGray);					g.setFont(plainFont10);					nmid = fMP10.stringWidth(String.valueOf(i+1)) / 2;					drawString(g, cwidth, String.valueOf(i+1),pixels - nmid,notesHeight);//^^^				}			}		}		g.setFont(plainFont10);		GraphicsUtil.setFontSize(10,g);		g.setColor(Color.gray);		g.drawString(getName(),4,notesHeight);	}	/*--------------------------------------*/	public boolean getColorBaseBackground(){ 		return true; 	}	/*--------------------------------------*/	public boolean getHasSpecialStandardBaseColors(){ 		return false; 	}	/*--------@@@------------------------------*/	/*--------------------------------------*/	//uses index in consensus	public boolean setSelectedConsensus(int i, boolean sel, boolean repaint){		localFirstTouched = MesquiteInteger.unassigned;		localSecondTouched = MesquiteInteger.unassigned;		int ji = getLocalIndexFromConsensus(i);		if (selected != null && ji >= 0 && ji<selected.length) {			if (selected[ji] == sel)				return false;			selected[ji] = sel;  //selected uses index in consensus			if (repaint)				repaint();			return true;		}		return false;	}	public boolean getSelectedConsensus(int i){		int ji = getLocalIndexFromConsensus(i);		if (selected != null && ji >= 0 && ji<selected.length)			return selected[ji] ;		return false;	}	public void deselectAll(){		localFirstTouched = MesquiteInteger.unassigned;		localSecondTouched = MesquiteInteger.unassigned;		for (int i=0; i<selected.length; i++)			selected[i] = false; 	}	/*--------------------------------------*/	//this is consensus position	private void selectConsensusRange(int i, int k){		int iLoc = getLocalIndexFromConsensus(i);		int kLoc = getLocalIndexFromConsensus(k);		for (int j= iLoc; j<=kLoc; j++)			if (j >=0 && j< selected.length)				selected[j] = true;		for (int j = i; j<=k; j++)			sequencePanel.exportSelectConsensusPosition(j);	}	//this is local position	protected void selectLocalRange(int i, int k){		for (int j= i; j<=k; j++)			if (j >=0 && j< selected.length)				selected[j] = true;		for (int j = i; j<=k; j++){			if (!isInsertedBase(j))				sequencePanel.exportSelectConsensusPosition(getConsensusFromLocalIndex(j));		}	}	//this is local position	private void deselectLocalRange(int i, int k){		for (int j= i; j<=k; j++)			if (j >=0 && j< selected.length)				selected[j] = false;		for (int j = i; j<=k; j++){			if (!isInsertedBase(j))				sequencePanel.exportDeselectConsensusPosition(getConsensusFromLocalIndex(j));		}	}	private void deselectAllWithExport(){		for (int i=0;i<selected.length;i++)			selected[i] = false;		sequencePanel.exportDeselectAll();	}	public void selectAndFocusConsensusPosition(int ic){		contigDisplay.setFirstTouchedOverall(contigDisplay.getOverallBaseFromConsensusBase(ic));		deselectAllWithExport();		selectConsensusRange(ic, ic);		contigDisplay.repaintPanels();	}	public void selectAndFocusLocalPosition(int ic){		contigDisplay.setFirstTouchedOverall(contigDisplay.getOverallBaseFromConsensusBase(ic));		localFirstTouched = ic;		deselectAllWithExport();		selectLocalRange(ic, ic);		contigDisplay.repaintPanels();	}	boolean isInsertedBase(int iloc){		return getConsensusFromLocalIndex(iloc) == getConsensusFromLocalIndex(iloc-1);	}	int localFirstTouched = MesquiteInteger.unassigned;	int localSecondTouched = MesquiteInteger.unassigned;	int firstConsensusTouched = MesquiteInteger.unassigned;	boolean doubleClicked = false;	int consensusDragged=MesquiteInteger.unassigned;	/* to be used by subclasses to tell that panel touched */	public void mouseDown (int modifiers, int clickCount, long when, int x, int y, MesquiteTool tool) {		MesquiteInteger consensusPos = new MesquiteInteger();		ChromatogramTool chromTool = (ChromatogramTool)tool;		int iloc = findLocalPosition(x, consensusPos, chromTool.getIsInBetween());		int ic = consensusPos.getValue();		firstConsensusTouched = ic;		consensusDragged=MesquiteInteger.unassigned;				doubleClicked = clickCount>1;		if (!tool.isArrowTool() && ((chromTool.getWorksOnEditableSequencePanel() && getEditable()) || (chromTool.getWorksOnOtherSequencePanels() && !getEditable()))){			((ChromatogramTool)tool).touched(ic, iloc, false, id, contigID, modifiers);			return;		}		if (MesquiteEvent.shiftKeyDown(modifiers)){				if (MesquiteInteger.isCombinable(iloc)){				if (!MesquiteInteger.isCombinable(ic))					ic = getConsensusFromLocalIndex(iloc);				contigDisplay.setSecondTouchedOverall(contigDisplay.getOverallBaseFromConsensusBase(getConsensusFromLocalIndex(iloc)));				deselectAllWithExport();				localSecondTouched = iloc;				if (!MesquiteInteger.isCombinable(localFirstTouched))					localFirstTouched = getLocalIndexFromConsensus(contigDisplay.getFirstTouchedConsensus());				if (MesquiteInteger.isCombinable(localFirstTouched)) {					if (localFirstTouched>iloc){						selectLocalRange(iloc, localFirstTouched);					}					else {						selectLocalRange(localFirstTouched, iloc);					}				}				else {					selectLocalRange(iloc, iloc);				}				contigDisplay.repaintPanels();			}		}		else if (MesquiteEvent.commandOrControlKeyDown(modifiers)){			if (MesquiteInteger.isCombinable(iloc)){				selectLocalRange(iloc, iloc);				contigDisplay.repaintPanels();			}		}		else {			if (MesquiteInteger.isCombinable(iloc)){				contigDisplay.setFirstTouchedOverall(contigDisplay.getOverallBaseFromConsensusBase( getConsensusFromLocalIndex(iloc)));				deselectAllWithExport();				selectLocalRange(iloc, iloc);				localFirstTouched = iloc;							//	if ((clickCount>1 || sequencePanel.getScrollToTouched()) && MesquiteInteger.isCombinable(ic)){					//window.scrollToConsensusBase(ic);					//window.deselectAllReads();			//	}				contigDisplay.repaintPanels();				requestFocus();			}		}	}	public void mouseDrag (int modifiers, int x, int y, MesquiteTool tool) {		ChromatogramTool chromTool = (ChromatogramTool)tool;		if (!tool.isArrowTool() && ((chromTool.getWorksOnEditableSequencePanel() && getEditable()) || (chromTool.getWorksOnOtherSequencePanels() && !getEditable()))){			MesquiteInteger consensusPos = new MesquiteInteger();			int iloc = findLocalPosition(x, consensusPos, ((ChromatogramTool)tool).getIsInBetween());			int ic = consensusPos.getValue();			((ChromatogramTool)tool).dragged(ic, iloc, false, id, contigID, modifiers);			return;		}		MesquiteInteger consensusPos = new MesquiteInteger();		int iloc = findLocalPosition(x, consensusPos, ((ChromatogramTool)tool).getIsInBetween());		consensusDragged = consensusPos.getValue();		if (MesquiteInteger.isCombinable(consensusDragged)){			if (!MesquiteInteger.isCombinable(localFirstTouched))				localFirstTouched = getLocalIndexFromConsensus(contigDisplay.getFirstTouchedConsensus());			if (!MesquiteInteger.isCombinable(localSecondTouched))				localSecondTouched = getLocalIndexFromConsensus(contigDisplay.getSecondTouchedConsensus());			//deselectAll(); //this isn't correct behaviour!  If shift down should remember previously sleected pieces			if (MesquiteInteger.isCombinable(localFirstTouched)) {				if (localFirstTouched>iloc){					if (MesquiteInteger.isCombinable(localSecondTouched) && localSecondTouched<localFirstTouched && iloc>localSecondTouched){ //retracting						deselectLocalRange(localSecondTouched, iloc);					}					else 						selectLocalRange(iloc, localFirstTouched);				}				else {					if (MesquiteInteger.isCombinable(localSecondTouched) && localSecondTouched>localFirstTouched && iloc<localSecondTouched){ //retracting						deselectLocalRange(iloc+1, localSecondTouched);					}					else 						selectLocalRange(localFirstTouched, iloc);				}			}			else {				selectLocalRange(iloc, iloc);			}			contigDisplay.repaintPanels();			contigDisplay.setSecondTouchedOverall(contigDisplay.getOverallBaseFromConsensusBase( getConsensusFromLocalIndex(iloc)));			localSecondTouched = iloc;		}	}	/* to be used by subclasses to tell that panel touched */	public void mouseUp(int modifiers, int x, int y, MesquiteTool tool) {		ChromatogramTool chromTool = (ChromatogramTool)tool;		if (!tool.isArrowTool() && ((chromTool.getWorksOnEditableSequencePanel() && getEditable()) || (chromTool.getWorksOnOtherSequencePanels() && !getEditable()))){			MesquiteInteger consensusPos = new MesquiteInteger();			int iloc = findLocalPosition(x, consensusPos, ((ChromatogramTool)tool).getIsInBetween());			int ic = consensusPos.getValue();			((ChromatogramTool)tool).dropped(ic, iloc, false, id, contigID, modifiers);			return;		}		if (MesquiteInteger.isCombinable(contigDisplay.getFirstTouchedConsensus())){			if (!MesquiteInteger.isCombinable(contigDisplay.getSecondTouchedConsensus()))				contigDisplay.focusMatrixOn(contigDisplay.getFirstTouchedConsensus(), MesquiteInteger.unassigned);			else				contigDisplay.focusMatrixOn(contigDisplay.getFirstTouchedConsensus(), contigDisplay.getSecondTouchedConsensus());														if ((doubleClicked || sequencePanel.getScrollToTouched()) && (localFirstTouched == consensusDragged)){					contigDisplay.scrollToConsensusBase(contigDisplay.getFirstTouchedConsensus());					//window.deselectAllReads();				}								}		contigDisplay.setSecondTouchedOverall(MesquiteInteger.unassigned);		localSecondTouched = MesquiteInteger.unassigned;		firstConsensusTouched = MesquiteInteger.unassigned;	}	/*...............................................................................................................*/	public void setCurrentCursor(int modifiers, int x, int y, ChromatogramTool tool) {		if (tool == null)			setCursor(getDisabledCursor());		else if ((tool.getWorksOnEditableSequencePanel() && getEditable()) || (tool.getWorksOnOtherSequencePanels() && !getEditable()))			setCursor(tool.getCursor());		else			setCursor(getDisabledCursor());	}	/*_________________________________________________*/	public void mouseMoved(int modifiers, int x, int y, MesquiteTool tool) {		if (tool == null)			return;		MesquiteInteger consensusPos = new MesquiteInteger();		int iloc = findLocalPosition(x, consensusPos, ((ChromatogramTool)tool).getIsInBetween());		int ic = consensusPos.getValue();		String s = getSequenceExplanation(ic);		contigDisplay.setExplanation( s);	}	/*...............................................................................................................*/	public abstract String getSequenceExplanation (int consensusBase);	/*...............................................................................................................*/	public void mouseExited(int modifiers, int x, int y, MesquiteTool tool) {		setCursor(Cursor.getDefaultCursor());	}	/*...............................................................................................................*/	public void mouseEntered(int modifiers, int x, int y, MesquiteTool tool) {		if (tool == null)			return;		setCurrentCursor(modifiers, x, y, (ChromatogramTool)tool);	}}