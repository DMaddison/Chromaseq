/* Mesquite chromaseq source code.  Copyright 2005-2007 D. Maddison and W. Maddison.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.chromaseq.lib; import java.io.*;public class SCFChromatogram extends StandardChromatogram{// private variablesprivate int sampleOffset, basesOffset, commentsOffset, commentsSize, sampleSize;private int bytesRead, baseNum, codeSet, sampleNum;private String versionString;private float versionNum;private DataInputStream filterIN;// public variablespublic static final int MagicNum = (((((int)'.'<<8)+(int)'s'<<8)+(int)'c'<<8)+(int)'f');private String comments;	public  SCFChromatogram(InputStream in, Read read) throws IOException {		readChromatogram(in);		this.read = read;	}public void write(OutputStream OUT) {	// yet to be written}public void readChromatogram(InputStream in) throws IOException{		filterIN = new DataInputStream(in);		bytesRead = 0;		filterIN.skipBytes(4);		sampleNum = filterIN.readInt();		sampleOffset = filterIN.readInt();		baseNum = filterIN.readInt();		filterIN.skip(8);		basesOffset = filterIN.readInt();		commentsSize = filterIN.readInt();		commentsOffset = filterIN.readInt();		int i;		versionString = "";		for (i=0;i<4;i++) {		versionString += (char) filterIN.readUnsignedByte();	}		versionNum = Float.valueOf(versionString).floatValue();		sampleSize = filterIN.readInt();		codeSet = filterIN.readInt();		bytesRead = 48;				// determine order of file components and then read them		if ((basesOffset < sampleOffset) && (basesOffset < commentsOffset)) {			readBases();			if (sampleOffset < commentsOffset) {				readSample();				readComments();			}			else {				readComments();				readSample();			}		}		else if (sampleOffset < commentsOffset) {			readSample();			if (basesOffset < commentsOffset) {				readBases();				readComments();			}			else {				readComments();				readBases();			}		}		else {			readComments();			if (sampleOffset < basesOffset) {				readSample();				readBases();			}			else {				readBases();				readSample();			}		}	}	private void readBases() throws IOException{	filterIN.skipBytes(basesOffset - bytesRead);	int i;	basePosition = new int[baseNum];	base = new char[baseNum];	if (versionNum < 3.0) {		for (i=0;i<baseNum;i++) {			basePosition[i] = filterIN.readInt();			filterIN.skipBytes(4);			base[i] = (char) filterIN.readUnsignedByte();			filterIN.skipBytes(3);		}	}	else {		for (i=0;i<baseNum;i++) {			basePosition[i] = filterIN.readInt();		}		filterIN.skipBytes(4 * baseNum);		for (i=0;i<baseNum;i++) {			base[i] = (char) filterIN.readUnsignedByte();		}		filterIN.skipBytes(3 * baseNum);	}	baseSequence = String.valueOf(base);	bytesRead = basesOffset + baseNum * 12;}private void readSample() throws IOException {		filterIN.skipBytes(sampleOffset - bytesRead);		A = new int[sampleNum];		C = new int[sampleNum];		G = new int[sampleNum];		T = new int[sampleNum];		int i;		if ((sampleSize == 2) && (versionNum < 3.0)) {		for (i=0;i<sampleNum;i++) {			A[i] = filterIN.readUnsignedShort();			C[i] = filterIN.readUnsignedShort();			G[i] = filterIN.readUnsignedShort();			T[i] = filterIN.readUnsignedShort();		}	}	else if ((sampleSize == 1) && (versionNum < 3.0)) {		for (i=0;i<sampleNum;i++) {			A[i] = filterIN.readUnsignedByte();			C[i] = filterIN.readUnsignedByte();			G[i] = filterIN.readUnsignedByte();			T[i] = filterIN.readUnsignedByte();		}	}	else if ((sampleSize == 2) && (versionNum >= 3.0)) {		for (i=0;i<sampleNum;i++) {			A[i] = filterIN.readShort();		}		A = deltaDelta(A);		for (i=0;i<sampleNum;i++) {			C[i] = filterIN.readShort();		}		C = deltaDelta(C);		for (i=0;i<sampleNum;i++) {			G[i] = filterIN.readShort();		}		G = deltaDelta(G);		for (i=0;i<sampleNum;i++) {			T[i] = filterIN.readShort();		}		T = deltaDelta(T);			}	else if ((sampleSize == 1) && (versionNum >= 3.0)) {		for (i=0;i<sampleNum;i++) {			A[i] = filterIN.readByte();		}		A = deltaDelta(A);		for (i=0;i<sampleNum;i++) {			C[i] = filterIN.readByte();		}		C = deltaDelta(C);		for (i=0;i<sampleNum;i++) {			G[i] = filterIN.readByte();		}		G = deltaDelta(G);		for (i=0;i<sampleNum;i++) {			T[i] = filterIN.readByte();		}		T = deltaDelta(T);	}		bytesRead = sampleOffset + sampleNum * sampleSize * 4;	}	private void readComments() throws IOException {		int i;		char[] cArray = new char[commentsSize];		filterIN.skipBytes(commentsOffset - bytesRead);		for (i=0;i<commentsSize;i++) {			cArray[i] = (char) filterIN.readUnsignedByte();		}		comments = String.valueOf(cArray);		bytesRead = commentsOffset + commentsSize;	}	private int[] deltaDelta(int[] theArray)	{	int i, pSample = 0;	for (i=0;i<theArray.length-1;i++) {		theArray[i] = theArray[i] + pSample;		pSample = theArray[i];	}	pSample = 0;	for (i=0;i<theArray.length-1;i++) {		theArray[i] = theArray[i] + pSample;		pSample = theArray[i];	}	return theArray;}			public static boolean isSCF(File aFile) throws FileNotFoundException, IOException{		FileInputStream fileIN = new FileInputStream(aFile);		DataInputStream filtIN = new DataInputStream(fileIN);		if (filtIN.readInt() == MagicNum) return true;		else return false;}public int getCodeSet() {	return codeSet;}public int getTraceLength() {	return sampleNum;}public String getVersion() {	return versionString;}public String getComments() {	return comments;}}