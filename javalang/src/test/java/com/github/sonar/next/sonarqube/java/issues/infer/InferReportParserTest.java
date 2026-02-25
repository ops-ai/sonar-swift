package com.github.sonar.next.sonarqube.java.issues.infer;

import static org.mockito.Mockito.*;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.rule.RuleKey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

class InferReportParserTest {

    @TempDir
    Path tempDir;

    @Mock
    SensorContext sensorContext;

    @Mock
    FileSystem fileSystem;

    @Mock
    FilePredicates filePredicates;

    @Mock
    FilePredicate filePredicate;

    @Mock
    InputFile inputFile;

    private InferReportParser self;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        when(sensorContext.fileSystem()).thenReturn(fileSystem);
        when(fileSystem.predicates()).thenReturn(filePredicates);
        when(filePredicates.hasAbsolutePath(anyString())).thenReturn(filePredicate);
        when(filePredicates.hasRelativePath(anyString())).thenReturn(filePredicate);
        when(filePredicates.or(any(FilePredicate.class), any(FilePredicate.class))).thenReturn(filePredicate);
        when(fileSystem.hasFiles(any(FilePredicate.class))).thenReturn(true);
        when(fileSystem.inputFile(any(FilePredicate.class))).thenReturn(inputFile);
        when(inputFile.selectLine(anyInt())).thenReturn(mock(org.sonar.api.batch.fs.TextRange.class));

        self = new InferReportParser(sensorContext);
    }

    @Test
    void parseReport() throws IOException, ParseException {
        ClassLoader classLoader = getClass().getClassLoader();
        File reportFile = new File(Objects.requireNonNull(classLoader.getResource("report_java.json")).getFile());

        NewIssue newIssue = mock(NewIssue.class);
        NewIssueLocation newIssueLocation = mock(NewIssueLocation.class);
        when(newIssue.addFlow(anyIterable())).thenReturn(newIssue);
        when(newIssue.forRule(any(RuleKey.class))).thenReturn(newIssue);
        when(newIssue.at(any(NewIssueLocation.class))).thenReturn(newIssue);
        when(newIssue.newLocation()).thenReturn(newIssueLocation);
        when(newIssueLocation.on(any(InputFile.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.at(any(org.sonar.api.batch.fs.TextRange.class))).thenReturn(newIssueLocation);
        when(newIssueLocation.message(anyString())).thenReturn(newIssueLocation);
        when(sensorContext.newIssue()).thenReturn(newIssue);

        self.parseReport(reportFile);

        verify(newIssue, times(1)).forRule(any(RuleKey.class));
        verify(newIssue, times(1)).at(any(NewIssueLocation.class));
        verify(newIssue, times(1)).save();
    }
}
