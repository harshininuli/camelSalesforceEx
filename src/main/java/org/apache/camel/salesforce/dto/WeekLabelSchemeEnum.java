/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Mon Mar 02 02:58:34 EST 2015
 */
package org.apache.camel.salesforce.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist WeekLabelScheme
 */
public enum WeekLabelSchemeEnum {

    // NumberByYear
    NUMBERBYYEAR("NumberByYear"),
    // NumberByQuarter
    NUMBERBYQUARTER("NumberByQuarter"),
    // NumberByPeriod
    NUMBERBYPERIOD("NumberByPeriod");

    final String value;

    private WeekLabelSchemeEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static WeekLabelSchemeEnum fromValue(String value) {
        for (WeekLabelSchemeEnum e : WeekLabelSchemeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
