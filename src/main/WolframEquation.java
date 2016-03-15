package main;

public class WolframEquation {
    private String finalEquation;
    private String error;

    public WolframEquation(String finalEquation, String error) {
        this.finalEquation = finalEquation;
        this.error = error;
    }

    public String getFinalEquation() {
        return finalEquation;
    }

    public String getError() {
        return error;
    }
}
