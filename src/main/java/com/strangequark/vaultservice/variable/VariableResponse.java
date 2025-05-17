package com.strangequark.vaultservice.variable;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class VariableResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String serviceName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String environmentName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String variableKey;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String variableValue;

    public VariableResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public VariableResponse(Variable variable) {
        this();
        serviceName = variable.getEnvironment().getService().getName();
        environmentName = variable.getEnvironment().getName();
        variableKey = variable.getKey();
        variableValue = variable.getValue();
    }
}
