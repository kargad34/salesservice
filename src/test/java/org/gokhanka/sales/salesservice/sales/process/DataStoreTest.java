package org.gokhanka.sales.salesservice.sales.process;

import org.gokhanka.sales.salesservice.sales.data.AdjustmentOperator;
import org.gokhanka.sales.salesservice.sales.data.AdjustmentToSale;
import org.gokhanka.sales.salesservice.sales.data.Sale;
import org.gokhanka.sales.salesservice.sales.process.DataStore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.LinkedList;

public class DataStoreTest {

    private static DataStore instance;

    @BeforeClass
    public static void setUp() throws Exception {
        instance = DataStore.getInstance();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        instance = null;
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testStoreSale() {
        String product = "elma";
        float val = 3;
        Sale sale = new Sale(product, val);
        LinkedList<Sale> initial = instance.retrieveSales(sale);
        int initialCount = 0;
        if (initial != null)
            initialCount = initial.size();
        instance.storeSale(sale, 1);
        initial = instance.retrieveSales(sale);
        assertEquals(initialCount + 1, initial.size());
    }

    /**
     * Test to see if the multiple product store function is working properly
     */
    @Test
    public void testMultipleProductStoreSale() {
        String product = "karpuz";
        float val = 3;
        Sale sale = new Sale(product, val);
        product = "portakal";
        val = 4;
        Sale sale2 = new Sale(product, val);
        product = "mandalina";
        val = 2;
        Sale sale3 = new Sale(product, val);
        LinkedList<Sale> initial1 = instance.retrieveSales(sale);
        int initialCount1 = 0;
        if (initial1 != null)
            initialCount1 = initial1.size();
        LinkedList<Sale> initial2 = instance.retrieveSales(sale2);
        int initialCount2 = 0;
        if (initial2 != null)
            initialCount2 = initial2.size();
        LinkedList<Sale> initial3 = instance.retrieveSales(sale3);
        int initialCount3 = 0;
        if (initial3 != null)
            initialCount3 = initial3.size();
        int totalInitial = initialCount1 + initialCount2 + initialCount3;
        for (int i = 0; i < 3; i++) {
            instance.storeSale(sale, 1);
            instance.storeSale(sale2, 1);
            instance.storeSale(sale3, 1);
        }
        int totalLast = instance.retrieveSales(sale).size() + instance.retrieveSales(sale2).size()
                + instance.retrieveSales(sale3).size();
        assertEquals(totalInitial + 9, totalLast);
    }

    /**
     *  Same Scenario with testMultipleProductStoreSale but this time application feature is used for multiple insertion
     */
    @Test
    public void testMultipleProductStoreSaleOccur() {
        String product = "karpuz";
        float val = 3;
        Sale sale = new Sale(product, val);
        product = "portakal";
        val = 4;
        Sale sale2 = new Sale(product, val);
        product = "mandalina";
        val = 2;
        Sale sale3 = new Sale(product, val);
        LinkedList<Sale> initial1 = instance.retrieveSales(sale);
        int initialCount1 = 0;
        if (initial1 != null)
            initialCount1 = initial1.size();
        LinkedList<Sale> initial2 = instance.retrieveSales(sale2);
        int initialCount2 = 0;
        if (initial2 != null)
            initialCount2 = initial2.size();
        LinkedList<Sale> initial3 = instance.retrieveSales(sale3);
        int initialCount3 = 0;
        if (initial3 != null)
            initialCount3 = initial3.size();
        int totalInitial = initialCount1 + initialCount2 + initialCount3;
        instance.storeSale(sale, 3);
        instance.storeSale(sale2, 3);
        instance.storeSale(sale3, 3);
        int totalLast = instance.retrieveSales(sale).size() + instance.retrieveSales(sale2).size()
                + instance.retrieveSales(sale3).size();
        assertEquals(totalInitial + 9, totalLast);
    }

    /**
     * to test AdjustmentOperator.MULTIPLY on data management
     */
    @Test
    public void testAdjustSaleMultiply() {
        String product = "elma";
        float val = 3;
        Sale sale = new Sale(product, val);
        instance.storeSale(sale, 1);
        LinkedList<Sale> initial = instance.retrieveSales(sale);
        float initVal = 0;
        if (initial == null)
            instance.storeSale(sale, 1);
        else {
            Iterator<Sale> it = initial.iterator();
            while (it.hasNext()) {
                initVal = initVal + it.next().getValue();
            }
        }
        instance.adjustmentToSale(new AdjustmentToSale("elma", 2f, AdjustmentOperator.MULTIPLY));
        assertEquals((initVal * 2f), instance.getValueOfSales("elma"), 0.01f);
    }

    /**
     *  test of adjustment of SUM operation on data objects
     */
    @Test
    public void testAdjustSaleSum() {
        String product = "elma";
        float val = 3;
        Sale sale = new Sale(product, val);
        LinkedList<Sale> initial = instance.retrieveSales(sale);
        instance.storeSale(sale, 1);
        float initVal = 0;
        if (initial == null)
            instance.storeSale(sale, 1);
        else {
            Iterator<Sale> it = initial.iterator();
            while (it.hasNext()) {
                initVal = initVal + it.next().getValue();
            }
        }
        instance.adjustmentToSale(new AdjustmentToSale("elma", 5f, AdjustmentOperator.SUM));
        assertEquals((initVal + (5f * instance.retrieveSales(sale).size())),
                     instance.getValueOfSales("elma"),
                     0.01f);
    }

    /**
     *  subtraction operation is tested
     */
    @Test
    public void testAdjustSaleSubtract() {
        String product = "elma";
        float val = 3;
        Sale sale = new Sale(product, val);
        LinkedList<Sale> initial = instance.retrieveSales(sale);
        instance.storeSale(sale, 1);
        float initVal = 0;
        if (initial == null)
            instance.storeSale(sale, 1);
        else {
            Iterator<Sale> it = initial.iterator();
            while (it.hasNext()) {
                initVal = initVal + it.next().getValue();
            }
        }
        instance.adjustmentToSale(new AdjustmentToSale("elma", 5f, AdjustmentOperator.SUBTRACT));
        float expected = (initVal - (5f * instance.retrieveSales(sale).size()));
        if (expected < 0)
            expected = 0.0f;
        assertEquals(expected, instance.getValueOfSales("elma"), 0.01f);
    }

    /**
     * to see if the reporting function is working as expected for adjustments
     */
    @Test
    public void testAdjustReport() {
        String product = "elma";
        float val = 3;
        Sale sale = new Sale(product, val);
        LinkedList<Sale> initial = instance.retrieveSales(sale);
        int initCount = 0;
        if (initial == null)
            instance.storeSale(sale, 1);
        else {
            LinkedList<AdjustmentToSale> initAdj = instance.retrieveAdjReports(sale);
            initCount = initAdj.size();
        }
        instance.adjustmentToSale(new AdjustmentToSale("elma", 2f, AdjustmentOperator.MULTIPLY));

        assertEquals(initCount + 1, instance.retrieveAdjReports(sale).size());
    }

    /**
     * to see if the reporting function is working as expected for sale transactions
     */
    @Test
    public void testSaleReportCounts() {
        String product = "karpuz";
        float val = 3;
        Sale sale = new Sale(product, val);
        product = "portakal";
        val = 4;
        Sale sale2 = new Sale(product, val);
        product = "mandalina";
        val = 2;
        Sale sale3 = new Sale(product, val);

        for (int i = 0; i < 3; i++) {
            instance.storeSale(sale, 1);
            instance.storeSale(sale2, 1);
            instance.storeSale(sale3, 1);
        }
        int totalLast = instance.retrieveSales(sale).size() + instance.retrieveSales(sale2).size()
                + instance.retrieveSales(sale3).size();
        assertEquals(instance.getTotalNumberOfSales("karpuz")
                + instance.getTotalNumberOfSales("portakal")
                + instance.getTotalNumberOfSales("mandalina"), totalLast);
    }

    /**
     * helper methods of reporting of sales transactions tested
     */
    @Test
    public void testSaleReporValuest() {
        String product = "karpuz";
        float val = 3;
        Sale sale = new Sale(product, val);
        product = "portakal";
        val = 4;
        Sale sale2 = new Sale(product, val);
        product = "mandalina";
        val = 2;
        Sale sale3 = new Sale(product, val);
        LinkedList<Sale> initial1 = instance.retrieveSales(sale);
        float initialVal1 = 0;
        if (initial1 != null) {
            Iterator<Sale> it = initial1.iterator();
            while (it.hasNext()) {
                initialVal1 = initialVal1 + it.next().getValue();
            }
        }
        LinkedList<Sale> initial2 = instance.retrieveSales(sale2);
        float initialVal2 = 0;
        if (initial2 != null) {
            Iterator<Sale> it = initial2.iterator();
            while (it.hasNext()) {
                initialVal2 = initialVal2 + it.next().getValue();
            }
        }
        LinkedList<Sale> initial3 = instance.retrieveSales(sale3);
        float initialVal3 = 0;
        if (initial3 != null) {
            Iterator<Sale> it = initial3.iterator();
            while (it.hasNext()) {
                initialVal3 = initialVal3 + it.next().getValue();
            }
        }
        float totalInitial = initialVal1 + initialVal2 + initialVal3;
        for (int i = 0; i < 3; i++) {
            instance.storeSale(sale, 1);
            instance.storeSale(sale2, 1);
            instance.storeSale(sale3, 1);
        }

        float totalLast = instance.getValueOfSales("mandalina")
                + instance.getValueOfSales("portakal") + instance.getValueOfSales("karpuz");
        assertEquals(totalInitial + 27.0f, totalLast, 0.0f);
    }
}
