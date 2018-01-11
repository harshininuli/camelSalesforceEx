package com.mycompany.camel.blueprint;

import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class ProcessCSVResult {

	public static void main(String args[]) throws Exception {

		// create CamelContext
		CamelContext context = new DefaultCamelContext();

		// add our route to the CamelContext
		context.addRoutes(new RouteBuilder() {
			public void configure() {

				from("file:data/csv?noop=true&fileName=resultFailure.csv").split().tokenize("\n",10000)
				.unmarshal().csv()
				.convertBodyTo(List.class)
				.bean(CSVSplitterProcessor.class, "processBatchResults")
            	.end();
			}
		});
		
		// start the route and let it do its work
		context.start();
		Thread.sleep(10000);

		// stop the CamelContext
		context.stop();
	}

}
