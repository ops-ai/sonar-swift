package com.backelite.sonarqube.objectivec;

import com.backelite.sonarqube.commons.MeasureUtil;
import com.backelite.sonarqube.objectivec.cpd.ObjectiveCCpdAnalyzer;
import com.backelite.sonarqube.objectivec.lang.ObjectiveCAstScanner;
import com.backelite.sonarqube.objectivec.lang.ObjectiveCConfiguration;
import com.backelite.sonarqube.objectivec.lang.core.ObjectiveC;
import com.backelite.sonarqube.objectivec.lang.lexer.ObjectiveCLexer;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;

import java.io.File;

public class ObjectiveCSquidSensor implements Sensor {

    private final ObjectiveCCpdAnalyzer objectiveCCpdAnalyzer;
    private SensorContext context;

    public ObjectiveCSquidSensor(SensorContext context) {
        this.context = context;
        this.objectiveCCpdAnalyzer = new ObjectiveCCpdAnalyzer(context, ObjectiveCLexer.create());
    }

    private ObjectiveCConfiguration createConfiguration() {
        return new ObjectiveCConfiguration(context.fileSystem().encoding());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
            .onlyOnLanguage(ObjectiveC.KEY)
            .name("Objective-C Squid")
            .onlyOnFileType(InputFile.Type.MAIN);
    }

    @Override
    public void execute(SensorContext context) {
        this.context = context;
        ObjectiveCConfiguration conf = createConfiguration();
        FilePredicate hasObjC = context.fileSystem().predicates().hasLanguage(ObjectiveC.KEY);
        FilePredicate isMain = context.fileSystem().predicates().hasType(InputFile.Type.MAIN);
        FilePredicate and = context.fileSystem().predicates().and(hasObjC, isMain);

        for (InputFile inputFile : context.fileSystem().inputFiles(and)) {
            File file = inputFile.file();
            ObjectiveCAstScanner.LineCounts counts = ObjectiveCAstScanner.scanFile(file, conf);

            MeasureUtil.saveMeasure(context, inputFile, CoreMetrics.NCLOC, counts.getLinesOfCode());
            MeasureUtil.saveMeasure(context, inputFile, CoreMetrics.COMMENT_LINES, counts.getCommentLines());

            this.objectiveCCpdAnalyzer.pushCpdTokens(inputFile);
        }
    }
}
