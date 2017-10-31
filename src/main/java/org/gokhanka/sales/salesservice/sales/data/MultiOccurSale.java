package org.gokhanka.sales.salesservice.sales.data;


public class MultiOccurSale extends Sale {
    private int numberOfOccurence = 0;
    
    public MultiOccurSale(String productType, float value, int numberOfOccurence) {
        super(productType, value);
        this.numberOfOccurence = numberOfOccurence;
    }
    
    public int getNumberOfOccurence() {
        return numberOfOccurence;
    }

}
