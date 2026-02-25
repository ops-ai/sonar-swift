/**
 * commons - Enables analysis of Swift and Objective-C projects into SonarQube.
 * Copyright © 2015 Backelite (${email})
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.backelite.sonarqube.commons.surefire;

import org.apache.commons.lang.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class SurefireStaxHandler {

    private final UnitTestIndex index;

    public SurefireStaxHandler(UnitTestIndex index) {
        this.index = index;
    }

    public void stream(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && "testsuite".equals(reader.getLocalName())) {
                String testSuiteClassName = reader.getAttributeValue(null, "name");
                if (StringUtils.contains(testSuiteClassName, "$")) {
                    skipElement(reader);
                    continue;
                }
                parseTestSuite(reader, testSuiteClassName);
            }
        }
    }

    private void parseTestSuite(XMLStreamReader reader, String testSuiteClassName) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                if ("testcase".equals(reader.getLocalName())) {
                    parseTestCase(reader, testSuiteClassName);
                } else {
                    skipElement(reader);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && "testsuite".equals(reader.getLocalName())) {
                return;
            }
        }
    }

    private void parseTestCase(XMLStreamReader reader, String testSuiteClassName) throws XMLStreamException {
        String testClassName = getClassname(reader, testSuiteClassName);
        UnitTestClassReport classReport = index.index(testClassName);

        UnitTestResult detail = new UnitTestResult();
        detail.setName(getTestCaseName(reader));
        detail.setTestSuiteClassName(testSuiteClassName);

        String status = UnitTestResult.STATUS_OK;
        String time = reader.getAttributeValue(null, "time");
        Long duration = null;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();
                if ("skipped".equals(elementName)) {
                    status = UnitTestResult.STATUS_SKIPPED;
                    duration = 0L;
                    skipElement(reader);
                } else if ("failure".equals(elementName)) {
                    status = UnitTestResult.STATUS_FAILURE;
                    detail.setMessage(reader.getAttributeValue(null, "message"));
                    detail.setStackTrace(reader.getElementText());
                } else if ("error".equals(elementName)) {
                    status = UnitTestResult.STATUS_ERROR;
                    detail.setMessage(reader.getAttributeValue(null, "message"));
                    detail.setStackTrace(reader.getElementText());
                } else {
                    skipElement(reader);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && "testcase".equals(reader.getLocalName())) {
                break;
            }
        }

        if (duration == null) {
            duration = getTimeAttributeInMS(time);
        }
        detail.setDurationMilliseconds(duration);
        detail.setStatus(status);
        classReport.add(detail);
    }

    private static String getClassname(XMLStreamReader reader, String defaultClassname) {
        String testClassName = reader.getAttributeValue(null, "classname");
        if (StringUtils.isNotBlank(testClassName) && testClassName.endsWith(")")) {
            int openParenthesisIndex = testClassName.indexOf('(');
            if (openParenthesisIndex > 0) {
                testClassName = testClassName.substring(0, openParenthesisIndex);
            }
        }
        return StringUtils.defaultIfBlank(testClassName, defaultClassname);
    }

    private static String getTestCaseName(XMLStreamReader reader) {
        String classname = reader.getAttributeValue(null, "classname");
        String name = reader.getAttributeValue(null, "name");
        if (StringUtils.contains(classname, "$")) {
            return StringUtils.substringAfter(classname, "$") + "/" + name;
        }
        return name;
    }

    private static long getTimeAttributeInMS(String value) throws XMLStreamException {
        if (value == null) {
            return 0L;
        }
        try {
            Number number = NumberFormat.getInstance(Locale.ENGLISH).parse(value);
            double time = number.doubleValue();
            return !Double.isNaN(time) ? Math.round(time * 1000.0) : 0L;
        } catch (ParseException e) {
            throw new XMLStreamException(e);
        }
    }

    private static void skipElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 1;
        while (reader.hasNext() && depth > 0) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                depth--;
            }
        }
    }
}
