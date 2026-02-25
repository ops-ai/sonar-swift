package com.backelite.sonarqube.objectivec.issues.fauxpas;

import com.backelite.sonarqube.objectivec.issues.ObjectiveCProfile;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class FauxPasProfile implements BuiltInQualityProfilesDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(FauxPasProfile.class);
    public static final String PROFILE_PATH = "/org/sonar/plugins/fauxpas/profile-fauxpas.xml";

    @Override
    public void define(Context context) {
        LOGGER.info("Creating FauxPas Profile");
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(FauxPasRulesDefinition.REPOSITORY_KEY, ObjectiveC.KEY);

        ObjectiveCProfile.loadRulesFromXml(profile, PROFILE_PATH);

        profile.done();
    }
}
