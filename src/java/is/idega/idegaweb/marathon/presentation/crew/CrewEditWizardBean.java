package is.idega.idegaweb.marathon.presentation.crew;

import is.idega.idegaweb.marathon.IWBundleStarter;
import is.idega.idegaweb.marathon.business.RunBusiness;
import is.idega.idegaweb.marathon.data.Participant;

import java.rmi.RemoteException;
import java.util.List;

import javax.faces.context.FacesContext;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/01/10 18:56:26 $ by $Author: civilis $
 *
 */
public class CrewEditWizardBean {

	private Integer mode;
	public static final Integer newCrewMode = new Integer(1);
	public static final Integer editCrewMode = new Integer(2);
	private Integer crewEditId;
	private String participantId;
	private List crewMembersInvitationSearchResults;
	
	private Participant participant;
	private RunBusiness runBusiness;

	public Integer getCrewEditId() {
		return crewEditId;
	}

	public void setCrewEditId(Integer crewEditId) {
		this.crewEditId = crewEditId;
	}
	
	public Integer getMode() {
		return mode == null ? new Integer(0) : mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}
	
	public boolean isNewCrewMode() {
		
		return newCrewMode.equals(getMode());
	}
	
	public boolean isEditCrewMode() {
		
		return editCrewMode.equals(getMode());
	}
	
	public Participant getParticipant() {

		if(participant == null && getParticipantId() != null && !CoreConstants.EMPTY.equals(getParticipantId())) {
			
			try {
				participant = getRunBusiness().getParticipantByPrimaryKey(new Integer(getParticipantId()).intValue());
				
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		}
		
		return participant;
	}
	
	public void setParticipant(Participant participant) {
		crewMembersInvitationSearchResults = null;
		this.participant = participant;
	}
	
	public RunBusiness getRunBusiness() {
		
		if(runBusiness == null) {
			
			try {
				runBusiness = (RunBusiness) IBOLookup.getServiceInstance(IWContext.getIWContext(FacesContext.getCurrentInstance()), RunBusiness.class);
			} catch (IBOLookupException e) {
				throw new RuntimeException(e);
			}
		}
		
		return runBusiness;
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setParticipantId(String participantId) {
		participant = null;
		crewMembersInvitationSearchResults = null;
		this.participantId = participantId;
	}
	
	public String getRunLabel() {

		IWContext iwc = IWContext.getIWContext(FacesContext.getCurrentInstance());
		Participant participant = getParticipant();
		
		return iwc.getIWMainApplication().getBundle(IWBundleStarter.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc)	
		.getLocalizedString(participant.getRunTypeGroup().getName(), participant.getRunTypeGroup().getName()) + " " + participant.getRunYearGroup().getName();
	}

	public List getCrewMembersInvitationSearchResults() {
		return crewMembersInvitationSearchResults;
	}

	public void setCrewMembersInvitationSearchResults(
			List crewMembersInvitationSearchResults) {
		this.crewMembersInvitationSearchResults = crewMembersInvitationSearchResults;
	}
}