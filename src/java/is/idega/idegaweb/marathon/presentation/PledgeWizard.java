package is.idega.idegaweb.marathon.presentation;

import is.idega.idegaweb.marathon.data.Charity;
import is.idega.idegaweb.marathon.data.Participant;
import is.idega.idegaweb.marathon.data.ParticipantHome;
import is.idega.idegaweb.marathon.util.IWMarathonConstants;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import com.idega.block.entity.business.EntityToPresentationObjectConverter;
import com.idega.block.entity.data.EntityPath;
import com.idega.block.entity.presentation.EntityBrowser;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.data.Group;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.IWColor;

public class PledgeWizard extends RunBlock {
	
	private static final String PARAMETER_ACTION = "prm_action";
	//private static final String PARAMETER_FROM_ACTION = "prm_from_action";
	private static final String PARAMETER_USER_ID = "prm_user_id";
	private static final String PARAMETER_PERSONAL_ID = "prm_personal_id";
	private static final String PARAMETER_FIRST_NAME = "prm_first_name";
	private static final String PARAMETER_MIDDLE_NAME = "prm_middle_name";
	private static final String PARAMETER_LAST_NAME = "prm_last_name";
	private static final String PARAMETER_NAME = "prm_name";
	private static final String PARAMETER_CHARITY = "prm_charity";
	private static final String PARAMETER_SEARCH = "prm_search";
	
	private static int NUMBER_OF_ROWS = 10;

	private static final int ACTION_STEP_PERSON_LOOKUP = 1;
	private static final int ACTION_STEP_PERSONAL_DETAILS = 2;
	private static final int ACTION_STEP_PAYMENT = 3;
	private static final int ACTION_STEP_RECEIPT = 4;
	private static final int ACTION_SAVE = 5;
	private static final int ACTION_CANCEL = 6;
	private int runGroupID = -1;
	
	
	public void main(IWContext iwc) throws Exception {

		switch (parseAction(iwc)) {
			case ACTION_STEP_PERSON_LOOKUP:
				stepOne(iwc);
				break;
			case ACTION_STEP_PERSONAL_DETAILS:
				stepTwo(iwc);
				break;
			case ACTION_STEP_PAYMENT:
				stepThree(iwc);
				break;
			case ACTION_STEP_RECEIPT:
				stepFour(iwc);
				break;
			case ACTION_SAVE:
				save(iwc, true);
				break;
			case ACTION_CANCEL:
				cancel(iwc);
				break;
		}
	}
	
	private int parseAction(IWContext iwc) {
		int action;
		
		if (iwc.isParameterSet(PARAMETER_ACTION)) {
			action = Integer.parseInt(iwc.getParameter(PARAMETER_ACTION));
		} else {
			action = ACTION_STEP_PERSON_LOOKUP;
		}
		return action;
	}
	
