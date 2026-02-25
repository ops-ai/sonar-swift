package com.backelite.sonarqube.objectivec.issues.infer;

import com.backelite.sonarqube.objectivec.issues.ObjectiveCProfile;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class InferProfile implements BuiltInQualityProfilesDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(InferProfile.class);
    public static final String PROFILE_PATH = "/org/sonar/plugins/infer/profile-infer.xml";

    @Override
    public void define(Context context) {
        LOGGER.info("Creating Infer Profile");
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(InferRulesDefinition.REPOSITORY_KEY, ObjectiveC.KEY);

        ObjectiveCProfile.loadRulesFromXml(profile, PROFILE_PATH);

        profile.done();
    }
}
