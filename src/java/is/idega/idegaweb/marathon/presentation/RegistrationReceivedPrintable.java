/*
 * Created on Aug 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package is.idega.idegaweb.marathon.presentation;

import is.idega.idegaweb.marathon.business.ConverterUtility;
import is.idega.idegaweb.marathon.data.Participant;
import is.idega.idegaweb.marathon.data.Run;
import is.idega.idegaweb.marathon.presentation.rm.RMRegistration;
import is.idega.idegaweb.marathon.util.IWMarathonConstants;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.PrintButton;
import com.idega.presentation.ui.Window;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;
import com.idega.util.LocaleUtil;
import com.idega.util.text.SocialSecurityNumber;


/**
 * @author birna
 *
 */
public class RegistrationReceivedPrintable extends Window {
	private boolean isIcelandicPersonalID = false;
	public static final String PARAM_HIDE_TSHIRT = "show_tshirt";
	
	public RegistrationReceivedPrintable() {
		setResizable(true);
		setHeight(450);
		setWidth(650);
	}

	public void main(IWContext iwc) throws Exception {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		setTitle(iwrb.getLocalizedString("run_reg.receipt", "Receipt"));

		Table t = new Table(1, 2);
		t.setStyleAttribute("border-style:solid;border-color:#e1e1e1;border-width:1px;");
		t.setHeight(Table.HUNDRED_PERCENT);
		t.setVerticalAlignment(1, 1, Table.VERTICAL_ALIGN_TOP);
		t.setVerticalAlignment(1, 2, Table.VERTICAL_ALIGN_BOTTOM);

		Table table = new Table();
		table.setCellpadding(0);
		table.setCellspacing(0);
		table.setWidth(Table.HUNDRED_PERCENT);
		int row = 1;
		
		boolean isIcelandic = iwc.getCurrentLocale().equals(LocaleUtil.getIcelandicLocale());
		boolean showTShirt = true;
		if (iwc.isParameterSet(PARAM_HIDE_TSHIRT)) {
			showTShirt = false;
		}
		
		Collection runners = (Collection) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_PARTICIPANTS);
		if (runners == null) {
			add(new Text(iwrb.getLocalizedString("session_has_expired", "The session has expired")));
			return;
		}
		double amount = ((Double) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_AMOUNT)).doubleValue();
		String cardNumber = (String) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_CARD_NUMBER);
		IWTimestamp stamp = (IWTimestamp) iwc.getSessionAttribute(Registration.SESSION_ATTRIBUTE_PAYMENT_DATE);

		Group run = null;
		Run selectedRun = null;
		Iterator it = runners.iterator();
		if (it.hasNext()) {
			Participant participant = (Participant) it.next();
			run = participant.getRunTypeGroup();
			try {
				selectedRun = ConverterUtility.getInstance().convertGroupToRun(run);
			} catch (FinderException e) {
			}

			if (participant.getUser() != null
					&& participant.getUser().getPersonalID() != null
					&& !"".equals(participant.getUser().getPersonalID()
							.trim())) {
				if (SocialSecurityNumber
						.isValidIcelandicSocialSecurityNumber(participant
								.getUser().getPersonalID())) {
					this.isIcelandicPersonalID = true;
				}
			}
		}

		table.setHeight(row++, 18);

		String greeting = iwrb.getLocalizedString("run_reg.hello_participant", "Dear participant");
		if (selectedRun != null) {
			if (isIcelandic) {	
				greeting = selectedRun.getRunRegistrationReceiptGreeting();
			} else {
				greeting = selectedRun.getRunRegistrationReceiptGreetingEnglish();
			}			
		}
		
		table.add(getHeader(greeting), 1, row++);
		table.setHeight(row++, 16);

		table.add(getText(iwrb.getLocalizedString("run_reg.payment_received", "You have registered for the following:")), 1, row++);
		table.setHeight(row++, 8);

		Table runnerTable = new Table(5, runners.size() + 4);
		runnerTable.setWidth(Table.HUNDRED_PERCENT);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.runner_name", "Runner name")), 1, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.run", "Run")), 2, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.distance", "Distance")), 3, 1);
		runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.race_number", "Race number")), 4, 1);
		if (showTShirt) {
			runnerTable.add(getHeader(iwrb.getLocalizedString("run_reg.shirt_size", "Shirt size")), 5, 1);
		}
		table.add(runnerTable, 1, row++);
		int runRow = 2;
		Iterator iter = runners.iterator();
		while (iter.hasNext()) {
			Participant participant = (Participant) iter.next();
			run = participant.getRunTypeGroup();
			Group distance = participant.getRunDistanceGroup();
			
			runnerTable.add(getText(participant.getUser().getName()), 1, runRow);
			runnerTable.add(getText(iwrb.getLocalizedString(run.getName(), run.getName())), 2, runRow);
			runnerTable.add(getText(iwrb.getLocalizedString(distance.getName(), distance.getName())), 3, runRow);
			if (run.getPrimaryKey().toString().equals(RMRegistration.LAZY_TOWN_GROUP_ID)) {
				runnerTable.add(getText(""), 4, runRow);
			} else {
				runnerTable.add(getText(String.valueOf(participant.getParticipantNumber())), 4, runRow);
			}

			if (showTShirt) {
				runnerTable.add(getText(iwrb.getLocalizedString("shirt_size." + participant.getShirtSize(), participant.getShirtSize())), 5, runRow++);
			}
			
			if (participant.getRelayPartner1SSN() != null && !"".equals(participant.getRelayPartner1SSN())) {
				runnerTable.add(participant.getRelayPartner1Name(), 1, runRow);
				runnerTable.add(iwrb.getLocalizedString(run.getName(), run.getName()), 2, runRow);
				runnerTable.add(getText(iwrb.getLocalizedString(distance.getName(), distance.getName())), 3, runRow);
				runnerTable.add(getText(String.valueOf(participant.getParticipantNumber())), 4, runRow);
				if (showTShirt) {
					runnerTable.add(getText(iwrb.getLocalizedString("shirt_size." + participant.getRelayPartner1ShirtSize(), participant.getRelayPartner1ShirtSize())), 5, runRow++);
				}

				if (participant.getRelayPartner2SSN() != null && !"".equals(participant.getRelayPartner2SSN())) {
					runnerTable.add(participant.getRelayPartner2Name(), 1, runRow);
					runnerTable.add(iwrb.getLocalizedString(run.getName(), run.getName()), 2, runRow);
					runnerTable.add(getText(iwrb.getLocalizedString(distance.getName(), distance.getName())), 3, runRow);
					runnerTable.add(getText(String.valueOf(participant.getParticipantNumber())), 4, runRow);
					if (showTShirt) {
						runnerTable.add(getText(iwrb.getLocalizedString("shirt_size." + participant.getRelayPartner2ShirtSize(), participant.getRelayPartner2ShirtSize())), 5, runRow++);
					}
					
					if (participant.getRelayPartner3SSN() != null && !"".equals(participant.getRelayPartner3SSN())) {
						runnerTable.add(participant.getRelayPartner3Name(), 1, runRow);
						runnerTable.add(iwrb.getLocalizedString(run.getName(), run.getName()), 2, runRow);
						runnerTable.add(getText(iwrb.getLocalizedString(distance.getName(), distance.getName())), 3, runRow);
						runnerTable.add(getText(String.valueOf(participant.getParticipantNumber())), 4, runRow);
						if (showTShirt) {
							runnerTable.add(getText(iwrb.getLocalizedString("shirt_size." + participant.getRelayPartner3ShirtSize(), participant.getRelayPartner3ShirtSize())), 5, runRow++);
						}
					}
				}				
			}
		}
		
		Table creditCardTable = new Table(2, 3);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.payment_received_timestamp", "Payment received") + ":"), 1, 1);
		creditCardTable.add(getText(stamp.getLocaleDateAndTime(iwc.getCurrentLocale(), IWTimestamp.SHORT, IWTimestamp.SHORT)), 2, 1);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.card_number", "Card number") + ":"), 1, 2);
		creditCardTable.add(getText(cardNumber), 2, 2);
		creditCardTable.add(getHeader(iwrb.getLocalizedString("run_reg.amount", "Amount") + ":"), 1, 3);
		creditCardTable.add(getText(formatAmount(iwc.getCurrentLocale(), (float) amount)), 2, 3);
		table.setHeight(row++, 16);
		table.add(creditCardTable, 1, row++);
				
		table.setHeight(row++, 16);
		table.add(getHeader(iwrb.getLocalizedString("run_reg.receipt_info_headline", "Receipt - Please print it out")), 1, row++);
		table.add(getText(iwrb.getLocalizedString("run_reg.receipt_info_headline_body", "This document is your receipt, please print it out and bring it with you when you collect your race material.")), 1, row++);

		if (selectedRun != null) {
			table.setHeight(row++, 16);
			table.add(getHeader(iwrb.getLocalizedString("run_reg.delivery_of_race_material_headline", "Further information about the run is available on:")), 1, row++);
			String informationText;
			if (isIcelandic) {	
				informationText= selectedRun.getRunRegistrationReceiptInfo();
			} else {
				informationText = selectedRun.getRunRegistrationReceiptInfoEnglish();
			}
			table.add(getText(informationText), 1, row++);
		}
		
		table.setHeight(row++, 16);
		table.add(getText(iwrb.getLocalizedString("run_reg.best_regards", "Best regards,")), 1, row++);

		if (selectedRun != null) {
			table.add(getText(iwrb.getLocalizedString(selectedRun.getName(), selectedRun.getName())), 1, row++);
			table.add(getText(selectedRun.getRunHomePage()), 1, row++);
		}
		
		table.setHeight(row++, 16);
		t.add(table, 1, 1);
		
		PrintButton print = new PrintButton(iwrb.getLocalizedString("print", "Print receipt"));
		t.add(print, 1, 2);
		
		add(t);
	}
	
	private String formatAmount(Locale locale, float amount) {
		return NumberFormat.getInstance(locale).format(amount) + " "
		+ (this.isIcelandicPersonalID ? "ISK" : "EUR");
	}
	
	private Text getHeader(String s) {
		Text text = new Text(s);
		text.setBold(true);
		
		return text;
	}
	
	private Text getText(String s) {
		return new Text(s);
	}
	
	public String getBundleIdentifier() {
		return IWMarathonConstants.IW_BUNDLE_IDENTIFIER;
	}
}