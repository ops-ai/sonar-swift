package com.backelite.sonarqube.objectivec.issues.antlr;

import com.backelite.sonarqube.objectivec.issues.ObjectiveCProfile;
import com.backelite.sonarqube.objectivec.issues.infer.InferRulesDefinition;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class NextProfile implements BuiltInQualityProfilesDefinition {

    private static final Logger logger = LoggerFactory.getLogger(NextProfile.class);
    public static final String PROFILE_PATH = "/org/sonar/plugins/next/profile-next.xml";

    @Override
    public void define(Context context) {
        logger.info("Creating Next Profile");
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(InferRulesDefinition.REPOSITORY_KEY, ObjectiveC.KEY);

        ObjectiveCProfile.loadRulesFromXml(profile, PROFILE_PATH);

        profile.done();
    }
}
