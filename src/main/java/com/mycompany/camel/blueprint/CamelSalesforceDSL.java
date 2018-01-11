package com.mycompany.camel.blueprint;

import java.io.IOException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.SalesforceEndpointConfig;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.salesforce.dto.Account;
import org.apache.camel.salesforce.dto.QueryRecordsAccount;


public class CamelSalesforceDSL {
	public static void main(String args[]) throws Exception {
		
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        createComponent(context);
       
         context.addRoutes(new RouteBuilder() {
            public void configure() {
            	 
            	from("timer://runOnce?repeatCount=1&delay=5000")
            	  .doTry()
            	  .to("salesforce:query?sObjectQuery=SELECT id, Name, Site from Account &sObjectClass=org.apache.camel.salesforce.dto.QueryRecordsAccount")
            	  .log(LoggingLevel.INFO, "Result"+"${body}")
         		.process(new Processor() {
					public void process(Exchange exchange) throws Exception {
						 QueryRecordsAccount queryRecords = (QueryRecordsAccount)exchange.getIn().getBody();
						 System.out.println("Accounts Result "+ exchange.getIn().getBody());
						/* Account a = new Account();
						 System.out.println("ID ::"+ queryRecords.getRecords().get(0).getId());
				    	 a.setId(queryRecords.getRecords().get(0).getId());
				         a.setSite("abcd");
				         exchange.getIn().setBody(a);
				         exchange.getIn().setHeader(SalesforceEndpointConfig.SOBJECT_ID, queryRecords.getRecords().get(0).getId());*/
					}
				})
				/* .to("salesforce:updateSObject?sObjectName=Account&sObjectId='0017F00000ND4ktQAD'")
				  .log(LoggingLevel.INFO, "Result"+"${body}")*/
            	 .endDoTry()
            	 .doCatch(Exception.class)
            	  .log(LoggingLevel.WARN,"In catch")
            	  .end();
            	
            	/*final BatchInfo[] bi = new BatchInfo[1];
            	from("timer://runOnce?repeatCount=1&delay=10")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        JobInfo jobInfo = new JobInfo();
            	        jobInfo.setContentType(ContentType.CSV);
            	        jobInfo.setOperation(OperationEnum.QUERY);
            	        jobInfo.setObject("Account");
            	        jobInfo.setConcurrencyMode(ConcurrencyModeEnum.PARALLEL);
            	        exchange.getOut().setBody(jobInfo);
            	    }

            	})
            	.to("salesforce:createJob")
            	.to("salesforce:createBatchQuery?sObjectQuery=select Id,Name,Type,BillingCity,BillingState," +
            	        "BillingPostalCode,BillingCountry,Phone from Account")
            	//.delay(10000)
            	.to("salesforce:getBatch")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        BatchInfo batchInfo = exchange.getIn().getBody(BatchInfo.class);
            	        bi[0] = batchInfo;
            	        exchange.getOut().setBody(batchInfo);

            	    }

            	})
            	.to("salesforce:getQueryResultIds")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        if (exchange.getException() != null)
            	        {
            	            exchange.getException().printStackTrace();
            	        }
            	        System.out.println("getQueryResultIds"+ exchange.getIn().getBody());
            	        Collection resultIds = exchange.getIn().getBody(Collection.class);
            	        List<String> resultIdslist =  exchange.getIn().getBody(List.class);
            	        System.out.println("resultIdslist"+resultIdslist.get(0));
            	        String resultId = (String) resultIds.iterator().next();
            	        exchange.getOut().setHeader(SalesforceEndpointConfig.RESULT_ID, resultId);
            	        exchange.getOut().setHeader(SalesforceEndpointConfig.JOB_ID, bi[0].getJobId());
            	        exchange.getOut().setHeader(SalesforceEndpointConfig.BATCH_ID, bi[0].getId());
            	        exchange.getOut().setBody(exchange.getIn().getBody());
            	    }

            	})
            	.to("salesforce:getQueryResult")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        

            	})
            	.end();*/
            	//.to("stream:out");
            }
        });
      
        // start the route and let it do its work
        context.start();
        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();
    }
	
	protected static void createComponent(CamelContext context) throws IllegalAccessException, IOException {
        // create the component
        SalesforceComponent component = new SalesforceComponent();
        final SalesforceEndpointConfig config = new SalesforceEndpointConfig();
        config.setApiVersion(System.getProperty("apiVersion", SalesforceEndpointConfig.DEFAULT_VERSION));
        component.setConfig(config);
        component.setLoginConfig(LoginConfig.getLoginConfig());

        // set DTO package
        component.setPackages(new String[] {
            Account.class.getPackage().getName()
        });

        // add it to context
        context.addComponent("salesforce", component);
    }
}
