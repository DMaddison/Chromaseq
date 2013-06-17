/* Mesquite Chromaseq source code.  Copyright 2005-2011 David Maddison and Wayne Maddison.Version 1.0   December 2011Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.TaxonListVoucherCode;/*~~  */import mesquite.lists.lib.*;import mesquite.lib.characters.*;import mesquite.lib.*;import mesquite.lib.table.*;import mesquite.chromaseq.lib.*;/* ======================================================================== */public class TaxonListVoucherCode extends TaxonListAssistant {	Taxa taxa;	MesquiteTable table=null;	VoucherInfoCoord voucherInfoTask;	MesquiteMenuItemSpec msSetID, fd, wpmMI;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		//temporary		voucherInfoTask = (VoucherInfoCoord)hireEmployee(VoucherInfoCoord.class, null);		return true;	}	//temporary	public String getExplanationForRow(int ic){		if (taxa!=null && voucherInfoTask != null) {			VoucherInfo vi= voucherInfoTask.getVoucherInfo(ChromaseqUtil.getStringAssociated(taxa, ChromaseqUtil.voucherDBRef, ic), ChromaseqUtil.getStringAssociated(taxa, ChromaseqUtil.voucherCodeRef, ic));			if (vi != null)				return vi.toGenBankString();		}		return null;	}			/*.................................................................................................................*/	public void setTableAndTaxa(MesquiteTable table, Taxa taxa){		if (this.taxa != null)			this.taxa.removeListener(this);		this.taxa = taxa;		if (this.taxa != null)			this.taxa.addListener(this);		this.table = table;		deleteMenuItem(msSetID);		msSetID = addMenuItem("Get Voucher ID from last token of taxon name", makeCommand("setIDFromName", this));		fd = addMenuItem("Select taxa with duplicate IDs", makeCommand("selectDuplicates", this));//		wpmMI = addMenuItem("Find Voucher ID in taxon name (WPM Lab Only)", makeCommand("findWPMID", this));}	/*.................................................................................................................*/	public void dispose() {		super.dispose();		if (taxa!=null)			taxa.removeListener(this);	}	/*.................................................................................................................*/	private void setIDFromTaxonName(){		if (table !=null && taxa!=null) {			boolean changed=false;			String id = "";			Parser parser = new Parser();			if (employer!=null && employer instanceof ListModule) {				int c = ((ListModule)employer).getMyColumn(this);				for (int i=0; i<taxa.getNumTaxa(); i++) {					if (table.isCellSelectedAnyWay(c, i)) {						id = taxa.getName(i);						parser.setString(id);						id = parser.getLastToken();						if (!StringUtil.blank(id)){							ChromaseqUtil.setStringAssociated(taxa,ChromaseqUtil.voucherCodeRef, i, id);							if (!changed)								outputInvalid();							changed = true;						}					}				}			}//			if (changed)//				data.notifyListeners(this, new Notification(MesquiteListener.NAMES_CHANGED)); //TODO: bogus! should notify via specs not data???			outputInvalid();			parametersChanged();		}	}	/*.................................................................................................................*/	private void selectDuplicates(){		if (table !=null && taxa!=null) {			boolean changed=false;			if (employer!=null && employer instanceof ListModule) {				int c = ((ListModule)employer).getMyColumn(this);				for (int i=0; i<taxa.getNumTaxa(); i++) {					String target = ChromaseqUtil.getStringAssociated(taxa,ChromaseqUtil.voucherCodeRef, i);										if (!StringUtil.blank(target))						for (int k=i+1; k<taxa.getNumTaxa(); k++) {						String id = ChromaseqUtil.getStringAssociated(taxa,ChromaseqUtil.voucherCodeRef, k);												if (!StringUtil.blank(id)) {							if (id.equalsIgnoreCase(target)){								if (!taxa.getSelected(i))									taxa.setSelected(i, true);								if (!taxa.getSelected(k))									taxa.setSelected(k, true);								changed = true;							}													}					}				}			}		if (changed)				taxa.notifyListeners(this, new Notification(MesquiteListener.SELECTION_CHANGED));			outputInvalid();			parametersChanged();		}	}	/*.................................................................................................................*/	private void findIDInTaxonNameWPM(){		if (table !=null && taxa!=null) {			boolean changed=false;			if (employer!=null && employer instanceof ListModule) {				int c = ((ListModule)employer).getMyColumn(this);				for (int i=0; i<taxa.getNumTaxa(); i++) {					if (table.isCellSelectedAnyWay(c, i)) {						String id = "";						String name = taxa.getName(i);						if (name.indexOf("JXZ") >=0)							id = name.substring(name.indexOf("JXZ"), name.indexOf("JXZ")+6);						else if (name.indexOf("MRB") >=0)							id = name.substring(name.indexOf("MRB"), name.indexOf("MRB")+6);						else if (name.indexOf(".DNA") >=0)							id = name.substring(name.indexOf(".DNA")+1, name.indexOf(".DNA")+8);						else if (name.indexOf(".d") >=0)							id = name.substring(name.indexOf(".d")+1, name.indexOf(".d")+5);						else if (name.indexOf(".s") >=0)							id = name.substring(name.indexOf(".s")+1, name.indexOf(".s")+5);						else if (name.indexOf(".S") >=0){							int st = name.indexOf(".S");							int lg = name.length();							if (st+5<= lg)								id = name.substring(name.indexOf(".S")+1, name.indexOf(".S")+5);							else if (st+4<= lg)								id = name.substring(name.indexOf(".S")+1, name.indexOf(".S")+4);							else if (st+3<= lg)								id = name.substring(name.indexOf(".S")+1, name.indexOf(".S")+3);							else if (st+2<= lg)								id = name.substring(name.indexOf(".S")+1, name.indexOf(".S")+2);						}						else if (name.indexOf(".GR") >=0)							id = name.substring(name.indexOf(".GR")+1, name.indexOf(".GR")+6);						else if (name.indexOf("d0") >=0)							id = name.substring(name.indexOf("d0"), name.indexOf("d0")+4);						else if (name.indexOf("d1") >=0)							id = name.substring(name.indexOf("d1"), name.indexOf("d1")+4);						else if (name.indexOf("d2") >=0)							id = name.substring(name.indexOf("d2"), name.indexOf("d2")+4);						else if (name.indexOf("d3") >=0)							id = name.substring(name.indexOf("d3"), name.indexOf("d3")+4);						else if (name.indexOf("d4") >=0)							id = name.substring(name.indexOf("d4"), name.indexOf("d4")+4);												if (!StringUtil.blank(id)){							ChromaseqUtil.setStringAssociated(taxa,ChromaseqUtil.voucherCodeRef, i, id);							if (!changed)								outputInvalid();							changed = true;						}					}				}			}//			if (changed)//				data.notifyListeners(this, new Notification(MesquiteListener.NAMES_CHANGED)); //TODO: bogus! should notify via specs not data???			outputInvalid();			parametersChanged();		}	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the Voucher ID to be the last token of the taxon name", null, commandName, "setIDFromName")) {			setIDFromTaxonName();			}		else if (checker.compare(this.getClass(), "Finds the Voucher ID in the taxon name (WPM lab only)", null, commandName, "findWPMID")) {			findIDInTaxonNameWPM();			}		else if (checker.compare(this.getClass(), "Selects taxa with duplicate Names", null, commandName, "selectDuplicates")) {			selectDuplicates();			}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}	public void changed(Object caller, Object obj, Notification notification){		outputInvalid();		parametersChanged(notification);	}	public String getTitle() {		return "Voucher ID";	}	public String getStringForTaxon(int ic){				if (taxa!=null) {			Object n = ChromaseqUtil.getStringAssociated(taxa, ChromaseqUtil.voucherCodeRef, ic);			if (n !=null)				return ((String)n);					}		return "-";	}	/*...............................................................................................................*/	/** returns whether or not a cell of table is editable.*/	public boolean isCellEditable(int row){		return true;	}	/*...............................................................................................................*/	/** for those permitting editing, indicates user has edited to incoming string.*/	public void setString(int row, String s){		if (taxa!=null) {			ChromaseqUtil.setStringAssociated(taxa,ChromaseqUtil.voucherCodeRef, row, s);		}			}	public boolean useString(int ic){		return true;	}		public String getWidestString(){		return "88888888888888888  ";	}	/*.................................................................................................................*/	public String getName() {		return "Voucher ID Code";	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;  	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return -1;  	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}		/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Lists the voucher ID code for a taxon." ;	}}