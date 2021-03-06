/*
 * Created on 19.8.2004
 */
package is.idega.idegaweb.marathon.business;

import is.idega.idegaweb.marathon.data.Participant;
import is.idega.idegaweb.marathon.data.ParticipantHome;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.idega.block.importer.data.ImportFile;
import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.user.data.Group;

/**
 * @author laddi
 */
public class RunChipNumberImportBean extends IBOServiceBean implements RunChipNumberImport {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3891664108682263479L;
	private UserTransaction transaction;
	private ImportFile file;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.block.importer.business.ImportFileHandler#getFailedRecords()
	 */
	public List getFailedRecords() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.block.importer.business.ImportFileHandler#handleRecords()
	 */
	public boolean handleRecords() throws RemoteException {
		this.transaction = getSessionContext().getUserTransaction();
		try {
			this.transaction.begin();

			String item;
			int count = 1;
			String chipBunchNumber = this.file.getFile().getName();
			chipBunchNumber = chipBunchNumber.substring(chipBunchNumber.indexOf("_") + 1);

			ParticipantHome runHome = (ParticipantHome) IDOLookup.getHome(Participant.class);
			Collection runners = runHome.findAllWithoutChipNumber(126);
			if (!runners.isEmpty()) {
				int runnersCount = runners.size();
				Iterator iterator = runners.iterator();
				while (!(item = (String) this.file.getNextRecord()).equals("") && iterator.hasNext()) {
					Participant runner = (Participant) iterator.next();
					String chipNumber = this.file.getValueAtIndexFromRecordString(1, item);

					runner.setChipNumber(chipNumber);
					runner.setChipBunchNumber(chipBunchNumber);
					runner.store();

					System.out.println("Processed record number: " + (count++) + " of total runners: " + runnersCount);
				}
			}
			else {
				System.out.println("No runners without chip number...");
			}
			this.transaction.commit();
		}
		catch (Exception ex) {
			ex.printStackTrace();

			try {
				this.transaction.rollback();
			}
			catch (SystemException e) {
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public void log(String msg) {
		super.log("[Missing Names Import Handler] " + msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.block.importer.business.ImportFileHandler#setImportFile(com.idega.block.importer.data.ImportFile)
	 */
	public void setImportFile(ImportFile file) throws RemoteException {
		this.file = file;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.block.importer.business.ImportFileHandler#setRootGroup(com.idega.user.data.Group)
	 */
	public void setRootGroup(Group rootGroup) throws RemoteException {

	}
}