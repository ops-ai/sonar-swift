package com.backelite.sonarqube.swift;

import com.backelite.sonarqube.commons.MeasureUtil;
import com.backelite.sonarqube.swift.cpd.SwiftCpdAnalyzer;
import com.backelite.sonarqube.swift.lang.SwiftAstScanner;
import com.backelite.sonarqube.swift.lang.SwiftConfiguration;
import com.backelite.sonarqube.swift.lang.core.Swift;
import com.backelite.sonarqube.swift.lang.lexer.SwiftLexer;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;

import java.io.File;

public class SwiftSquidSensor implements Sensor {

    private final SwiftCpdAnalyzer swiftCpdAnalyzer;
    private SensorContext context;

    public SwiftSquidSensor(SensorContext context) {
        this.context = context;
        this.swiftCpdAnalyzer = new SwiftCpdAnalyzer(context, SwiftLexer.create());
    }

    private SwiftConfiguration createConfiguration() {
        return new SwiftConfiguration(context.fileSystem().encoding());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
            .onlyOnLanguage(Swift.KEY)
            .name("Swift Squid")
            .onlyOnFileType(InputFile.Type.MAIN);
    }

    @Override
    public void execute(SensorContext sensorContext) {
        this.context = sensorContext;
        SwiftConfiguration conf = createConfiguration();
        FilePredicate hasSwift = context.fileSystem().predicates().hasLanguage(Swift.KEY);
        FilePredicate isMain = context.fileSystem().predicates().hasType(InputFile.Type.MAIN);
        FilePredicate and = context.fileSystem().predicates().and(hasSwift, isMain);

        for (InputFile inputFile : context.fileSystem().inputFiles(and)) {
            File file = inputFile.file();
            SwiftAstScanner.LineCounts counts = SwiftAstScanner.scanFile(file, conf);

            MeasureUtil.saveMeasure(context, inputFile, CoreMetrics.NCLOC, counts.getLinesOfCode());
            MeasureUtil.saveMeasure(context, inputFile, CoreMetrics.COMMENT_LINES, counts.getCommentLines());

            this.swiftCpdAnalyzer.pushCpdTokens(inputFile);
        }
    }
}
