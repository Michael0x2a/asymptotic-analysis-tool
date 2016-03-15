package main;

import java.util.Map;

public class Result {
    private String antlrAst;
    private String simplifiedAst;
    private String rawEquation;
    private String simplifiedEquation;
    private String finalEquation;
    private Map<String, String> assumptions;
    private String error;

    public Result(String antlrAst,
                  String simplifedAst,
                  String rawEquation,
                  String simplifiedEquation,
                  String finalEquation,
                  Map<String, String> assumptions,
                  String error) {
        this.antlrAst = antlrAst;
        this.simplifiedAst = simplifedAst;
        this.rawEquation = rawEquation;
        this.simplifiedEquation = simplifiedEquation;
        this.finalEquation = finalEquation;
        this.error = error;
        this.assumptions = assumptions;
    }

    public String getAntlrAst() {
        return antlrAst;
    }

    public String getSimplifiedAst() {
        return simplifiedAst;
    }

    public String getRawEquation() {
        return rawEquation;
    }

    public String getSimplifiedEquation() {
        return simplifiedEquation;
    }

    public String getFinalEquation() {
        return finalEquation;
    }

    public String getError() {
        return error;
    }

    public Map<String, String> getAssumptions() {
        return assumptions;
    }
}
