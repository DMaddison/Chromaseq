/* Mesquite chromaseq source code.  Copyright 2005-2008 D. Maddison and W. Maddison.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
package mesquite.chromaseq.ManageChromaseqBlock;


import mesquite.lib.*;
import mesquite.lib.duties.*;

public class ManageChromaseqBlock extends FileInit {
	
	public static final int CHROMASEQBLOCKVERSION = 2;

	int numBlocks =0;
	public Class getDutyClass(){
		return ManageChromaseqBlock.class;
	}
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		return true;
	}
	/*.................................................................................................................*/
	public boolean isSubstantive(){
		return true;
	}
	public boolean isPrerelease(){
		return true;
	}
	/*.................................................................................................................*
  	 public Snapshot getSnapshot(MesquiteFile file) { 
   	 	Snapshot temp = new Snapshot();
  	 	temp.addLine("addAuthorNameToMatrices");
 	 	return temp;
  	 }
	/*.................................................................................................................*/
	/** A method called immediately after the file has been read in or completely set up (if a new file).*/
	public void fileReadIn(MesquiteFile f) {
		if (f== null || f.getProject() == null)
			return;
		NexusBlock[] bs = getProject().getNexusBlocks(ChromaseqBlock.class, f); 
		if ((bs == null || bs.length ==0)){
			ChromaseqBlock ab = new ChromaseqBlock(f, this);
			ab.setVersion(CHROMASEQBLOCKVERSION);
			numBlocks++;
			addNEXUSBlock(ab);
		}
		MesquiteTrunk.resetMenuItemEnabling();
	}
	/*.................................................................................................................*/
	public NexusBlockTest getNexusBlockTest(){ return new ChromaseqBlockTest();}
	/*.................................................................................................................*/
	public NexusBlock readNexusBlock(MesquiteFile file, String name, FileBlock block, StringBuffer blockComments, String fileReadingArguments){

		String commandString;
		NexusBlock b=new ChromaseqBlock(file, this);
		((ChromaseqBlock)b).setVersion(CHROMASEQBLOCKVERSION);
		MesquiteString comment = new MesquiteString();

		while (!StringUtil.blank(commandString = block.getNextFileCommand(comment))) {
			String commandName = parser.getFirstToken(commandString);

			if (commandName.equalsIgnoreCase("VERSION")) {
				String token  = parser.getNextToken();
				
				Debugg.println ("ChromseqBlock version: " + token);

			}
		}
		return b;
	}

	/*.................................................................................................................*/
	public String getName() {
		return "Manage CHROMASEQ blocks";
	}

	/*.................................................................................................................*/
	/** returns an explanation of what the module does.*/
	public String getExplanation() {
		return "Manages CHROMASEQ block in NEXUS file." ;
	}
	/*.................................................................................................................*/
	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer
	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.
	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/
	public int getVersionOfFirstRelease(){
		return NEXTRELEASE;  
	}
}



/* ======================================================================== */
class ChromaseqBlockTest extends NexusBlockTest  {
	public ChromaseqBlockTest () {
	}
	public  boolean readsWritesBlock(String blockName, FileBlock block){ //returns whether or not can deal with block
		return blockName.equalsIgnoreCase("CHROMASEQ");
	}
}

/* ======================================================================== */
class ChromaseqBlock extends NexusBlock {
	int version = 1;

	public ChromaseqBlock(MesquiteFile f, ManageChromaseqBlock mb){
		super(f, mb);
		version = mb.CHROMASEQBLOCKVERSION;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public boolean contains(FileElement e) {
		return false;  
	}

	public void written() {
	}
	public String getName(){
		return "CHROMASEQ block";
	}
	public boolean mustBeAfter(NexusBlock block){
		return false;
	}
	public String getBlockName(){
		return "CHROMASEQ";
	}
	public String getNEXUSBlock(){
		String blocks="BEGIN CHROMASEQ;" + StringUtil.lineEnding();
		blocks += "\tVERSION " + version+ ";" + StringUtil.lineEnding();
		blocks += "END;" + StringUtil.lineEnding();
		return blocks;
	}
}
