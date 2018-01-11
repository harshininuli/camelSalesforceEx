
package com.mycompany.camel.blueprint;

import java.io.IOException;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.salesforce.SalesforceComponent;
import org.apache.camel.component.salesforce.SalesforceEndpointConfig;
import org.apache.camel.component.salesforce.api.dto.Version;
import org.apache.camel.component.salesforce.api.dto.Versions;
import org.apache.camel.salesforce.dto.Account;
import org.apache.camel.salesforce.dto.Opportunity;
import org.apache.camel.salesforce.dto.QueryRecordsAccount;
import org.apache.camel.salesforce.dto.QueryRecordsOpportunity;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesforceTest extends CamelTestSupport {
	private static final Logger LOG = LoggerFactory.getLogger(SalesforceTest.class);
    
	@Override
    public boolean isCreateCamelContextPerClass() {
        // only create the context once for this class
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        // create the test component
        createComponent();
        return doCreateRouteBuilder();

    }

    protected void createComponent() throws IllegalAccessException, IOException {
        // create the component
        SalesforceComponent component = new SalesforceComponent();
        final SalesforceEndpointConfig config = new SalesforceEndpointConfig();
        config.setApiVersion(System.getProperty("apiVersion", SalesforceEndpointConfig.DEFAULT_VERSION));
        component.setConfig(config);
        component.setLoginConfig(LoginConfig.getLoginConfig());

        // set DTO package
        component.setPackages(new String[] {
            Opportunity.class.getPackage().getName()
        });

        // add it to context
        context().addComponent("salesforce", component);
    }
    
    @Test
    public void testQuery() throws Exception {
        doTestQuery("");
    }

    private void doTestQuery(String suffix) throws InterruptedException {
        QueryRecordsOpportunity queryRecords = template().requestBody("direct:query" + suffix, null,
            QueryRecordsOpportunity.class);
        System.out.println("Running Query: query?sObjectQuery=SELECT id,name from Opportunity&sObjectClass=" + QueryRecordsOpportunity.class.getName());
        assertNotNull(queryRecords);
        LOG.debug("ExecuteQuery: {}", queryRecords);
        ObjectMapper mapper = new ObjectMapper();
        try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryRecords));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    /*@Test
    public void testUpdateAccount() throws Exception {
        doTestUpdateAccount("");
    }*/
    

    private void doTestUpdateAccount(String suffix) throws InterruptedException {
    	 Account a = new Account();
    	 a.setId("0017F00000ND4ktQAD");
         a.setSite("vskp");
         assertNull(template().requestBodyAndHeader("direct:updateSObject", a,
                 SalesforceEndpointConfig.SOBJECT_ID, "0017F00000ND4ktQAD"));
    	
      }
    
   @Test
    public void testQueryAccount() throws Exception {
        doTestQueryAccount("");
    }

    private void doTestQueryAccount(String suffix) throws InterruptedException {
        QueryRecordsAccount queryRecords = template().requestBody("direct:queryAccount" + suffix, null,
            QueryRecordsAccount.class);
        System.out.println("Running Query: salesforce:query?sObjectQuery=SELECT Id,Name FROM Account&amp;sObjectClass=org.apache.camel.salesforce.dto.QueryRecordsAccount");
        assertNotNull(queryRecords);
        LOG.debug("ExecuteQuery: {}", queryRecords);
        ObjectMapper mapper = new ObjectMapper();
        try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryRecords));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        }
    
    @Test
    public void testGetVersions() throws Exception {
        doTestGetVersions("");
    }

    @SuppressWarnings("unchecked")
    private void doTestGetVersions(String suffix) throws Exception {
        // test getVersions doesn't need a body
        // assert expected result
        Object o = template().requestBody("direct:getVersions" + suffix, (Object) null);
        
        List<Version> versions = null;
        if (o instanceof Versions) {
            versions = ((Versions) o).getVersions();
        } else {
            versions = (List<Version>) o;
        }
        assertNotNull(versions);
        LOG.debug("Versions: {}", versions);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(versions));
    }
    protected RouteBuilder doCreateRouteBuilder() throws Exception {

        // create test route
        return new RouteBuilder() {
            public void configure() {

                // testGetVersion
                from("direct:getVersions")
                    .to("salesforce:getVersions");
                
                // testQuery
                from("direct:query")
                      .to("salesforce:query?sObjectQuery=SELECT id,name from Opportunity&sObjectClass=" + QueryRecordsOpportunity.class.getName())
                      .to("log:Received update notification for ${body.name}");
                
                from("direct:queryAccount")
                .to("salesforce:query?sObjectQuery=SELECT Id,Name,Site FROM Account&sObjectClass=org.apache.camel.salesforce.dto.QueryRecordsAccount")
                .to("log:Received update notification for ${body.name}");
               
                from("direct:updateSObject")
                .to("salesforce:updateSObject?sObjectName=Account");

            }
        };
    }
}
