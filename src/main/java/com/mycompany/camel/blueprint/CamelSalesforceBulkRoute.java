package com.mycompany.camel.blueprint;

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.salesforce.SalesforceEndpointConfig;
import org.apache.camel.component.salesforce.api.dto.bulk.BatchInfo;
import org.apache.camel.component.salesforce.api.dto.bulk.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelSalesforceBulkRoute extends RouteBuilder {
	
	@PropertyInject("FilePath")
	String filePath;
	private static final Logger LOG = LoggerFactory.getLogger(CamelSalesforceBulkRoute.class);
	@Override
	public void configure() throws Exception {
		
	from("file:" + filePath +"?noop=true").routeId("SALESFORCE-BULK-ROUTE")
    	.choice()
    		.when(simple("${in.headers.CamelFileName} contains 'mySample'"))
    		.to("direct-vm:createSalesforcebulkdata")
    	.otherwise()
    		.log(LoggingLevel.WARN, "invalid file")
    .end();
   
    final BatchInfo[] bi = new BatchInfo[1];
    
    from("direct-vm:createSalesforcebulkdata")
    	.bean(CSVSplitterProcessor.class, "setJobInfo")
    	.to("salesforce:createJob")
    	.process(new Processor() {

    	    public void process(Exchange exchange) throws Exception
    	    {
    	        JobInfo jobinfo = exchange.getIn().getBody(JobInfo.class);
    	        String filename =exchange.getIn().getHeader("filename",String.class);
    	        LOG.info("filename :: "+filename);
    	        System.out.println("filename :: "+filename);
    	        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(filename);   
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
    	        LOG.info("initial batch state :: "+ batchInfo.getState());
    	        System.out.println("initial batch state :: "+ batchInfo.getState());
    	    }

    	})
    	.to("salesforce:getRequest")
    	.bean(CSVSplitterProcessor.class, "checkBatchStatus")
    	.to("salesforce:getResults")
    	.process(new Processor() {
    	    public void process(Exchange exchange) throws Exception
    	    {
    	    	 InputStream results = exchange.getIn().getBody(InputStream.class);
    	    	 LOG.info("Result captured :: " + results.toString());
    	    }

    	})
    	.to("file:"+filePath+"?noop=true&fileName=result.csv&fileExist=Append")
    	.end();
    
    
    	from("direct:getBatch").
    	to("salesforce:getBatch")
    	.process(new Processor() {

    	    public void process(Exchange exchange) throws Exception
    	    {
    	        BatchInfo batchInfo = exchange.getIn().getBody(BatchInfo.class);
    	        bi[0] = batchInfo;
    	        LOG.info("check state :: "+ batchInfo.getState());
    	        System.out.println("check state :: "+ batchInfo.getState());
    	    }

    	}).end();
		
	}

}
