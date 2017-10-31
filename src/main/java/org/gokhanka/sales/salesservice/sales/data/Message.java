package org.gokhanka.sales.salesservice.sales.data;


public class Message {
private MessageTypeEnum type = null;
private Sale sale = null;
    public Message(MessageTypeEnum type, Sale sale) {
    super();
    this.type = type;
    this.sale = sale;
}
    
    public MessageTypeEnum getType() {
        return type;
    }
    
    public Sale getSale() {
        return sale;
    }


}