	private void stepOne(IWContext iwc) {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		//form.addParameter(PARAMETER_ACTION, ACTION_STEP_TWO);
		
		form.add(getPhasesTable(1, 4, "run_reg.make_pledge", "Make a pledge"));
		form.add(localize("run_reg.pledge_information_text_step_1", "Information text 1..."));
		
		TextInput personalIDInput = new TextInput(PARAMETER_PERSONAL_ID);
		//input.setAsIcelandicSSNumber(localize("run_reg.not_valid_personal_id", "The personal ID you've entered is not valid"));
		personalIDInput.setLength(10);
		personalIDInput.setMaxlength(10);
		//input.setInFocusOnPageLoad(true);
		Layer personalIDLayer = new Layer(Layer.DIV);
		personalIDLayer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label personalIDLabel = new Label(localize("run_reg.personal_id", "Personal ID") + ":", personalIDInput);
		personalIDLayer.add(personalIDLabel);
		personalIDLayer.add(personalIDInput);
		form.add(personalIDLayer);
		form.add(new Break());
		
		TextInput firstNameInput = new TextInput(PARAMETER_FIRST_NAME);
		Layer firstnameLayer = new Layer(Layer.DIV);
		firstnameLayer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label firstnameLabel = new Label(localize("run_reg.first_name", "First name") + ":", firstNameInput);
		firstnameLayer.add(firstnameLabel);
		firstnameLayer.add(firstNameInput);
		form.add(firstnameLayer);
		form.add(new Break());
		
		TextInput middleNameInput = new TextInput(PARAMETER_MIDDLE_NAME);
		Layer middleNameLayer = new Layer(Layer.DIV);
		middleNameLayer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label middleNameLabel = new Label(localize("run_reg.middle_name", "Middle name") + ":", middleNameInput);
		middleNameLayer.add(middleNameLabel);
		middleNameLayer.add(middleNameInput);
		form.add(middleNameLayer);
		form.add(new Break());
		
		TextInput lastName = new TextInput(PARAMETER_LAST_NAME);
		Layer lastNameLayer = new Layer(Layer.DIV);
		lastNameLayer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label lastNameLabel = new Label(localize("run_reg.last_name", "Last name") + ":", lastName);
		lastNameLayer.add(lastNameLabel);
		lastNameLayer.add(lastName);
		form.add(lastNameLayer);
		form.add(new Break());
		
		DropdownMenu charityDropDown = new CharitiesForRunDropDownMenu(PARAMETER_CHARITY);
		Layer charityDropDownLayer = new Layer(Layer.DIV);
		charityDropDownLayer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label charityDropDownLabel = new Label(localize("run_reg.charity", "Charity") + ":", charityDropDown);
		charityDropDownLayer.add(charityDropDownLabel);
		charityDropDownLayer.add(charityDropDown);
		form.add(charityDropDownLayer);
		form.add(new Break());
		
		SubmitButton search = new SubmitButton(PARAMETER_SEARCH, localize("search", "Search"));
		form.add(search);

		if (iwc.isParameterSet(PARAMETER_SEARCH)) {
			
			String first = iwc.getParameter(PARAMETER_FIRST_NAME);
			String middle = iwc.getParameter(PARAMETER_MIDDLE_NAME);
			String last = iwc.getParameter(PARAMETER_LAST_NAME);
			String pid = iwc.getParameter(PARAMETER_PERSONAL_ID);
			String charity = iwc.getParameter(PARAMETER_CHARITY);
			try {
				UserHome userHome = (UserHome) IDOLookup.getHome(User.class);
				ParticipantHome runHome = (ParticipantHome) IDOLookup.getHome(Participant.class);
				
				
		        Collection groups = null;
				Group run = null;
				if (this.runGroupID != -1) {
					run = getGroupBusiness(iwc).getGroupByGroupID(this.runGroupID);
				}
		    
			if (run != null) {
				Collection groupTypesFilter = new ArrayList();
				groupTypesFilter.add(IWMarathonConstants.GROUP_TYPE_RUN_GROUP);
				groups = getGroupBusiness(iwc).getChildGroupsRecursiveResultFiltered(run, groupTypesFilter, true, true, true);
			}
			
			String[] group_id = {"346530"};
			group_id = new String[groups.size()];
			Iterator grIt = groups.iterator();
			for (int i=0; grIt.hasNext(); i++) {
				Group group = (Group)grIt.next();
				group_id[i] = group.getPrimaryKey().toString();
			}
				Collection usersFound = userHome.findUsersByConditions(first, middle, last, pid, null, null, -1, -1, -1, -1, group_id, null, true, false);
				EntityBrowser browser = getBrowser(usersFound, iwc);
		        form.add(browser);
			} catch (Exception e) {
				e.printStackTrace();
			}
			SubmitButton next = new SubmitButton(localize("next", "Next"), PARAMETER_ACTION, String.valueOf(ACTION_STEP_PERSONAL_DETAILS));
			form.add(next);
		}
		add(form);
	}
	
