/* Mesquite chromaseq source code.  Copyright 2005-2007 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.lib; import java.io.*;public class TaggedRecord { /*   public static final int DATA_TYPE_ASCII_ARRAY = 2;    public static final int DATA_TYPE_INTEGER = 4;    public static final int DATA_TYPE_FLOAT   = 7;    public static final int DATA_TYPE_DATE    = 10;    public static final int DATA_TYPE_TIME    = 11;    public static final int DATA_TYPE_PSTRING = 18;*/		private String tagName;	private int tagNum, eNum, arrayLength, dataRecord, cryptic;	private short dataType, eLength;	private char[] tagArray;		public TaggedRecord() {}	public TaggedRecord(DataInput IN) throws IOException {		read(IN);}	public void read(DataInput filterIN) throws IOException {		int i;		char[] tagArray = new char[4];		for (i=0;i<4;i++) {		tagArray[i] += (char) (filterIN.readUnsignedByte());	}		tagName = new String(tagArray);		tagNum = filterIN.readInt();		dataType = filterIN.readShort();		eLength = filterIN.readShort();		eNum = filterIN.readInt();		arrayLength = filterIN.readInt();		dataRecord = filterIN.readInt();		cryptic = filterIN.readInt();			}	public String getTagName() {		return tagName;	}	public int getTagNum() {		return tagNum;	}	public short getDataType() {		return dataType;	}	public int getElementLength() {		return eLength;	}	public int getElementNumber() {		return eNum;	}	public int getArrayLength() {		return arrayLength;	}	public int getDataRecord() {		return dataRecord;	}     /**     * A very verbose <code>toString</code> that dumps all of the     * data in this record in a human-readable format.     */ /*  *    public String toString() {        StringBuffer sb = new StringBuffer(super.toString()).append("[\n");        sb.append("  tagName         = ").append(tagName).append('\n');        sb.append("  tagNumber       = ").append(tagNum).append('\n');        sb.append("  dataType        = ");        switch (dataType) {            case DATA_TYPE_ASCII_ARRAY: sb.append("ASCII"); break;            case DATA_TYPE_INTEGER: sb.append("INTEGER"); break;            case DATA_TYPE_FLOAT:   sb.append("FLOAT");   break;            case DATA_TYPE_DATE:    sb.append("DATE");    break;            case DATA_TYPE_TIME:    sb.append("TIME");    break;            case DATA_TYPE_PSTRING: sb.append("PSTRING"); break;            default: sb.append(dataType);        }        sb.append('\n');        sb.append("  elementLength   = ").append(eLength).append('\n');        sb.append("  numberOfElements= ").append(eNum).append('\n');        sb.append("  recordLength    = ").append(arrayLength).append('\n');        sb.append("  dataRecord      = ");        if (arrayLength <= 4) {            switch (dataType) {            case DATA_TYPE_ASCII_ARRAY:                if (eLength > 3)                    sb.append((char) ((dataRecord >>> 24) & 0xFF));                if (eLength > 2)                    sb.append((char) ((dataRecord >>> 16) & 0xFF));                if (eLength > 1)                    sb.append((char) ((dataRecord >>> 8 ) & 0xFF));                sb.append((char) ((dataRecord) & 0xFF));                break;            case DATA_TYPE_DATE:                sb.append((dataRecord >>> 16) & 0xffff).append('/');                sb.append((dataRecord >>> 8 ) & 0xff).append('/');                sb.append((dataRecord) & 0xff);                break;            case DATA_TYPE_TIME:                sb.append((dataRecord >>> 24) & 0xff).append(':');                sb.append((dataRecord >>> 16) & 0xff).append(':');                sb.append((dataRecord >>> 8 ) & 0xff);                break;            case DATA_TYPE_INTEGER:                sb.append(dataRecord >>> (4 - arrayLength)*8);                break;            default:                hexStringify((int)dataRecord, sb);            }        }        else {            hexStringify((int)dataRecord, sb);        }        sb.append('\n');        sb.append("  crypticVariable = ").append(cryptic).append('\n');        sb.append(']');        return sb.toString();    }    private void hexStringify(int l, StringBuffer sb) {        sb.append("0x");        String hex = Integer.toHexString(l).toUpperCase();        for (int i = 8 ; i > hex.length() ; i--)            sb.append('0');        sb.append(hex);    }    */}