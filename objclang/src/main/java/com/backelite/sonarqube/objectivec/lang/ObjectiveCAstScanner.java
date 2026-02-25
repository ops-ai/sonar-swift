package com.backelite.sonarqube.objectivec.lang;

import com.backelite.sonarqube.objectivec.lang.lexer.ObjectiveCLexer;
import com.sonar.sslr.api.Token;
import com.sonar.sslr.api.Trivia;
import com.sonar.sslr.impl.Lexer;

import java.io.File;
import java.util.*;

public class ObjectiveCAstScanner {

    private ObjectiveCAstScanner() {
    }

    public static LineCounts scanFile(File file, ObjectiveCConfiguration conf) {
        Lexer lexer = ObjectiveCLexer.create(conf);
        List<Token> tokens = lexer.lex(file);

        Set<Integer> linesOfCode = new HashSet<>();
        Set<Integer> commentLines = new HashSet<>();
        int maxLine = 0;

        for (Token token : tokens) {
            if (token.getType().equals(com.sonar.sslr.api.GenericTokenType.EOF)) {
                if (token.getLine() > maxLine) {
                    maxLine = token.getLine();
                }
                continue;
            }

            linesOfCode.add(token.getLine());
            String[] tokenLines = token.getOriginalValue().split("\r\n|\n|\r", -1);
            for (int i = 1; i < tokenLines.length; i++) {
                linesOfCode.add(token.getLine() + i);
            }
            int lastTokenLine = token.getLine() + tokenLines.length - 1;
            if (lastTokenLine > maxLine) {
                maxLine = lastTokenLine;
            }

            for (Trivia trivia : token.getTrivia()) {
                Token triviaToken = trivia.getToken();
                if (trivia.isComment()) {
                    commentLines.add(triviaToken.getLine());
                    String[] triviaLines = triviaToken.getOriginalValue().split("\r\n|\n|\r", -1);
                    for (int i = 1; i < triviaLines.length; i++) {
                        commentLines.add(triviaToken.getLine() + i);
                    }
                    int lastTriviaLine = triviaToken.getLine() + triviaLines.length - 1;
                    if (lastTriviaLine > maxLine) {
                        maxLine = lastTriviaLine;
                    }
                }
            }
        }

        return new LineCounts(maxLine, linesOfCode.size(), commentLines.size());
    }

    public static class LineCounts {
        private final int lines;
        private final int linesOfCode;
        private final int commentLines;

        public LineCounts(int lines, int linesOfCode, int commentLines) {
            this.lines = lines;
            this.linesOfCode = linesOfCode;
            this.commentLines = commentLines;
        }

        public int getLines() { return lines; }
        public int getLinesOfCode() { return linesOfCode; }
        public int getCommentLines() { return commentLines; }
    }
}
