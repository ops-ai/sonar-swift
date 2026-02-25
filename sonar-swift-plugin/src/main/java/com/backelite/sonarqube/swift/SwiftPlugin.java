package com.backelite.sonarqube.swift;

import com.backelite.sonarqube.commons.TestFileFinders;
import com.backelite.sonarqube.objectivec.ObjectiveCSquidSensor;
import com.backelite.sonarqube.objectivec.cpd.ObjectiveCCpdAnalyzer;
import com.backelite.sonarqube.objectivec.issues.ObjectiveCProfile;
import com.backelite.sonarqube.objectivec.issues.fauxpas.FauxPasProfile;
import com.backelite.sonarqube.objectivec.issues.fauxpas.FauxPasRulesDefinition;
import com.backelite.sonarqube.objectivec.issues.fauxpas.FauxPasSensor;
import com.backelite.sonarqube.objectivec.issues.infer.InferProfile;
import com.backelite.sonarqube.objectivec.issues.infer.InferRulesDefinition;
import com.backelite.sonarqube.objectivec.issues.infer.InferSensor;
import com.backelite.sonarqube.objectivec.issues.oclint.OCLintProfile;
import com.backelite.sonarqube.objectivec.issues.oclint.OCLintRulesDefinition;
import com.backelite.sonarqube.objectivec.issues.oclint.OCLintSensor;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import com.backelite.sonarqube.objectivec.surefire.ObjectiveCTestFileFinder;
import com.backelite.sonarqube.swift.complexity.LizardSensor;
import com.backelite.sonarqube.swift.coverage.CoberturaSensor;
import com.backelite.sonarqube.swift.issues.SwiftProfile;
import com.backelite.sonarqube.swift.issues.swiftlint.SwiftLintProfile;
import com.backelite.sonarqube.swift.issues.swiftlint.SwiftLintRulesDefinition;
import com.backelite.sonarqube.swift.issues.swiftlint.SwiftLintSensor;
import com.backelite.sonarqube.swift.issues.tailor.TailorProfile;
import com.backelite.sonarqube.swift.issues.tailor.TailorRulesDefinition;
import com.backelite.sonarqube.swift.issues.tailor.TailorSensor;
import com.backelite.sonarqube.swift.lang.core.Swift;
import com.backelite.sonarqube.swift.surefire.SwiftTestFileFinder;
import com.github.sonar.next.sonarqube.java.issues.infer.JavaInferSensor;
import org.sonar.api.Plugin;
import org.sonar.api.config.PropertyDefinition;

import java.util.Arrays;

public class SwiftPlugin implements Plugin {

    @Override
    public void define(Context context) {
        TestFileFinders.getInstance().addFinder(new SwiftTestFileFinder());
        TestFileFinders.getInstance().addFinder(new ObjectiveCTestFileFinder());

        context.addExtensions(Arrays.asList(
            PropertyDefinition.builder(CoberturaSensor.REPORT_PATTERN_KEY)
                .defaultValue(CoberturaSensor.DEFAULT_REPORT_PATTERN)
                .name("Path to Cobertura reports (coverage)")
                .description("Relative to projects' root. Ant patterns are accepted")
                .onQualifiers("TRK")
                .build(),
            PropertyDefinition.builder(SwiftLintSensor.REPORT_PATH_KEY)
                .defaultValue(SwiftLintSensor.DEFAULT_REPORT_PATH)
                .name("Path to SwiftLint report")
                .description("Relative to projects' root.")
                .onQualifiers("TRK")
                .build(),
            PropertyDefinition.builder(TailorSensor.REPORT_PATH_KEY)
                .defaultValue(TailorSensor.DEFAULT_REPORT_PATH)
                .name("Path to Tailor report")
                .description("Relative to projects' root.")
                .onQualifiers("TRK")
                .build(),
            PropertyDefinition.builder(LizardSensor.REPORT_PATH_KEY)
                .defaultValue(LizardSensor.DEFAULT_REPORT_PATH)
                .name("Path to Lizard report (complexity)")
                .description("Relative to projects' root.")
                .onQualifiers("TRK")
                .build(),
            PropertyDefinition.builder(OCLintSensor.REPORT_PATH_KEY)
                .defaultValue(OCLintSensor.DEFAULT_REPORT_PATH)
                .name("Path to OCLint pmd formatted report")
                .description("Relative to projects' root.")
                .onQualifiers("TRK")
                .build(),
            PropertyDefinition.builder(FauxPasSensor.REPORT_PATH_KEY)
                .defaultValue(FauxPasSensor.DEFAULT_REPORT_PATH)
                .name("Path to FauxPas json formatted report")
                .description("Relative to projects' root.")
                .onQualifiers("TRK")
                .build(),
            PropertyDefinition.builder(InferSensor.REPORT_PATH_KEY)
                .defaultValue(InferSensor.DEFAULT_REPORT_PATH)
                .name("Path to Infer json formatted report")
                .description("Relative to projects' root.")
                .onQualifiers("TRK")
                .build(),
            PropertyDefinition.builder(JavaInferSensor.REPORT_PATH_KEY)
                .defaultValue(JavaInferSensor.DEFAULT_REPORT_PATH)
                .name("Path to Infer json formatted report")
                .description("Relative to projects' root.")
                .onQualifiers("TRK")
                .build()
        ));

        context.addExtensions(Arrays.asList(
            // Language support
            Swift.class,
            SwiftProfile.class,
            ObjectiveC.class,
            ObjectiveCProfile.class,

            // SwiftLint rules
            SwiftLintSensor.class,
            SwiftLintRulesDefinition.class,

            // SwiftLint quality profile
            SwiftLintProfile.class,

            // Tailor rules
            TailorSensor.class,
            TailorRulesDefinition.class,

            // Tailor quality profile
            TailorProfile.class,

            // OCLint rules
            OCLintSensor.class,
            OCLintRulesDefinition.class,

            // OCLint quality profile
            OCLintProfile.class,

            // Infer OC rules
            InferSensor.class,
            InferRulesDefinition.class,

            // Infer OC quality profile
            InferProfile.class,

            // Infer Java rules
            JavaInferSensor.class,
            com.github.sonar.next.sonarqube.java.issues.infer.InferRulesDefinition.class,

            // Infer Java quality profile
            com.github.sonar.next.sonarqube.java.issues.infer.InferProfile.class,

            // FauxPas rules
            FauxPasSensor.class,
            FauxPasRulesDefinition.class,

            // FauxPas quality profile
            FauxPasProfile.class,

            // Duplications search
            ObjectiveCCpdAnalyzer.class,

            // Code
            SwiftSquidSensor.class,
            ObjectiveCSquidSensor.class,

            // Coverage
            CoberturaSensor.class,

            // Complexity
            LizardSensor.class
        ));
    }
}