	private void stepTwo(IWContext iwc) {
		Form form = new Form();
		form.maintainParameter(PARAMETER_PERSONAL_ID);
		//form.addParameter(PARAMETER_ACTION, ACTION_STEP_TWO);
		
		form.add(getPhasesTable(2, 4, "run_reg.select_run", "Select run"));
		form.add(localize("run_reg.pledge_information_text_step_2", "Information text 2..."));
		
		User user = null;
		try {
			user = getUserBusiness(iwc).getUser(Integer.parseInt(iwc.getParameter(PARAMETER_USER_ID)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TextInput input = new TextInput(PARAMETER_PERSONAL_ID);
		//input.setAsIcelandicSSNumber(localize("run_reg.not_valid_personal_id", "The personal ID you've entered is not valid"));
		input.setLength(10);
		input.setMaxlength(10);
		//input.setInFocusOnPageLoad(true);
		Layer layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		Label label = new Label(localize("run_reg.personal_id", "Personal ID") + ":", input);
		layer.add(label);
		layer.add(input);
		form.add(layer);
		form.add(new Break());
		input.setValue(user.getPersonalID());
		
		input = new TextInput(PARAMETER_NAME);
		//input.setAsIcelandicSSNumber(localize("run_reg.not_valid_personal_id", "The personal ID you've entered is not valid"));
		input.setLength(10);
		input.setMaxlength(10);
		//input.setInFocusOnPageLoad(true);
		layer = new Layer(Layer.DIV);
		layer.setStyleClass(STYLENAME_FORM_ELEMENT);
		label = new Label(localize("run_reg.name", "Name") + ":", input);
		layer.add(label);
		layer.add(input);
		form.add(layer);
		form.add(new Break());
		input.setValue(user.getName());

		SubmitButton next = new SubmitButton(localize("next", "Next"), PARAMETER_ACTION, String.valueOf(ACTION_STEP_PERSONAL_DETAILS));
		form.add(next);
		add(form);
	}

	private void stepThree(IWContext iwc) {
	}

	private void stepFour(IWContext iwc) {
	}
	
	private void save(IWContext iwc, boolean doPayment) throws RemoteException {
		
	}
	
	private void cancel(IWContext iwc) {
		//iwc.removeSessionAttribute(SESSION_ATTRIBUTE_RUNNER_MAP);
	}
	
	private EntityBrowser getBrowser(Collection entities, IWContext iwc)  {
	    // define checkbox button converter class
	    EntityToPresentationObjectConverter converterToChooseButton = new EntityToPresentationObjectConverter() {
	      public PresentationObject getHeaderPresentationObject(EntityPath entityPath, EntityBrowser browser, IWContext iwc) {
	        return browser.getDefaultConverter().getHeaderPresentationObject(entityPath, browser, iwc);  
	      } 

	      public PresentationObject getPresentationObject(Object entity, EntityPath path, EntityBrowser browser, IWContext iwc) {
	        User user = (User) entity;
	        RadioButton radioButton = new RadioButton(PARAMETER_USER_ID, user.getPrimaryKey().toString());
	        radioButton.setMustBeSelected(localize("must_be_selected", "You must select person"));
	        return radioButton;
	      }
	    };
	    
	    EntityToPresentationObjectConverter converterCharity = new EntityToPresentationObjectConverter() {
            public PresentationObject getHeaderPresentationObject(EntityPath entityPath, EntityBrowser browser, IWContext iwc) {
              return browser.getDefaultConverter().getHeaderPresentationObject(entityPath, browser, iwc);
          }
          
          public PresentationObject getPresentationObject(Object entity, EntityPath path, EntityBrowser browser, IWContext iwc) {
              Participant participant = (Participant) entity;
              Charity charity = null;
              String charitySSN = null;
//              try {
//            	  charitySSN = null;//getRunBusiness(iwc).getCharityForRunner(participant, null, null);
//              }
//              catch (RemoteException e) {
//                  e.printStackTrace();
//              }
              Text text = null;
              if (charity!= null) {
				text = new Text(charity.getName());
			}
              return text; 
          }
        };

	    // set default columns
	    String nameKey = User.class.getName()+".FIRST_NAME:" + User.class.getName()+".MIDDLE_NAME:"+User.class.getName()+".LAST_NAME";
	    String pinKey = User.class.getName()+".PERSONAL_ID";
	    //String charityKey = Charity.class.getName()+".NAME";
	    EntityBrowser browser = EntityBrowser.getInstanceUsingExternalForm();
	    browser.setAcceptUserSettingsShowUserSettingsButton(false, false);
	    browser.setDefaultNumberOfRows(NUMBER_OF_ROWS);
	    browser.setEntities("pledge_wizard", entities);
	    browser.setWidth(Table.HUNDRED_PERCENT);
	    //fonts
	    Text column = new Text();
	    column.setBold();
	    browser.setColumnTextProxy(column);
	    //   set color of rows
	    browser.setColorForEvenRows(IWColor.getHexColorString(246, 246, 247));
	    browser.setColorForOddRows("#FFFFFF");
	      
	    browser.setDefaultColumn(1, nameKey);
	    browser.setDefaultColumn(2, pinKey);
	    //browser.setDefaultColumn(3, charityKey);
	    browser.setMandatoryColumn(1, "Choose");
	    // set special converters
	    browser.setEntityToPresentationConverter("Choose", converterToChooseButton);
	    return browser;
	}
	
	public void setRunYearGroup(Group group) {
		setRunYearGroup(new Integer(group.getPrimaryKey().toString()).intValue());
	}
	
	public void setRunYearGroup(int groupID) {
		if (groupID != -1) {
			this.runGroupID = groupID;
		}
	}
}