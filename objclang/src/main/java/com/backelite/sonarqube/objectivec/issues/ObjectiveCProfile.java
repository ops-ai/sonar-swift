package com.backelite.sonarqube.objectivec.issues;

import com.backelite.sonarqube.objectivec.issues.fauxpas.FauxPasProfile;
import com.backelite.sonarqube.objectivec.issues.infer.InferProfile;
import com.backelite.sonarqube.objectivec.issues.oclint.OCLintProfile;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

public class ObjectiveCProfile implements BuiltInQualityProfilesDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectiveCProfile.class);

    @Override
    public void define(Context context) {
        LOGGER.info("Creating Objective-C Profile");

        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile("Objective-C", ObjectiveC.KEY);
        profile.setDefault(true);

        loadRulesFromXml(profile, OCLintProfile.PROFILE_PATH);
        loadRulesFromXml(profile, FauxPasProfile.PROFILE_PATH);
        loadRulesFromXml(profile, InferProfile.PROFILE_PATH);

        profile.done();
    }

    public static void loadRulesFromXml(NewBuiltInQualityProfile profile, String resourcePath) {
        try (InputStream is = ObjectiveCProfile.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                LOGGER.warn("Profile resource not found: {}", resourcePath);
                return;
            }
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
            XMLStreamReader reader = factory.createXMLStreamReader(is);

            String repositoryKey = null;
            String ruleKey = null;
            String currentElement = null;

            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        currentElement = reader.getLocalName();
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        String text = reader.getText().trim();
                        if (!text.isEmpty()) {
                            if ("repositoryKey".equals(currentElement)) {
                                repositoryKey = text;
                            } else if ("key".equals(currentElement)) {
                                ruleKey = text;
                            }
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if ("rule".equals(reader.getLocalName()) && repositoryKey != null && ruleKey != null) {
                            profile.activateRule(repositoryKey, ruleKey);
                            repositoryKey = null;
                            ruleKey = null;
                        }
                        currentElement = null;
                        break;
                }
            }
            reader.close();
        } catch (XMLStreamException | java.io.IOException e) {
            LOGGER.error("Error loading profile from {}", resourcePath, e);
        }
    }
}
