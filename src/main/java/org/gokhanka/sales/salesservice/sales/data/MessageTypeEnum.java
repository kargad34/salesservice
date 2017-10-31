package org.gokhanka.sales.salesservice.sales.data;

public enum MessageTypeEnum {

    SALE(0), MULTI_OCCUR_SALE(1), ADJUSTMENT_OF_SALE(2),UNDEFINED(3);

    private int id;

    private MessageTypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public static MessageTypeEnum getEnum(int type) {
        switch (type) {
        case 0:
            return SALE;
        case 1:
            return MULTI_OCCUR_SALE;
        case 2:
            return ADJUSTMENT_OF_SALE;
        default:
            return UNDEFINED;
        }
    }

    @Override
    public String toString() {
        switch (this) {
        case SALE:
            return "SALE: " + id;
        case MULTI_OCCUR_SALE:
            return "MULTI_OCCUR_SALES: " + id;
        case ADJUSTMENT_OF_SALE:
            return "ADJUSTMENT_OF_SALE: " + id;
        default:
            return null;
        }
    }

}
