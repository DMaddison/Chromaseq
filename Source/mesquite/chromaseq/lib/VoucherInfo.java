/* Mesquite chromaseq source code.  Copyright 2005-2006 D. Maddison and W. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.chromaseq.lib; import mesquite.lib.*;/* ======================================================================== */public class  VoucherInfo {	protected String voucherID;	protected String species;	protected String latLong;	protected String locality;	protected String note;	protected String collectionDate;	protected String identifiedBy;	StringArray fieldNames, fieldValues;	boolean flexible = true;	public VoucherInfo(){		fieldNames = new StringArray(20);		fieldValues = new StringArray(20);	}	public VoucherInfo(String voucherID, String species, String latLong, String locality, String note, String collectionDate, String identifiedBy){		this.voucherID = voucherID;		this.species = species;		this.latLong = latLong;		this.locality = locality;		this.note = note;		this.collectionDate = collectionDate;		this.identifiedBy = identifiedBy;		flexible=false;	}	public void addElement(String fieldName, String fieldValue){		if (!flexible)			return;		if (StringUtil.blank(fieldName))			return;		if (fieldNames.getFilledSize()>=fieldNames.getSize()){   //add more elements if needed			fieldNames.addParts(fieldNames.getSize(), 1);			fieldValues.addParts(fieldValues.getSize(), 1);		}		fieldNames.setValue(fieldNames.getFilledSize(), fieldName);		if (!StringUtil.blank(fieldValue))			fieldValues.setValue(fieldValues.getFilledSize(), fieldValue);	}	public void addElement(String fieldName){		addElement(fieldName,null);	}	public void setFieldValue(int i, String fieldValue){		if (!flexible)			return;		if (StringUtil.blank(fieldValue))			return;		if (i>=fieldValues.getSize())			return;		fieldValues.setValue(i, fieldValue);	}	public String getVoucherID(){		return voucherID;	}	public String getSpecies(){		return species;	}	public String getLatLong(){		return latLong;	}	public String getLocality(){		return locality;	}	public String getNote(){		return note;	}	public String getCollectionDate(){		return collectionDate;	}	public String toString(){		if (flexible) {			String s = "";			for (int i=0; i<fieldNames.getFilledSize(); i++) {				s += fieldNames.getValue(i) + " " ;  						}			return "VoucherInfo: " +s;		}		else			return "VoucherInfo: " + voucherID + " " + species + " " + latLong + " " + locality + " " + note + " " + collectionDate;	}	public String toGenBankString(){		if (flexible) {			String s = "";			for (int i=0; i<fieldNames.getFilledSize(); i++) {				s += "[" + fieldNames.getValue(i) + " = " + fieldValues.getValue(i) + "] ";  						}			return s;		}		else {			String s = "";			if (!StringUtil.blank(species))				s += "[organism = " + species + "] ";			if (!StringUtil.blank(identifiedBy))				s += "[identified-by = " + identifiedBy + "] ";			if (!StringUtil.blank(voucherID))				s += "[specimen-voucher = " + voucherID + "] ";			if (!StringUtil.blank(locality))				s += "[country = " + locality + "] ";			if (!StringUtil.blank(latLong))				s += "[lat-lon = " + latLong + "] ";			if (!StringUtil.blank(collectionDate))				s += "[collection-date = " + collectionDate + "] ";			if (!StringUtil.blank(note))				s += "[note = " + note + "] ";			return s;		}	}}