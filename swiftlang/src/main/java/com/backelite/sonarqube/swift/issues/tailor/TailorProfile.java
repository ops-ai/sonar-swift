package com.backelite.sonarqube.swift.issues.tailor;

import com.backelite.sonarqube.swift.issues.SwiftProfile;
import com.backelite.sonarqube.swift.lang.core.Swift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class TailorProfile implements BuiltInQualityProfilesDefinition {
    private static final Logger LOGGER = LoggerFactory.getLogger(TailorProfile.class);
    public static final String PROFILE_PATH = "/org/sonar/plugins/tailor/profile-tailor.xml";

    @Override
    public void define(Context context) {
        LOGGER.info("Creating Tailor Profile");
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(TailorRulesDefinition.REPOSITORY_KEY, Swift.KEY);

        SwiftProfile.loadRulesFromXml(profile, PROFILE_PATH);

        profile.done();
    }
}
