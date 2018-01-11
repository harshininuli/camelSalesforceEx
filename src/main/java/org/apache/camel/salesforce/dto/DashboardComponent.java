/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Mon Mar 02 02:58:34 EST 2015
 */
package org.apache.camel.salesforce.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.codehaus.jackson.annotate.JsonProperty;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;

/**
 * Salesforce DTO for SObject DashboardComponent
 */
@XStreamAlias("DashboardComponent")
public class DashboardComponent extends AbstractSObjectBase {

    // DashboardId
    private String DashboardId;

    @JsonProperty("DashboardId")
    public String getDashboardId() {
        return this.DashboardId;
    }

    @JsonProperty("DashboardId")
    public void setDashboardId(String DashboardId) {
        this.DashboardId = DashboardId;
    }

}
