package org.gokhanka.sales.salesservice.sales.data;

public class Sale {

    private String productType = null;
    private  float value = 0;

    public Sale(String productType, float value) {
        this.productType = productType;
        this.value = value;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public float getValue() {
        return value;
    }
}
