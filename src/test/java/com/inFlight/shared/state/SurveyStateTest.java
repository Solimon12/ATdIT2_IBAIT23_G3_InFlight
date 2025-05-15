package com.inFlight.shared.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SurveyStateTest {

    @BeforeEach
    void resetSurveyState() throws Exception {
        // Reset static state using reflection (since there's no reset method)
        var field = SurveyState.class.getDeclaredField("surveyTriggered");
        field.setAccessible(true);
        field.setBoolean(null, false);
    }

    @Test
    void testSurveyInitiallyNotTriggered() {
        assertFalse(SurveyState.isSurveyTriggered(), "Survey should not be triggered initially.");
    }

    @Test
    void testTriggerSurveySetsFlagToTrue() {
        SurveyState.triggerSurvey();
        assertTrue(SurveyState.isSurveyTriggered(), "Survey should be triggered after calling triggerSurvey().");
    }

    @Test
    void testTriggerSurveyIsIdempotent() {
        SurveyState.triggerSurvey();
        SurveyState.triggerSurvey(); // calling multiple times
        assertTrue(SurveyState.isSurveyTriggered(), "Survey should remain triggered.");
    }
}