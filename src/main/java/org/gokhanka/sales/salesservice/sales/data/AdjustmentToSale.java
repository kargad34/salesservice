package org.gokhanka.sales.salesservice.sales.data;


public class AdjustmentToSale extends Sale {
private AdjustmentOperator oper = null;

public AdjustmentToSale(String productType, float value, AdjustmentOperator oper) {
        super(productType, value);
        this.oper = oper;
    }

    public AdjustmentOperator getOper() {
        return oper;
    }

}
