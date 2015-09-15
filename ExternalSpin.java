package com.poc.demo.smx.psb;

import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.language.XPath;

public class ExternalSpin {
	private static final Logger LOG = Logger.getLogger(ExternalSpin.class.getPackage().getName());

	private String domainName;
	private int waitTime;

	@Produce(uri = "seda:domainAggregator")
	private ProducerTemplate quoteAggregator;

	public ExternalSpin(String domainName, int waitTime) {
		this.domainName = domainName;
		this.waitTime = waitTime;
	}

	public void getQuote(Exchange exchange) throws Exception {
			exchange.getIn().setBody(domainName + exchange.getIn().getHeader("domainId"));
			Thread.sleep(waitTime);
			
	}
}
