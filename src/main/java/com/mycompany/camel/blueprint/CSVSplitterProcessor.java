package com.mycompany.camel.blueprint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.camel.component.salesforce.SalesforceEndpointConfig;
import org.apache.camel.component.salesforce.api.dto.bulk.BatchInfo;
import org.apache.camel.component.salesforce.api.dto.bulk.BatchStateEnum;
import org.apache.camel.component.salesforce.api.dto.bulk.ContentType;
import org.apache.camel.component.salesforce.api.dto.bulk.JobInfo;
import org.apache.camel.component.salesforce.api.dto.bulk.OperationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVSplitterProcessor {

	@PropertyInject("ObjectName")
	String objectName;

	private static final Logger LOG = LoggerFactory.getLogger(CSVSplitterProcessor.class);
	static List<String> headers = new ArrayList<String>();
	static int success = 0, failed = 0;

	public void addHeaderRow(Exchange exchange) {
		exchange.getIn().setHeader("uuid", UUID.randomUUID().toString());
		List<List<String>> data = (List<List<String>>) exchange.getIn().getBody();
		if (data.get(0).contains("Name")) {
			System.out.println(data.get(0));
			for (String header : data.get(0)) {
				headers.add(header);
			}
			exchange.getIn().setHeader("csvheaders", headers);
		} else {
			data.add(0, headers);
		}

		System.out.println("list" + exchange.getIn().getBody());
	}

	public void processBatchResults(Exchange exchange) {
		exchange.getIn().setHeader("uuid", UUID.randomUUID().toString());
		List<List<String>> data = (List<List<String>>) exchange.getIn().getBody();
		for (List<String> row : data) {
			if (!(row.get(1).equals("Success")) && (row.get(1).equalsIgnoreCase("TRUE")))
				success++;
			else if (row.get(1).equalsIgnoreCase("FALSE"))
				failed++;

		}
		System.out.println("Total no.of records created/updated " + success);
		System.out.println("Total no.of records failed " + failed);
	}

	public void checkBatchStatus(Exchange exchange) throws Exception {

		BatchInfo batchInfo = exchange.getIn().getHeader("batchinfo", BatchInfo.class);
		LOG.info("getRequest result :: jobid " + batchInfo.getJobId());
		System.out.println("getRequest result :: jobid " + batchInfo.getJobId());

		while (!batchProcessed(batchInfo)) {
			ProducerTemplate template = exchange.getContext().createProducerTemplate();
			Thread.sleep(5000);
			batchInfo = template.requestBody("direct:getBatch", batchInfo, BatchInfo.class);
			LOG.info("batch info status " + batchProcessed(batchInfo));
			System.out.println("batch info status " + batchProcessed(batchInfo));
			exchange.getOut().setBody(batchInfo);
			template.stop();
		}
		exchange.getIn().setHeader(SalesforceEndpointConfig.JOB_ID, batchInfo.getJobId());
		exchange.getIn().setHeader(SalesforceEndpointConfig.BATCH_ID, batchInfo.getId());

	}
	
	public void setJobInfo(Exchange exchange) throws Exception
    {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setContentType(ContentType.CSV);
        jobInfo.setOperation(OperationEnum.UPSERT);
        jobInfo.setObject(objectName);
        jobInfo.setExternalIdFieldName("External_Id__c");
        exchange.getOut().setHeader("filename", exchange.getIn().getHeader("CamelFileName"));
        exchange.getOut().setBody(jobInfo);
    } 

	protected static boolean batchProcessed(BatchInfo batchInfo) {
		BatchStateEnum state = batchInfo.getState();
		return !(state == BatchStateEnum.QUEUED || state == BatchStateEnum.IN_PROGRESS);
	}
}
