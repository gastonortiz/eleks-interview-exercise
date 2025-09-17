package org.eleks.interview.exercise.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eleks.interview.exercise.model.deserializer.ReportDeserializer;

@JsonDeserialize(using = ReportDeserializer.class)
public record Report(double temp, String description, int humidity) {
}
