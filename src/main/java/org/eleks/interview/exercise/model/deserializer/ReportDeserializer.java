package org.eleks.interview.exercise.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.eleks.interview.exercise.model.Report;

import java.io.IOException;

public class ReportDeserializer extends JsonDeserializer<Report> {
    @Override
    public Report deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode current = parser.getCodec().<JsonNode>readTree(parser).get("current");
        return new Report(current.get("temp_c").asDouble(), current.get("condition").get("text").asText(), current.get("humidity").asInt());
    }
}
