package com.mycompany.camel.blueprint;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class CamelSalesforceRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("timer://salesforceRunOnce?repeatCount=1&delay=5000")
		.routeId("SALESFORCE-ROUTE")
		.doTry()
		.to("salesforce:query?sObjectQuery=SELECT id, Name, Site from Account &sObjectClass=org.apache.camel.salesforce.dto.QueryRecordsAccount")
		.log(LoggingLevel.INFO, "Result"+"${body}")
		.endDoTry()
		.doCatch(Exception.class)
		.log(LoggingLevel.WARN,"In catch")
		.end();
		
	}

}
