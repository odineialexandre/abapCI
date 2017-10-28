package abapci.result;

import java.util.ArrayList;
import java.util.List;
import com.sap.adt.tools.abapsource.abapunit.AbapUnitAlertSeverity;
import com.sap.adt.tools.abapsource.abapunit.IAbapUnitAlert;
import com.sap.adt.tools.abapsource.abapunit.IAbapUnitResult;
import com.sap.adt.tools.abapsource.abapunit.IAbapUnitResultItem;
import abapci.domain.UnitTestResultSummary;
import abapci.domain.TestState;

public class TestResultSummaryFactory {
	private static final String UNDEFINED_PACKAGE_NAME = null;

	private TestResultSummaryFactory() {
	}

	public static UnitTestResultSummary create(String packageName, IAbapUnitResult abapUnitResult) {
		int numSuppressedErrors = 0; 

		List<IAbapUnitAlert> criticalAlerts = getCriticalAlerts(abapUnitResult.getAlerts());

		for (IAbapUnitResultItem abapUnitResultItem : abapUnitResult.getItems()) {
			criticalAlerts.addAll(getCriticalAlerts(abapUnitResultItem));
		}
		
		//TODO Split criticalAlerts into active alerts and suppressed alerts 
		
		TestState testState = criticalAlerts.isEmpty() ? TestState.OK : TestState.NOK;
		return new UnitTestResultSummary(packageName, testState, numSuppressedErrors);
	}

	private static List<IAbapUnitAlert> getCriticalAlerts(IAbapUnitResultItem abapUnitResultItem) {
		
		  List<IAbapUnitAlert> criticalAlerts = getCriticalAlerts(abapUnitResultItem.getAlerts());
	    
	    for (IAbapUnitResultItem abapUnitResultSubItem : abapUnitResultItem.getChildItems()) {
			criticalAlerts.addAll(getCriticalAlerts(abapUnitResultSubItem));
		}
		
		return criticalAlerts;
	}

	private static List<IAbapUnitAlert> getCriticalAlerts(List<IAbapUnitAlert> alerts) {
		List<IAbapUnitAlert> criticalAlerts = new ArrayList<>();
		for (IAbapUnitAlert alert : alerts) {
			if (alert != null && alert.getSeverity() != AbapUnitAlertSeverity.TOLERABLE
					&& (alert.getTitle() == null  || !alert.getTitle().contains("Invalid parameter ID"))) {
				criticalAlerts.add(alert);
			}
		}
		return criticalAlerts;
	}

	public static UnitTestResultSummary createUndefined(String packageName) {
		return new UnitTestResultSummary(packageName, TestState.UNDEF, 0);
	}

	public static UnitTestResultSummary createOffline(String packageName) {
		return new UnitTestResultSummary(packageName, TestState.OFFL, 0);
	}

	public static UnitTestResultSummary createUndefined() {
		return createUndefined(UNDEFINED_PACKAGE_NAME);
	}
}