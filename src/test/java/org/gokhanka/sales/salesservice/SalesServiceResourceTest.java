package org.gokhanka.sales.salesservice;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.gokhanka.sales.salesservice.sales.process.MessageProcessor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import static org.junit.Assert.assertEquals;


public class SalesServiceResourceTest {

    private  HttpServer server;
    private  WebTarget target;
    @Before
    public  void setUp() throws Exception {
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
       // urlNameL = Main.BASE_URI + "service/message";
        MessageProcessor.getInstance().reWork();;
    }

    @After
    public  void tearDown() throws Exception {
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
     * To test 1 sale transaction of ELMA product
     */
    @Test
    public void testStoreSale() {
        String responseMsg = target.path("service/message").queryParam("messageType", "0").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        assertEquals("SUCCESS", responseMsg);
    }
    /**
     * To test 1 sale transaction of ELMA product with wrong input, to test validations
     */
    @Test
    public void testStoreSaleWrongInput() {
        String responseMsg = target.path("service/message").queryParam("messageType", "-1").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        assertEquals("ERROR", responseMsg);
    }
    @Test
    public void testStoreSaleWrongInput2() {
        String responseMsg = target.path("service/message").queryParam("messageType", "3").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        assertEquals("ERROR", responseMsg);
    }
    /**
     *  Multiple occurrence of a sale transaction is simulated
     *  first an store process is pushed then the multiple occurrence scenario applies
     */
    @Test
    public void testStoreSaleMultiple() {
        String responseMsg = target.path("service/message").queryParam("messageType", "0").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "1").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("ocurrence", "2").request().get(String.class);
        assertEquals("SUCCESS", responseMsg);
    }
    /**
     *  Scenario is continued from the previous step in testStoreSaleMultiple
     */
    @Test
    public void testStoreSaleAdjSale0() {
        String responseMsg = target.path("service/message").queryParam("messageType", "0").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "1").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("ocurrence", "2").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "2").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("operator", "0").request().get(String.class);
        assertEquals("SUCCESS", responseMsg);
    }
    /**
     *  Scenario is continued from the previous step in testStoreSaleMultiple, with different operand
     */
    @Test
    public void testStoreSaleAdjSale1() {
        String responseMsg = target.path("service/message").queryParam("messageType", "0").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "1").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("ocurrence", "2").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "2").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("operator", "1").request().get(String.class);
        assertEquals("SUCCESS", responseMsg);
    }
    /**
     *  Scenario is continued from the previous step in testStoreSaleMultiple, with different operand
     */
    @Test
    public void testStoreSaleAdjSale2() {
        String responseMsg = target.path("service/message").queryParam("messageType", "0").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "1").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("ocurrence", "2").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "2").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("operator", "2").request().get(String.class);
        assertEquals("SUCCESS", responseMsg);
    }
    @Test
    public void testStoreSaleWrongAdjSale() {
        String responseMsg = target.path("service/message").queryParam("messageType", "0").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "1").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("ocurrence", "2").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "2").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("operator", "3").request().get(String.class);
        assertEquals("ERROR", responseMsg);
    }
    @Test
    public void testStoreSaleWrongAdjSale2() {
        String responseMsg = target.path("service/message").queryParam("messageType", "0").queryParam("productType", "ELMA").queryParam("value", "2.0").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "1").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("ocurrence", "2").request().get(String.class);
        responseMsg = target.path("service/message").queryParam("messageType", "2").queryParam("productType", "ELMA").queryParam("value", "2.0").queryParam("operator", "-1").request().get(String.class);
        assertEquals("ERROR", responseMsg);
    }
}
