package org.gokhanka.sales.salesservice.sales.process;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.gokhanka.sales.salesservice.sales.data.AdjustmentToSale;
import org.gokhanka.sales.salesservice.sales.data.Sale;

public class DataStore {

    private Hashtable<String, LinkedList<Sale>>             storageSale       = new Hashtable<String, LinkedList<Sale>>();
    private Hashtable<String, LinkedList<AdjustmentToSale>> storageAdjustment = new Hashtable<String, LinkedList<AdjustmentToSale>>();
    private static Object                                   mutex             = new Object();
    private static DataStore                                instance          = null;

    private DataStore() {
    }

    /**
     * Access interface to singleton's instance
     * @return
     */
    public static DataStore getInstance() {
        if (instance == null) {
            synchronized (mutex) {
                if (instance == null)
                    instance = new DataStore();
            }
        }
        return instance;
    }

    /**
     * Sales are stored in Hashtable with a value of a linked list that holds the transactions on the product and key is the product Type
     * This method takes the Sale Object and store it according to the occurence of the transaction
     * @param sale
     * @param count
     * @return
     */
    public boolean storeSale(Sale sale, int count) {
        boolean result = true;
        int numberOfOccurence = count;
        try {
            LinkedList<Sale> temp = storageSale.get(sale.getProductType());
            if (temp == null) {
                temp = new LinkedList<Sale>();
            }
            for (int i = 0; i < numberOfOccurence; i++) {
                result = temp.add(sale);
                if (!result)
                    break;
            }
            storageSale.put(sale.getProductType(), temp);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * If there is need for adjustment to all transactions of a Sale of a Product this method is used
     * @param sale
     * @return
     */
    public boolean storeAdjustment(AdjustmentToSale sale) {
        boolean result = true;
        try {
            LinkedList<AdjustmentToSale> temp = storageAdjustment.get(sale.getProductType());
            if (temp == null) {
                temp = new LinkedList<AdjustmentToSale>();
            }
            result = temp.add(sale);
            if (result)
                storageAdjustment.put(sale.getProductType(), temp);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * To traverse the history records of a product
     * The adjustment history is store in a hashtable with a key of product type and a value of linkedlist of adjustment operations done
     * @return
     */
    public int printAdjReportToConsole() {
        int result = 0;
        Collection<LinkedList<AdjustmentToSale>> colVal = storageAdjustment.values();
        Iterator<LinkedList<AdjustmentToSale>> itList = colVal.iterator();
        LinkedList<AdjustmentToSale> tempList = null;
        Iterator<AdjustmentToSale> itSales = null;
        AdjustmentToSale tempSale = null;
        while (itList.hasNext()) {
            tempList = itList.next();
            itSales = tempList.iterator();
            while (itSales.hasNext()) {
                tempSale = itSales.next();
                System.out.println("REPORT: ADJUSTMENT --->Product: " + tempSale.getProductType() + " adjustment value: "
                        + tempSale.getValue() + " adjustment operator: "
                        + tempSale.getOper().toString());
                result++;
            }
        }
        return result;
    }

    /**
     * traverse all the transactions of all products and print them out
     * @return
     */
    public int printSaleReportToConsole() {
        int result = 0;
        Enumeration<String> keyEnum = storageSale.keys();
        int totalNumberOfSalesOfProduct = 0;
        String key = null;
        float totalValue = 0;
        while (keyEnum.hasMoreElements()) {
            key = keyEnum.nextElement();
            totalNumberOfSalesOfProduct = getTotalNumberOfSales(key);
            totalValue = getValueOfSales(key);
            System.out.println("REPORT: SALE ---> Product: " + key + " total number of sales: "
                    + totalNumberOfSalesOfProduct + " total value: " + totalValue);
            result++;
        }
        return result;
    }

    /**
     * to calculate the total value of sales transactions of a product type
     * @param product
     * @return
     */
    public float getValueOfSales(String product) {
        float totalValue = 0;
        LinkedList<Sale> temp = storageSale.get(product);
        Iterator<Sale> it = temp.iterator();
        while (it.hasNext()) {
            totalValue = totalValue + it.next().getValue();
        }
        return totalValue;
    }

    /**
     * to get the count of sale transaction of a product type
     * @param product
     * @return
     */
    public int getTotalNumberOfSales(String product) {
        int totalValue = 0;
        LinkedList<Sale> temp = storageSale.get(product);
        totalValue = temp.size();
        return totalValue;
    }

    public LinkedList<Sale> retrieveSales(Sale sale) {
        return storageSale.get(sale.getProductType());
    }

    public LinkedList<AdjustmentToSale> retrieveAdjReports(Sale sale) {
        return storageAdjustment.get(sale.getProductType());
    }

    /**
     * traverse all the transactions of a product type and modify them according to the operation request(sum, multiply and subtract)
     * @param sale
     * @return
     */
    public boolean adjustmentToSale(AdjustmentToSale sale) {
        boolean result = true;
        LinkedList<Sale> temp = storageSale.get(sale.getProductType());
        LinkedList<Sale> adjusted = new LinkedList<Sale>();
        if (temp == null) {
            result = false;
        } else {
            Sale tempSale = null;
            while (!temp.isEmpty()) {
                tempSale = temp.remove(0);
                adjusted.add(adjustSale(sale, tempSale));
            }
            storageSale.put(sale.getProductType(), adjusted);
            storeAdjustment(sale);
        }
        return result;
    }

    /**
     * helper method for adjustmentToSale
     * makes the modification for the single transaction of the product type
     * @param adjSale
     * @param sale
     * @return
     */
    private Sale adjustSale(AdjustmentToSale adjSale, Sale sale) {
        float newVal = 0f;
        switch (adjSale.getOper()) {
        case SUM:
            newVal = sale.getValue() + adjSale.getValue();
            break;
        case MULTIPLY:
            newVal = sale.getValue() * adjSale.getValue();
            break;
        case SUBTRACT:
            newVal = sale.getValue() - adjSale.getValue();
            if (newVal < 0)
                newVal = 0.0f;
            break;
        default:
            newVal = sale.getValue();
            break;
        }
        return new Sale(sale.getProductType(), newVal);
    }
}
