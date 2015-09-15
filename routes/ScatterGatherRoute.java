package com.poc.demo.smx.psb.routes;


import java.util.logging.Logger;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.poc.demo.smx.psb.AssembleAggregationStrategy;

public class ScatterGatherRoute extends RouteBuilder implements InitializingBean, DisposableBean {

	private static final Logger LOG = Logger.getLogger(ScatterGatherRoute.class.getPackage().getName());

	private static final String PROCESS_ID = "domainId";

	@Override
	public void configure() throws Exception {
		// get the message from the queue and send to the job process endpoint
		from("activemq:queue:domain").id("awesome-scatter").setHeader(PROCESS_ID,constant(1)).multicast().to("direct:external1", "direct:external2", "direct:external3");

		from("direct:external1").id("awesomeness-1").to("bean:spin1").to("direct:domainAggregator");
		from("direct:external2").to("bean:spin2").to("direct:domainAggregator");
		from("direct:external3").threads(5).maxPoolSize(10).to("bean:spin3").to("direct:domainAggregator");
		
		// aggregate the return values - the processId is a unique identifier used
		//  to associate return values with each other.
		from("direct:domainAggregator").id("awesome-gather")
		    .aggregate(header(PROCESS_ID), new AssembleAggregationStrategy())
		    .completionSize(3).to("log:dummy")
		    .setHeader(Exchange.FILE_NAME, constant("response-${date:now:yyyy-MM-dd-HHmmssSSS}"))
	        .wireTap("file://target/outbox") ;


	}

	@Override
	public void destroy() throws Exception {
		LOG.info("[STOPPING] :: SCATTER - GATHER ROUTE");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.info("[STARTING] :: SCATTER - GATHER ROUTE");
	}

}
