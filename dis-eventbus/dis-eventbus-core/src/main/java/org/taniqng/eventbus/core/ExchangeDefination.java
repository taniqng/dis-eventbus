package org.taniqng.eventbus.core;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExchangeDefination {

	public static final String EXCHANGE_TANIQNG_FANOUT_EVENTBUS = "EXCHANGE_TANIQNG_FANOUT_EVENTBUS";
	
	@Bean(name=EXCHANGE_TANIQNG_FANOUT_EVENTBUS)
    public FanoutExchange defineEventBusExchange(){
        return new FanoutExchange(EXCHANGE_TANIQNG_FANOUT_EVENTBUS);
    }
}
