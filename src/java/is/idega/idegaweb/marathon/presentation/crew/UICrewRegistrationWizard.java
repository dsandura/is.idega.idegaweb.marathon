package is.idega.idegaweb.marathon.presentation.crew;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.idega.presentation.wizard.Wizard;
import com.idega.presentation.wizard.WizardStep;

/**
 * 
 * @author <a href="civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.4 $
 *
 * Last modified: $Date: 2008/01/10 18:56:26 $ by $Author: civilis $
 *
 */
public class UICrewRegistrationWizard extends Wizard {

	public static final String COMPONENT_TYPE = 									"idega_CrewRegistrationWizard";
	
	public static final String crewManageBeanExp = 									"#{crewManageBean}";
	public static final String crewManageBean_startNewCrewRegistrationExp = 		"#{crewManageBean.startNewCrewRegistration}";
	public static final String crewManageBean_wizardModeExp = 						"#{crewManageBean.wizardMode}";
	public static final String crewManageBean_crewManageHeaderValueExp = 			"#{crewManageBean.crewManageHeaderValue}";
	public static final String crewManageBean_crewLabelExp = 						"#{crewManageBean.crewLabel}";
	public static final String crewManageBean_runsExp = 							"#{crewManageBean.runs}";
	public static final String crewManageBean_validateRunSelectionExp = 			"#{crewManageBean.validateRunSelection}";
	public static final String crewManageBean_runIdExp =				 			"#{crewManageBean.runId}";
	public static final String crewManageBean_createCrewExp =						"#{crewManageBean.createCrew}";
	public static final String crewManageBean_editCrewExp =							"#{crewManageBean.editCrew}";
	public static final String crewManageBean_updateCrewExp =						"#{crewManageBean.updateCrew}";
	public static final String crewManageBean_deleteCrewExp =						"#{crewManageBean.deleteCrew}";
	
	
	public static final String crewMembersInvitationBean_deleteMemberExp =			"#{crewMembersInvitationBean.deleteMember}";
	public static final String crewMembersInvitationBean_addMemberExp =				"#{crewMembersInvitationBean.addMember}";
	public static final String crewMembersInvitationBean_membersListExp =			"#{crewMembersInvitationBean.membersList}";
	public static final String crewMembersInvitationBean_deleteMemberParticipantIdExp =	"#{crewMembersInvitationBean.deleteMemberParticipantId}";
	public static final String crewMembersInvitationBean_addMemberParticipantIdExp =	"#{crewMembersInvitationBean.addMemberParticipantId}";
	public static final String crewMembersInvitationBean_searchStringExp =			"#{crewMembersInvitationBean.searchString}";
	public static final String crewMembersInvitationBean_searchExp =				"#{crewMembersInvitationBean.search}";
	public static final String crewMembersInvitationBean_searchResultListRenderedExp =	"#{crewMembersInvitationBean.searchResultListRendered}";
	
	
	
	public static final String crewMembersInvitationBean_forceIdHackExp = 			"#{crewMembersInvitationBean.forceIdHack}";
	public static final String crewMembersInvitationBean_headerExp = 				"#{crewMembersInvitationBean.header}";
	
	
	public static final String crewEditWizardBeanExp = 								"#{crewEditWizardBean}";
	public static final String crewEditWizardBean_newCrewModeExp = 					"#{crewEditWizardBean.newCrewMode}";
	public static final String crewEditWizardBean_modeExp = 						"#{crewEditWizardBean.mode}";
	public static final String crewEditWizardBean_editCrewModeExp = 				"#{crewEditWizardBean.editCrewMode}";
	public static final String crewEditWizardBean_runLabelExp = 					"#{crewEditWizardBean.runLabel}";
	public static final String crewEditWizardBean_participantIdExp =				"#{crewEditWizardBean.participantId}";
	
	
	private static final String wizardModeFacet = 									"wizardMode";
	
	/**
	 * @Override
	 */
	public WizardStep getSubmissionSuccessStep() {
		return new UICrewRegistrationSuccess();
	}

	/**
	 * @Override
	 */
	public List getWizardSteps() {
		
		List wizardSteps = new ArrayList(2);
		wizardSteps.add(new UICrewManageStep());
		wizardSteps.add(new UICrewMembersInivitationStep());
		
		return wizardSteps;
	}
	
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		
		Application application = context.getApplication();
		
		HtmlInputHidden hidden = (HtmlInputHidden)application.createComponent(HtmlInputHidden.COMPONENT_TYPE);
		hidden.setId(context.getViewRoot().createUniqueId());
		hidden.setValueBinding(valueAtt, application.createValueBinding(crewManageBean_wizardModeExp));
		getFacets().put(wizardModeFacet, hidden);
	}
	
	public void encodeChildren(FacesContext context) throws IOException {
		
		renderChild(context, getFacet(wizardModeFacet));
		super.encodeChildren(context);
	}
	
	/**
	 * @Override
	 */
	public void decode(FacesContext context) {
		super.decode(context);
		
		ValueBinding vb = context.getApplication().createValueBinding(crewManageBean_wizardModeExp);
		vb.setValue(context, Boolean.valueOf((String)context.getExternalContext().getRequestParameterMap().get(getFacet(wizardModeFacet).getClientId(context))));
	}
}