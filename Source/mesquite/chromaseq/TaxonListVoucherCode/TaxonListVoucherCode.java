/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.  Version 1.11, June 2006. Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code.  The commenting leaves much to be desired. Please approach this source code with the spirit of helping out. Perhaps with your help we can be more than a few, and make Mesquite better.  Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY. Mesquite's web site is http://mesquiteproject.org  This source code and its compiled class files are free and modifiable under the terms of  GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.TaxonListVoucherCode;/*~~  */import mesquite.lists.lib.*;import mesquite.lib.*;import mesquite.lib.table.*;import mesquite.chromaseq.lib.*;/* ======================================================================== */public class TaxonListVoucherCode extends TaxonListAssistant {	Taxa taxa;	MesquiteTable table=null;	NameReference anr = NameReference.getNameReference("VoucherCode");	//temporary	NameReference vdb = NameReference.getNameReference("VoucherDB");	VoucherInfoCoord voucherInfoTask;	MesquiteMenuItemSpec msSetID;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		//temporary		voucherInfoTask = (VoucherInfoCoord)hireEmployee(commandRec, VoucherInfoCoord.class, null);		return true;	}	//temporary	public String getExplanationForRow(int ic){		if (taxa!=null && voucherInfoTask != null) {			VoucherInfo vi= voucherInfoTask.getVoucherInfo((String)taxa.getAssociatedObject(vdb, ic), (String)taxa.getAssociatedObject(anr, ic));			if (vi != null)				return vi.toGenBankString();		}		return null;	}			/*.................................................................................................................*/	public void setTableAndTaxa(MesquiteTable table, Taxa taxa, CommandRecord commandRec){		if (this.taxa != null)			this.taxa.removeListener(this);		this.taxa = taxa;		if (this.taxa != null)			this.taxa.addListener(this);		this.table = table;		deleteMenuItem(msSetID);		msSetID = addMenuItem("Get Voucher ID from last token of taxon name", makeCommand("setIDFromName", this));}	/*.................................................................................................................*/	private void setIDFromTaxonName(CommandRecord commandRec){		if (table !=null && taxa!=null) {			boolean changed=false;			String id = "";			Parser parser = new Parser();			if (employer!=null && employer instanceof ListModule) {				int c = ((ListModule)employer).getMyColumn(this);				for (int i=0; i<taxa.getNumTaxa(); i++) {					if (table.isCellSelectedAnyWay(c, i)) {						id = taxa.getName(i);						parser.setString(id);						id = parser.getLastToken();						if (!StringUtil.blank(id)){							taxa.setAssociatedObject(anr, i, id);							if (!changed)								outputInvalid(commandRec);							changed = true;						}					}				}			}//			if (changed)//				data.notifyListeners(this, new Notification(MesquiteListener.NAMES_CHANGED), commandRec); //TODO: bogus! should notify via specs not data???			outputInvalid(commandRec);			parametersChanged(null, commandRec);		}	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the Voucher ID to be the last token of the taxon name", null, commandName, "setIDFromName")) {			setIDFromTaxonName(commandRec);			}		else			return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}	public void changed(Object caller, Object obj, Notification notification,  CommandRecord commandRec){		outputInvalid(commandRec);		parametersChanged(notification, commandRec);	}	public String getTitle() {		return "Voucher ID";	}	public String getStringForTaxon(int ic){				if (taxa!=null) {			Object n = taxa.getAssociatedObject(anr, ic);			if (n !=null)				return ((String)n);					}		return "-";	}	/*...............................................................................................................*/	/** returns whether or not a cell of table is editable.*/	public boolean isCellEditable(int row){		return true;	}	/*...............................................................................................................*/	/** for those permitting editing, indicates user has edited to incoming string.*/	public void setString(int row, String s){		if (taxa!=null) {			taxa.setAssociatedObject(anr, row, s);		}			}	public boolean useString(int ic){		return true;	}		public String getWidestString(){		return "88888888888888888  ";	}	/*.................................................................................................................*/	public String getName() {		return "Voucher ID Code";	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return true;  	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return -1;  	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}		/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Lists the voucher ID code for a taxon." ;	}}