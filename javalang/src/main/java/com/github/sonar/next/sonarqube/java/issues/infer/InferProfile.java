package com.github.sonar.next.sonarqube.java.issues.infer;

import com.github.sonar.next.sonarqube.java.lang.core.Java;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

public class InferProfile implements BuiltInQualityProfilesDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(InferProfile.class);
    public static final String PROFILE_PATH = "/org/sonar/plugins/infer/java-profile-infer.xml";

    @Override
    public void define(Context context) {
        LOGGER.info("Creating Infer Java Profile");
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(InferRulesDefinition.REPOSITORY_KEY, Java.KEY);

        try (InputStream is = getClass().getResourceAsStream(PROFILE_PATH)) {
            if (is == null) {
                LOGGER.warn("Profile resource not found: {}", PROFILE_PATH);
                profile.done();
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
            LOGGER.error("Error loading profile from {}", PROFILE_PATH, e);
        }

        profile.done();
    }
}
