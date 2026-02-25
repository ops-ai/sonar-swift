package com.backelite.sonarqube.objectivec.issues.oclint;

import com.backelite.sonarqube.objectivec.issues.ObjectiveCProfile;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public final class OCLintProfile implements BuiltInQualityProfilesDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(OCLintProfile.class);
    public static final String PROFILE_PATH = "/org/sonar/plugins/oclint/profile-oclint.xml";

    @Override
    public void define(Context context) {
        LOGGER.info("Creating OCLint Profile");
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(OCLintRulesDefinition.REPOSITORY_KEY, ObjectiveC.KEY);

        ObjectiveCProfile.loadRulesFromXml(profile, PROFILE_PATH);

        profile.done();
    }
}
