package com.backelite.sonarqube.commons;

import org.sonar.api.utils.WildcardPattern;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DirectoryScanner {

    private final File baseDir;
    private final WildcardPattern pattern;

    public DirectoryScanner(File baseDir, WildcardPattern pattern) {
        this.baseDir = baseDir;
        this.pattern = pattern;
    }

    public List<File> getIncludedFiles() {
        List<File> result = new ArrayList<>();
        Path basePath = baseDir.toPath();
        try (Stream<Path> stream = Files.walk(basePath)) {
            stream.filter(Files::isRegularFile)
                .forEach(path -> {
                    String relativePath = basePath.relativize(path).toString().replace('\\', '/');
                    if (pattern.match(relativePath)) {
                        result.add(path.toFile());
                    }
                });
        } catch (IOException e) {
            // Return empty list if directory cannot be walked
        }
        return result;
    }

}
