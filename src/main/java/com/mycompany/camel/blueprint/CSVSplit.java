package com.mycompany.camel.blueprint;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.impl.DefaultCamelContext;

public class CSVSplit {

	public static void main(String args[])  throws Exception {

		// create CamelContext
		CamelContext context = new DefaultCamelContext();

		// add our route to the CamelContext
		context.addRoutes(new RouteBuilder() {
			public void configure() {

				CsvDataFormat csv = new CsvDataFormat();
				//csv.setHeader(new String[] { "Id", "Name" });
				csv.setDelimiter(',');
				csv.setQuoteDisabled(true); // maintains single quote as it is

				System.out.println("csv splitting files");
				from("file:data/csv?noop=true&fileName=mySampleData.csv").split().tokenize("\n", 10)
						// .parallelProcessing()
						.unmarshal().csv().convertBodyTo(List.class).bean(CSVSplitterProcessor.class, "addHeaderRow")
						.delay(10).marshal().csv()
						.to("file:data/csv?fileName=InputCSV-${date:now:yyyyMMddHHmmssSSSSSSSS}-${headers.uuid}.csv&fileExist=Append").end();

			}
		});

		// start the route and let it do its work
		context.start();
		Thread.sleep(10000);

		// stop the CamelContext
		context.stop();
	}

}
