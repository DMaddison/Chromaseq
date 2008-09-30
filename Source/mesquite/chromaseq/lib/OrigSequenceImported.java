/* Mesquite chromaseq source code.  Copyright 2005-2008 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.lib; import mesquite.categ.lib.*;import mesquite.cont.lib.*;import mesquite.lib.ColorDistribution;import mesquite.lib.Debugg;import java.awt.*;public class OrigSequenceImported extends MatrixSequence { 	/*.................................................................................................................*/	public  OrigSequenceImported (MolecularData edited, MolecularData original, ContinuousData quality, Contig contig, int it){		super( edited,  original,  quality, contig,  it);		data = original;	}	public Color getHighlightColor(int iSequence, int iConsensus){		return null;	}	public Color getQualityColorOfBase(int i){  // using index of local sequence 		int qual = getQualityOfBase(i); // using index of local sequence 		if (qual==0)			return ColorDistribution.brighter(AceFile.getColorOfQuality(qual),0.2);		else			return ColorDistribution.brighter(AceFile.getColorOfQuality(qual),0.5);	}	public boolean inapplicableInSourceMatrixIndex(int ic, int it){		return chromMapper.originalIsInapplicable(ic, it);	}	/*.................................................................................*/	public int sequenceBaseFromMatrixBase(int iMatrix){   // iMatrix is base in the edited matrix.		if (chromMapper!=null) {			int it = taxon.getNumber();			int originalBase = chromMapper.getOriginalPositionFromEditedMatrix(iMatrix, it);  			if (originalBase<0) {				return -1;			}			else if (originalBase>=original.getNumChars())				return original.getNumChars()-1;			else if (originalBase>=positionInSequence.length)				return positionInSequence.length-1; //return positionInSequence[positionInSequence.length-1];			else				return originalBase; //return positionInSequence[originalBase];		}		if (iMatrix<0)			return positionInSequence[0];		if (iMatrix>=positionInSequence.length)			return positionInSequence[positionInSequence.length-1];		return positionInSequence[iMatrix];	}	/*.................................................................................*/	public int matrixBaseFromSequenceBase(int iSequence){		int pos =0;		if (iSequence<0)			pos=0;		else if (iSequence>=positionInSourceMatrix.length)			pos=positionInSourceMatrix.length-1;		int originalPos = positionInSourceMatrix[pos];		if (chromMapper!=null) {			int it = taxon.getNumber();			return chromMapper.getEditedMatrixPositionFromOriginal(originalPos, it);		} 		if (iSequence<0)			return positionInSourceMatrix[0]+iSequence;		if (iSequence>=positionInSourceMatrix.length)			return positionInSourceMatrix[positionInSourceMatrix.length-1]+iSequence-positionInSourceMatrix.length+1;		return positionInSourceMatrix[iSequence];	}}