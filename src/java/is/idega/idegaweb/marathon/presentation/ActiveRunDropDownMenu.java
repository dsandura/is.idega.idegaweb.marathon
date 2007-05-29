package is.idega.idegaweb.marathon.presentation;

import is.idega.idegaweb.marathon.business.RunBusiness;
import is.idega.idegaweb.marathon.business.Runner;
import is.idega.idegaweb.marathon.data.Year;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;

public class ActiveRunDropDownMenu extends DropdownMenu {

	private static final String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.marathon";
	private static final String PARAMETER_ACTIVE_RUNS = "prm_active_runs";
	private Runner runner = null;

	public ActiveRunDropDownMenu() {
		this(PARAMETER_ACTIVE_RUNS);
	}

	public ActiveRunDropDownMenu(String parameterName) {
		this(parameterName, null);
	}

	public ActiveRunDropDownMenu(String parameterName, Runner runner) {
		super(parameterName);
		this.runner = runner;
	}

	public void main(IWContext iwc) throws Exception {
		super.main(iwc);
		IWResourceBundle iwrb = this.getResourceBundle(iwc);

		IWTimestamp thisYearStamp = IWTimestamp.RightNow();
		String yearString = String.valueOf(thisYearStamp.getYear());
		IWTimestamp nextYearStamp = IWTimestamp.RightNow();
		nextYearStamp.addYears(1);
		String nextYearString = String.valueOf(nextYearStamp.getYear());

		Collection runs = getRunBusiness(iwc).getRuns();
		addMenuElement("-1", iwrb.getLocalizedString("run_year_ddd.select_run", "Select run..."));
		if (runs != null) {
			Iterator iter = runs.iterator();
			while (iter.hasNext()) {
				Group run = (Group) iter.next();
				String runnerYearString = yearString;

				boolean show = false;
				boolean finished = true;
				Map yearMap = getRunBusiness(iwc).getYearsMap(run);
				Year year = (Year) yearMap.get(yearString);
				if (year != null && year.getLastRegistrationDate() != null) {
					IWTimestamp lastRegistrationDate = new IWTimestamp(year.getLastRegistrationDate());
					if (thisYearStamp.isEarlierThan(lastRegistrationDate)) {
						finished = false;
						show = true;
					}
				}
				Year nextYear = (Year) yearMap.get(nextYearString);
				if (finished && nextYear != null) {
					runnerYearString = nextYearString;
					show = true;
				}

				if (show) {
					if (this.runner != null && this.runner.getUser() != null) {
						if (!getRunBusiness(iwc).isRegisteredInRun(runnerYearString, run, this.runner.getUser())) {
							addMenuElement(run.getPrimaryKey().toString(), iwrb.getLocalizedString(run.getName(), run.getName()) + " - " + runnerYearString);
						}
					}
					else {
						addMenuElement(run.getPrimaryKey().toString(), iwrb.getLocalizedString(run.getName(), run.getName()) + " - " + runnerYearString);
					}
				}
			}
		}
		if (this.runner != null && this.runner.getRun() != null) {
			setSelectedElement(this.runner.getRun().getPrimaryKey().toString());
		}
	}

	protected RunBusiness getRunBusiness(IWApplicationContext iwac) {
		try {
			return (RunBusiness) IBOLookup.getServiceInstance(iwac, RunBusiness.class);
		}
		catch (IBOLookupException e) {
			throw new IBORuntimeException(e);
		}
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}