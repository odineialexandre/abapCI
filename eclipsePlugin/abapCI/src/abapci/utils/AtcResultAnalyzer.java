package abapci.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sap.adt.atc.model.atcfinding.IAtcFinding;
import com.sap.adt.atc.model.atcfinding.IAtcFindingList;
import com.sap.adt.atc.model.atcobject.IAtcObject;
import com.sap.adt.atc.model.atcworklist.IAtcWorklist;

import abapci.activation.Activation;
import abapci.ci.views.ViewModel;
import abapci.domain.ErrorPriority;
import abapci.domain.InvalidItem;
import abapci.domain.TestState;
import abapci.testResult.TestResult;

public class AtcResultAnalyzer {

	public static TestResult getTestResult(IAtcWorklist atcWorklist, Set<Activation> inactiveObjects) {
		TestResult testResult;

		if (atcWorklist == null) {
			testResult = new TestResult();
		} else {
			testResult = new TestResult(true, 0, getInvalidItems(atcWorklist), inactiveObjects);
		}

		return testResult;
	}

	private static List<InvalidItem> getInvalidItems(IAtcWorklist atcWorklist) {
		List<InvalidItem> invalidItems = new ArrayList<>();

		for (IAtcObject object : atcWorklist.getObjects().getObject()) {
			IAtcFindingList findingList = object.getFindings();
			for (IAtcFinding finding : findingList.getFinding()) {
				if (finding != null) {
					String location = finding.getLocation().toLowerCase();
					boolean isSuppressed = ViewModel.INSTANCE.getSuppressions().stream()
							.anyMatch(item -> location.contains("/" + item.getClassName().toLowerCase() + "/"));

					// TODO Identifier is Objectname and Line
					Pattern devObjectNamePattern = Pattern.compile("findings\\/(.*)\\/CLAS");
					Matcher matcher = devObjectNamePattern.matcher(finding.getUri());
					String devObjectName = matcher.find() ? matcher.group(1) : "UNDEF";
					devObjectName = object.getName();
					invalidItems.add(new InvalidItem(devObjectName, finding.getCheckTitle(), isSuppressed,
							URI.create(finding.getLocation()), ErrorPriority.getFromInt(finding.getPriority())));
				}
			}
		}
		return invalidItems;
	}

	@Deprecated
	public static TestState getTestState(IAtcWorklist atcWorklist) {
		TestState testState;

		if (atcWorklist == null) {
			testState = TestState.UNDEF;
		} else if (getNumOfFindings(atcWorklist) > 0) {
			testState = TestState.NOK;
		} else {
			testState = TestState.OK;
		}
		return testState;
	}

	@Deprecated
	public static int getNumOfFindings(IAtcWorklist atcWorklist) {
		int numFindings = 0;

		for (IAtcObject object : atcWorklist.getObjects().getObject()) {
			IAtcFindingList findingList = object.getFindings();
			for (IAtcFinding finding : findingList.getFinding()) {
				if (finding.getPriority() == 1) {
					numFindings = numFindings + 1;
				}
			}
		}
		return numFindings;
	}

	public static String getOutputLabel(IAtcWorklist atcWorklist) {
		String outputLabel;
		if (atcWorklist == null) {
			outputLabel = TestState.UNDEF.toString();
		} else {
			long numUnsuppressedItems = getInvalidItems(atcWorklist).stream().filter(item -> !item.isSuppressed())
					.count();
			if (numUnsuppressedItems > 0) {
				outputLabel = "Findings: " + numUnsuppressedItems;
			} else {
				outputLabel = TestState.OK.toString();
			}
		}
		return outputLabel;
	}

}
