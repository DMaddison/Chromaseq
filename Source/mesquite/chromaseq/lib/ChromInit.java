/* Mesquite chromaseq source code.  Copyright 2005-2007 D. Maddison and W. Maddison.Version 2.01, December 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.lib; import mesquite.lib.*;import mesquite.categ.lib.*;import mesquite.cont.lib.*;import mesquite.lib.table.*;import java.util.*;/* ======================================================================== */public abstract class  ChromInit extends MesquiteModule {	Vector v;	protected CMTable table;	protected DNAData edited, originalData;	protected ContinuousData qualityData;	int numContexts = 0;   	 public Class getDutyClass() {   	 	return ChromInit.class;   	 } 	public String getDutyName() { 		return "Chromatogram Viewer INIT";   	 } 	public abstract void setWindow(MesquiteWindow w);   	public void addContext(Taxon taxon, Contig contig, ContigDisplay panel, Read[] reads, SequencePanel[] sequences, DNAData matrixData, DNAData originalData, ContinuousData qualityData, MesquiteTable table, int id){   		if (v == null)   			v = new Vector();   		this.table = (CMTable)table;   		this.edited = matrixData;   		this.qualityData = qualityData;   		this.originalData = originalData;   		v.addElement(new ChromViewContext(taxon, contig, panel, reads, sequences, id));     	}   	public ChromViewContext getContext(int id){   		for (int i = 0; i< v.size(); i++){   			ChromViewContext c = (ChromViewContext)v.elementAt(i);   			if (c.id == id)   				return c;   		}   		return null;   	}   	public ChromViewContext getContext(ContigDisplay panel){   		for (int i = 0; i< v.size(); i++){   			ChromViewContext c = (ChromViewContext)v.elementAt(i);   			if (c.id == panel.getID())   				return c;   		}   		return null;   	}  }