package com.poc.demo.smx.psb;

import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class AssembleAggregationStrategy implements AggregationStrategy {
	
	private static final Logger LOG = Logger.getLogger(AssembleAggregationStrategy.class.getPackage().getName());
	
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // the first time we only have the new exchange
        if (oldExchange == null) {
            return newExchange;
        }
        // add the newExchange to the old exchange
        oldExchange.getIn().setBody(oldExchange.getIn().getBody(String.class) + " , " + newExchange.getIn().getBody(String.class));
        String tempResponseQueueName = newExchange.getIn().getHeader(Constants.REPLY_TO_PROP, String.class);
    	String correlationId = newExchange.getIn().getHeader(Constants.CORRELATION_PROP, String.class);
    	oldExchange.getIn().setHeader(Constants.REPLY_TO_PROP, tempResponseQueueName);
    	oldExchange.getIn().setHeader(Constants.CORRELATION_PROP, correlationId);
    	
        LOG.info("------------------------------------------------------------------------------------------");
        LOG.info("[EXCHANGE MESSAGE]:[ " + newExchange.getIn().getHeader("domainId") + " ] " + oldExchange.getIn().getBody(String.class));
        LOG.info("------------------------------------------------------------------------------------------");
        return oldExchange;
    }


}
