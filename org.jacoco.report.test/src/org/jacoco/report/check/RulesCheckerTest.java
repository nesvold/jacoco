/*******************************************************************************
 * Copyright (c) 2009, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 *******************************************************************************/
package org.jacoco.report.check;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jacoco.core.analysis.ICounter.CounterValue;
import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.jacoco.report.ILanguageNames;
import org.jacoco.report.ReportStructureTestDriver;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Limit}.
 */
public class RulesCheckerTest implements ICheckerOutput {

	private RulesChecker checker;
	private ReportStructureTestDriver driver;
	private List<String> violationMessages;
    private List<String> conformanceMessages;

	@Before
	public void setup() {
		checker = new RulesChecker();
		driver = new ReportStructureTestDriver();
		violationMessages = new ArrayList<String>();
        conformanceMessages = new ArrayList<String>();
	}

	@Test
	public void testSetRules() throws IOException {
		Rule rule = new Rule();
		Limit limit = rule.createLimit();
		limit.setValue(CounterValue.MISSEDCOUNT.name());
		limit.setMaximum("5");
		checker.setRules(Arrays.asList(rule));
		driver.sendGroup(checker.createVisitor(this));
		assertEquals(
				Arrays.asList("Rule violated for BUNDLE bundle: instructions missed count is 10, but expected maximum is 5"),
                violationMessages);
	}

	@Test
	public void testSetLanguageNames() throws IOException {
		Rule rule = new Rule();
		rule.setElement(ElementType.CLASS);
		Limit limit = rule.createLimit();
		limit.setValue(CounterValue.MISSEDCOUNT.name());
		limit.setMaximum("5");
		checker.setRules(Arrays.asList(rule));

		checker.setLanguageNames(new ILanguageNames() {
			public String getQualifiedClassName(String vmname) {
				return "MyClass";
			}

			public String getQualifiedMethodName(String vmclassname,
					String vmmethodname, String vmdesc, String vmsignature) {
				return null;
			}

			public String getPackageName(String vmname) {
				return null;
			}

			public String getMethodName(String vmclassname,
					String vmmethodname, String vmdesc, String vmsignature) {
				return null;
			}

			public String getClassName(String vmname, String vmsignature,
					String vmsuperclass, String[] vminterfaces) {
				return null;
			}
		});

		driver.sendGroup(checker.createVisitor(this));
		assertEquals(
				Arrays.asList("Rule violated for CLASS MyClass: instructions missed count is 10, but expected maximum is 5"),
                violationMessages);
	}

    @Override
    public void onResult(CheckResult result) {
        String message = result.createMessage();
        if (result.getResult() == CheckResult.Result.OK) {
            conformanceMessages.add(message);
        } else {
            violationMessages.add(message);
        }
    }

}
