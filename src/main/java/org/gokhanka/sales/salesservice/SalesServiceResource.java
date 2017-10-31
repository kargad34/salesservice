package org.gokhanka.sales.salesservice;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.gokhanka.sales.salesservice.sales.data.AdjustmentOperator;
import org.gokhanka.sales.salesservice.sales.data.AdjustmentToSale;
import org.gokhanka.sales.salesservice.sales.data.Message;
import org.gokhanka.sales.salesservice.sales.data.MessageTypeEnum;
import org.gokhanka.sales.salesservice.sales.data.MultiOccurSale;
import org.gokhanka.sales.salesservice.sales.data.Sale;
import org.gokhanka.sales.salesservice.sales.process.MessageProcessor;

/**
 * Root resource (exposed at "service" path)
 */
@Path("service")
public class SalesServiceResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("servertest")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    /**
     * 
     * @param msgType : to determine the type of message, please see enum MessageTypeEnum
     * @param productType : to determine the productType : user can give any String of alpha numerics, it is changed to lower case in internal process
     * @param value : float value for the product
     * @param operator : used when message is Adjust Sale please see enum AdjustmentOperator
     * @param occur : for the multipple occured sales, integer
     * @return
     */
    @GET
    @Path("message")
    @Produces(MediaType.TEXT_PLAIN)
    public String sendMessage(@QueryParam("messageType") @NotNull @Min(0) @Max(2) int msgType,
                              @QueryParam("productType") @NotNull @Pattern(regexp = "^[a-zA-Z0-9]*$") String productType,
                              @DefaultValue("0.0") @QueryParam("value") float value,
                              @QueryParam("operator") @Min(0) @Max(2) Integer operator,
                              @QueryParam("ocurrence") @Min(2) Integer occur) {
        int operand = -1;
        int occurence = -1;
        if (operator != null)
            operand = operator.intValue();
        if (occur != null)
            occurence = occur.intValue();
        if (MessageProcessor.getInstance().isWorking()) {
            if (processIncomingMessage(msgType,
                                       productType.toLowerCase(),
                                       value,
                                       operand,
                                       occurence))
                return "SUCCESS";
            else
                return "ERROR";
        } else {
            return "NO MORE NEW REQUEST ACCEPTED";
        }
    }

    /**
     *   processing of the validated incoming message
     *   Asynchronously (unless the incoming queue is full) handling the request by putting it to a message queue
     * @param msgType
     * @param productType
     * @param value
     * @param operand
     * @param occur
     * @return
     */
    private boolean processIncomingMessage(int msgType, String productType, float value,
                                           int operand, int occur) {
        System.out.println("Valid Message received and it will be processed");
        boolean result = true;
        MessageTypeEnum msgTypeEnum = MessageTypeEnum.getEnum(msgType);
        Message msg = null;
        switch (msgTypeEnum) {
        case SALE:
            msg = new Message(MessageTypeEnum.SALE, new Sale(productType, value));
            break;
        case MULTI_OCCUR_SALE:
            if (occur >= 2)
                msg = new Message(MessageTypeEnum.MULTI_OCCUR_SALE,
                                  new MultiOccurSale(productType, value, occur));
            break;
        case ADJUSTMENT_OF_SALE:
            AdjustmentOperator operEnum = AdjustmentOperator.getEnum(operand);
            if (operand >= 0 && operEnum.getId() != AdjustmentOperator.UNDEFINED.getId())
                msg = new Message(MessageTypeEnum.ADJUSTMENT_OF_SALE,
                                  new AdjustmentToSale(productType, value, operEnum));
            break;
        default:
            break;
        }
        if (msg == null) {
            result = false;
        } else {
            result = MessageProcessor.getInstance().putToQueue(msg);
        }
        return result;
    }
}
