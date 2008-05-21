/* Mesquite chromaseq source code.  Copyright 2005-2007 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.lib; import mesquite.categ.lib.*;import mesquite.cont.lib.*;import mesquite.lib.Associable;import mesquite.lib.ColorDistribution;import mesquite.lib.Debugg;import mesquite.lib.MesquiteInteger;import mesquite.lib.MesquiteLong;import mesquite.lib.NameReference;import java.awt.*;public class EditedMatrixSequence extends MatrixSequence { 	/*.................................................................................................................*/	public  EditedMatrixSequence (MolecularData edited, MolecularData original, ContinuousData quality, Contig contig, int it){		super(edited, original, quality,contig,  it);		this.data = edited;	}	public Color getHighlightColor(int iSequence, int iConsensus){			if (edited ==  null || original == null)			return null;		int ic = matrixBaseFromSequenceBase(iSequence);		if (edited.getState(ic, it) != chromMapper.getOriginalState(ic, it))			return Color.black;		return null;	}	NameReference trimmableNameRef = NameReference.getNameReference("trimmable");	private int getFlag(int ic, int it){		Object obj = edited.getCellObject(trimmableNameRef, ic, it);		if (obj != null && obj instanceof MesquiteInteger)			return ((MesquiteInteger)obj).getValue();		return MesquiteInteger.unassigned;	}	public Color getStandardColorOfBase(int i){		if (edited ==  null || original == null)			return null;		int ic = matrixBaseFromSequenceBase(i);		int color = getFlag(ic, it);		if (color == 1) //trimmable		return AceFile.colorTrimmable.getColor();		else if (color == 2) //touched			return AceFile.colorTouched.getColor();		else if (color == 3) //please check			return AceFile.colorPleaseRecheck.getColor();		if (CategoricalState.hasMultipleStates(edited.getState(ic, it)))			return Color.lightGray;		return null;	}	public Color getQualityColorOfBase(int i){		if (edited ==  null || original == null)			return null;		Color color = getStandardColorOfBase(i);		if (color!= null)			return color;		int qual = getQualityOfBase(i); // using index of local sequence 		if (qual==0)			return ColorDistribution.brighter(AceFile.getColorOfQuality(qual),0.2);		else			return ColorDistribution.brighter(AceFile.getColorOfQuality(qual),0.5);	}	public boolean inapplicableInSourceMatrixIndex(int ic, int it){		return edited.isInapplicable(ic, it) && chromMapper.originalIsInapplicable(ic, it);	}}