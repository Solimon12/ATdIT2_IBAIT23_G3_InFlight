package com.inFlight.shared.protocol;

import com.inFlight.shared.protocol.MessageEnvelope;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageEnvelopeTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        MessageEnvelope envelope = new MessageEnvelope();

        JsonObject data = new JsonObject();
        data.addProperty("key", "value");

        envelope.setAction("TEST_ACTION");
        envelope.setData(data);

        assertEquals("TEST_ACTION", envelope.getAction());
        assertEquals("value", envelope.getData().get("key").getAsString());
    }

    @Test
    void testAllArgsConstructor() {
        JsonObject data = JsonParser.parseString("{\"info\":\"test\"}").getAsJsonObject();

        MessageEnvelope envelope = new MessageEnvelope("DO_SOMETHING", data);

        assertEquals("DO_SOMETHING", envelope.getAction());
        assertNotNull(envelope.getData());
        assertEquals("test", envelope.getData().get("info").getAsString());
    }
}