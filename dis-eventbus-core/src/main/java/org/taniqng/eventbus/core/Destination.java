package org.taniqng.eventbus.core;

public final class Destination {
    
    private String exchange;
    
    private String routingKey;

    private Destination(){}
    
    public static Destination create(String exchangeName, String routingKey){
        Destination dest = new Destination();
        dest.exchange = exchangeName;
        dest.routingKey = routingKey;
        return dest;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }
    
}
