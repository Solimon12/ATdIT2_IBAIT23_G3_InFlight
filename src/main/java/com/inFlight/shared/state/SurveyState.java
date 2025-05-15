package com.inFlight.shared.state;

/**
 * SurveyState is a singleton class that manages the state of the survey trigger.
 * It provides methods to trigger the survey and check if it has been triggered.
 */
public class SurveyState {
    private static boolean surveyTriggered = false; // Default to false

    /**
     * Triggers the survey by setting the surveyTriggered flag to true.
     */
    public static void triggerSurvey() {
        surveyTriggered = true;
    }

    /**
     * Checks if the survey has been triggered.
     *
     * @return true if the survey has been triggered, false otherwise.
     */
    public static boolean isSurveyTriggered() {
        return surveyTriggered;
    }
}
