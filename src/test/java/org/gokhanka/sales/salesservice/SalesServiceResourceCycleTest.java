package org.gokhanka.sales.salesservice;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.gokhanka.sales.salesservice.sales.data.Message;
import org.gokhanka.sales.salesservice.sales.process.MessageProcessor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SalesServiceResourceCycleTest {

    private HttpServer server;
    private WebTarget  target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
        String responseMsg = target.path("service/servertest").request().get(String.class);
        assertEquals("Got it!", responseMsg);
    }

    /**
     * To simulate the whole lifecycle of the application by sending various types of messages
     */
    @Test
    public void testSaleStrore() {
        String responseMsg = null;
        for (int i = 0; i < MessageProcessor.getInstance().getReportingThreshold(); i++) {
            responseMsg = target.path("service/message").queryParam("messageType",
                                                                    "0").queryParam("productType",
                                                                                    "ELMA").queryParam("value",
                                                                                                       "2").request().get(String.class);
        }
        for (int i = 0; i < MessageProcessor.getInstance().getReportingThreshold(); i++) {
            responseMsg = target.path("service/message").queryParam("messageType",
                                                                    "0").queryParam("productType",
                                                                                    "Portakal").queryParam("value",
                                                                                                           "5").request().get(String.class);
        }
        for (int i = 0; i < MessageProcessor.getInstance().getReportingThreshold(); i++) {
            responseMsg = target.path("service/message").queryParam("messageType",
                                                                    "1").queryParam("productType",
                                                                                    "ELMA").queryParam("value",
                                                                                                       "2").queryParam("ocurrence",
                                                                                                                       "2").request().get(String.class);
        }
        for (int i = 0; i < MessageProcessor.getInstance().getReportingThreshold(); i++) {
            responseMsg = target.path("service/message").queryParam("messageType",
                                                                    "2").queryParam("productType",
                                                                                    "ELMA").queryParam("value",
                                                                                                       "2").queryParam("operator",
                                                                                                                       "0").request().get(String.class);
        }
        for (int i = 0; i < MessageProcessor.getInstance().getReportingThreshold(); i++) {
            responseMsg = target.path("service/message").queryParam("messageType",
                                                                    "2").queryParam("productType",
                                                                                    "PORTAKAL").queryParam("value",
                                                                                                           "1").queryParam("operator",
                                                                                                                           "2").request().get(String.class);
        }
        responseMsg = target.path("service/message").queryParam("messageType",
                                                                "0").queryParam("productType",
                                                                                "ELMA").queryParam("value",
                                                                                                   "2").request().get(String.class);
        assertEquals("NO MORE NEW REQUEST ACCEPTED", responseMsg);
    }
}
