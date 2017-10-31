package org.gokhanka.sales.salesservice.sales.data;


public enum AdjustmentOperator {

    SUM(0), MULTIPLY(1), SUBTRACT(2), UNDEFINED(3);

    private int id;

    private AdjustmentOperator(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public static AdjustmentOperator getEnum(int type) {
        switch (type) {
        case 0:
            return SUM;
        case 1:
            return MULTIPLY;
        case 2:
            return SUBTRACT;
        default:
            return UNDEFINED;
        }
    }
    @Override
    public String toString() {
        switch (this) {
        case SUM:
            return "SUM: " + id;
        case MULTIPLY:
            return "MULTIPLY: " + id;
        case SUBTRACT:
            return "SUBTRACT: " + id;
        default:
            return null;
        }
    }
}
