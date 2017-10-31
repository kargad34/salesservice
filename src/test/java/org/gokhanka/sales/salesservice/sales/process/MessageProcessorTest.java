package org.gokhanka.sales.salesservice.sales.process;

import org.gokhanka.sales.salesservice.sales.data.AdjustmentOperator;
import org.gokhanka.sales.salesservice.sales.data.AdjustmentToSale;
import org.gokhanka.sales.salesservice.sales.data.Message;
import org.gokhanka.sales.salesservice.sales.data.MessageTypeEnum;
import org.gokhanka.sales.salesservice.sales.data.MultiOccurSale;
import org.gokhanka.sales.salesservice.sales.data.Sale;
import org.gokhanka.sales.salesservice.sales.process.DataStore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Time;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class MessageProcessorTest {

    private static DataStore        dInstance;
    private static MessageProcessor mInstance;

    @BeforeClass
    public static void setUp() throws Exception {
        dInstance = DataStore.getInstance();
        mInstance = MessageProcessor.getInstance();
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */

    @Test
    public void testMessageSale() {
        String product = "elma";
        float val = 3;
        Sale sale = new Sale(product, val);
        int initCount = 0;
        LinkedList<Sale> initial = dInstance.retrieveSales(sale);
        if (initial != null) {
            initCount = initial.size();
        }
        Message msg = new Message(MessageTypeEnum.SALE, sale);
        mInstance.putToQueue(msg);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        assertEquals(dInstance.retrieveSales(sale).size(), initCount + 1);
    }

    @Test
    public void testMessageMultipleOccur() {
        String product = "elma";
        float val = 3;
        MultiOccurSale sale = new MultiOccurSale(product, val, 3);
        int initCount = 0;
        LinkedList<Sale> initial = dInstance.retrieveSales(sale);
        if (initial != null) {
            initCount = initial.size();
        }
        Message msg = new Message(MessageTypeEnum.MULTI_OCCUR_SALE, sale);
        mInstance.putToQueue(msg);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        assertEquals(dInstance.retrieveSales(sale).size(), initCount + 3);
    }

    @Test
    public void testMessageAdjustSale() {
        String product = "elma";
        float val = 5;
        AdjustmentToSale sale = new AdjustmentToSale(product, val, AdjustmentOperator.MULTIPLY);
        float initCount = 0f;
        LinkedList<Sale> initial = dInstance.retrieveSales(sale);
        if (initial != null) {
            initCount = dInstance.getValueOfSales(product);
        } else {
            Sale salet = new Sale(product, val);
            Message msg = new Message(MessageTypeEnum.SALE, sale);
            mInstance.putToQueue(msg);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            initCount = val;
        }
        Message msg = new Message(MessageTypeEnum.ADJUSTMENT_OF_SALE, sale);
        mInstance.putToQueue(msg);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        assertEquals(dInstance.getValueOfSales(product), initCount * 5, 0.01f);
    }

    @Test
    public void testMessageAdjustSaleSubtract() {
        String product = "elma";
        float val = 1;
        AdjustmentToSale sale = new AdjustmentToSale(product, val, AdjustmentOperator.SUBTRACT);
        float initCount = 0f;
        LinkedList<Sale> initial = dInstance.retrieveSales(sale);
        if (initial != null) {
            initCount = dInstance.getValueOfSales(product);
        } else {
            Sale salet = new Sale(product, 5f);
            Message msg = new Message(MessageTypeEnum.SALE, sale);
            mInstance.putToQueue(msg);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            initCount = val;
        }
        Message msg = new Message(MessageTypeEnum.ADJUSTMENT_OF_SALE, sale);
        mInstance.putToQueue(msg);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        float expected = initCount - (dInstance.retrieveSales(sale).size() * val);
        assertEquals(dInstance.getValueOfSales(product), expected, 0.01f);
    }

    @Test
    public void testMessageAdjustSaleAdd() {
        String product = "elma";
        float val = 1;
        AdjustmentToSale sale = new AdjustmentToSale(product, val, AdjustmentOperator.SUM);
        float initCount = 0f;
        LinkedList<Sale> initial = dInstance.retrieveSales(sale);
        if (initial != null) {
            initCount = dInstance.getValueOfSales(product);
        } else {
            Sale salet = new Sale(product, 5f);
            Message msg = new Message(MessageTypeEnum.SALE, sale);
            mInstance.putToQueue(msg);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            initCount = val;
        }
        Message msg = new Message(MessageTypeEnum.ADJUSTMENT_OF_SALE, sale);
        mInstance.putToQueue(msg);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        float expected = initCount + (dInstance.retrieveSales(sale).size() * val);
        assertEquals(dInstance.getValueOfSales(product), expected, 0.01f);
    }
}
