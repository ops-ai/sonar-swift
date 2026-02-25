package com.backelite.sonarqube.swift.issues.swiftlint;

import com.backelite.sonarqube.swift.issues.SwiftProfile;
import com.backelite.sonarqube.swift.lang.core.Swift;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;

public class SwiftLintProfile implements BuiltInQualityProfilesDefinition {
    public static final String PROFILE_PATH = "/org/sonar/plugins/swiftlint/profile-swiftlint.xml";
    private static final Logger LOGGER = LoggerFactory.getLogger(SwiftLintProfile.class);

    @Override
    public void define(Context context) {
        LOGGER.info("Creating SwiftLint Profile");
        NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(SwiftLintRulesDefinition.REPOSITORY_KEY, Swift.KEY);

        SwiftProfile.loadRulesFromXml(profile, PROFILE_PATH);

        profile.done();
    }
}
