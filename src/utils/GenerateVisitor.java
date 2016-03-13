package utils;

import org.antlr.v4.Tool;

public class GenerateVisitor {
    public static void main(String[] args) {
        Tool.main(new String[] {
                "-visitor", "Java8.g4",
                "-package", "visitors",
        });
    }
}
