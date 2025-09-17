package org.eleks.interview.exercise.model.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.eleks.interview.exercise.model.ApplicationError;

import java.io.IOException;

import static org.eleks.interview.exercise.model.ErrorCode.*;

@Slf4j
public class ErrorDeserializer extends JsonDeserializer<ApplicationError> {
    @Override
    public ApplicationError deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode error = parser.getCodec().<JsonNode>readTree(parser).get("error");
        return new ApplicationError(switch(error.get("code").asInt()) {
            case 1002 -> MISSING_API_KEY;
            case 1005 -> API_NOT_FOUND;
            case 1006 -> CITY_NAME_MISCONFIGURATION;
            case 2006, 2008, 2009 -> INVALID_API_KEY;
            case 2007 -> EXCEEDED_API_CALLS_LIMIT;
            default -> UNKNOWN_ERROR;
        }, error.get("message").asText());
    }
}
