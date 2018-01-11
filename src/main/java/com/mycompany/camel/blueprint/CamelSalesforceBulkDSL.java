package com.mycompany.camel.blueprint;

import java.io.IOException;
import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.SalesforceEndpointConfig;
import org.apache.camel.component.salesforce.api.dto.bulk.BatchInfo;
import org.apache.camel.component.salesforce.api.dto.bulk.BatchStateEnum;
import org.apache.camel.component.salesforce.api.dto.bulk.ContentType;
import org.apache.camel.component.salesforce.api.dto.bulk.JobInfo;
import org.apache.camel.component.salesforce.api.dto.bulk.OperationEnum;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.salesforce.dto.Account;


public class CamelSalesforceBulkDSL {
	public static void main(String args[]) throws Exception {
		
        // create CamelContext
        CamelContext context = new DefaultCamelContext();
        createComponent(context);
       
         context.addRoutes(new RouteBuilder() {
            public void configure() {
            	 
            	from("file:src/main/resources?noop=true")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	    	System.out.println("CamelFileName "+ exchange.getIn().getHeader("CamelFileName"));
            	    	//exchange.getIn().setHeader("filename", exchange.getIn().getHeader("CamelFileName"));
            	    }

            	})
            	.choice()
            	.when(simple("${in.headers.CamelFileName} contains 'mySample'"))
            	 .to("direct-vm:createSalesforcebulkdata")
            	 .otherwise()
            	 .log(LoggingLevel.WARN, "invalid file")
            	.end();
           
            final BatchInfo[] bi = new BatchInfo[1];
            from("direct-vm:createSalesforcebulkdata")
            //from("timer://runOnce?repeatCount=1&delay=10")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        JobInfo jobInfo = new JobInfo();
            	        jobInfo.setContentType(ContentType.CSV);
            	        jobInfo.setOperation(OperationEnum.UPSERT);
            	        jobInfo.setObject("Account");
            	        jobInfo.setExternalIdFieldName("External_Id__c");
            	        exchange.getOut().setHeader("filename", exchange.getIn().getHeader("CamelFileName"));
            	        exchange.getOut().setBody(jobInfo);
            	    } 

            	})
            	.to("salesforce:createJob")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        JobInfo jobinfo = exchange.getIn().getBody(JobInfo.class);
            	        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            	        String filename =exchange.getIn().getHeader("filename",String.class);
            	        System.out.println("filename"+filename);
            	     
            	        InputStream stream = classLoader.getResourceAsStream(filename);   
            	        System.out.println("input" + stream);
            	        exchange.getIn().setHeader(SalesforceEndpointConfig.JOB_ID, jobinfo.getId());
            	        exchange.getIn().setHeader(SalesforceEndpointConfig.CONTENT_TYPE, jobinfo.getContentType());
            	        exchange.getIn().setBody(stream);
            	        
            	    }

            	})
            	.to("salesforce:createBatch")
            	.to("salesforce:getBatch")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        BatchInfo batchInfo = exchange.getIn().getBody(BatchInfo.class);
            	        bi[0] = batchInfo;
            	        exchange.getOut().setHeader("batchinfo", batchInfo);
            	        exchange.getOut().setBody(batchInfo);
            	       System.out.println("state"+ batchInfo.getState());
            	    }

            	})
            	.to("salesforce:getRequest")
            	//.bean(CSVSplitterProcessor.class, "checkBatchStatus")
            	.process(new Processor() {
            	    public void process(Exchange exchange) throws Exception
            	    {
            	    	BatchInfo batchInfo = exchange.getIn().getHeader("batchinfo", BatchInfo.class);
            	    	InputStream results = exchange.getIn().getBody(InputStream.class);
             	    	System.out.println("getRequest result :: "+results.toString() + "jobid" + batchInfo.getJobId());
             	    	
            	    	 while (!batchProcessed(batchInfo)) {
            	             Thread.sleep(5000);
            	             System.out.println("Processed" + batchProcessed(batchInfo));
            	             exchange.getOut().setBody(batchInfo);
            	         }
            	    	 exchange.getIn().setHeader(SalesforceEndpointConfig.JOB_ID, batchInfo.getJobId());
            	    	 exchange.getIn().setHeader(SalesforceEndpointConfig.BATCH_ID, batchInfo.getId());
            	    }

            	})
            	.to("salesforce:getResults")
            	.process(new Processor() {
            	    public void process(Exchange exchange) throws Exception
            	    {
            	    	 InputStream results = exchange.getIn().getBody(InputStream.class);
            	    	System.out.println("getResult result ::" + results.toString());
            	    }

            	})
            	.to("file:src/main/resources?noop=true&fileName=result.csv")
            	.end();
            
            
            	from("direct:getBatch").
            	to("salesforce:getBatch")
            	.process(new Processor() {

            	    public void process(Exchange exchange) throws Exception
            	    {
            	        BatchInfo batchInfo = exchange.getIn().getBody(BatchInfo.class);
            	        bi[0] = batchInfo;
            	       System.out.println("state"+ batchInfo.getState());
            	    }

            	}).end();
            }
        });
      
        // start the route and let it do its work
        context.start();
        
        Thread.sleep(1000*60*60*10);

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
	protected static boolean batchProcessed(BatchInfo batchInfo) {
        BatchStateEnum state = batchInfo.getState();
        return !(state == BatchStateEnum.QUEUED || state == BatchStateEnum.IN_PROGRESS);
    }
}
