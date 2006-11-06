package mesquite.chromaseq.DownloadAbiFromDb;

import mesquite.chromaseq.lib.AbiDownloader;
import mesquite.chromaseq.lib.AbiUploader;
import mesquite.lib.CommandChecker;
import mesquite.lib.CommandRecord;
import mesquite.lib.MesquiteMessage;
import mesquite.lib.duties.UtilitiesAssistant;

public class DownloadAbiFromDb extends UtilitiesAssistant {
	private static final String COMMAND_NAME = "downloadAbiFromDb";
	private static final String EXPLANATION = "Download abi files from a database and phred/phrap them.";

	/* ................................................................................................................. */
	public boolean startJob(String arguments, Object condition,
			CommandRecord commandRec, boolean hiredByName) {
		addMenuItem(null, "Run Phred/Phrap on database...", makeCommand(
				COMMAND_NAME, this));
		return true;
	}

	/* ................................................................................................................. */
	/** returns whether this module is requesting to appear as a primary choice */
	public boolean requestPrimaryChoice() {
		return true;
	}

	/* ................................................................................................................. */
	public boolean isPrerelease() {
		return true;
	}

	/* ................................................................................................................. */
	public boolean isSubstantive() {
		return false;
	}

	/* ................................................................................................................. */
	public Object doCommand(String commandName, String arguments,
			CommandRecord commandRec, CommandChecker checker) {
		if (checker.compare(this.getClass(),
				EXPLANATION, null,
				commandName, COMMAND_NAME)) {
			AbiDownloader downloader = (AbiDownloader) hireEmployee(commandRec,
					AbiDownloader.class, "Abi Downloader");
			if (downloader != null) {
				downloader.downloadAbiFilesFromDb(commandRec);
			} else {
				MesquiteMessage.warnProgrammer("Can't find ABI downloader module.");
			}
		} else {
			return super.doCommand(commandName, arguments, commandRec, checker);
		}
		return null;
	}

	/* ................................................................................................................. */
	public String getName() {
		return EXPLANATION;
	}

	/* ................................................................................................................. */
	public boolean showCitation() {
		return false;
	}

	/* ................................................................................................................. */
	public String getExplanation() {
		return EXPLANATION;
	}
}
