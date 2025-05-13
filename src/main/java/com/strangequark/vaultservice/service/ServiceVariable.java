package com.strangequark.vaultservice.service;

import jakarta.persistence.*;

@Entity
@Table(name = "service_variables")
public class ServiceVariable {

    public ServiceVariable() {

    }

    public ServiceVariable(String serviceName, String environment, String key, String value) {
        this.serviceName = serviceName;
        this.environment = environment;
        this.key = key;
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String serviceName;
    private String environment;
    private String key;
    private String value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
