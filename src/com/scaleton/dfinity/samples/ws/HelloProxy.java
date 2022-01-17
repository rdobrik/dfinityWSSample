package com.scaleton.dfinity.samples.ws;

import java.util.concurrent.CompletableFuture;

import org.ic4j.agent.annotations.Waiter;
import org.ic4j.candid.annotations.QUERY;
import org.ic4j.candid.annotations.UPDATE;

public interface HelloProxy {
	
	@QUERY
	public String peek();
	
	@UPDATE
	@Waiter(timeout = 30)
	public CompletableFuture<String> greet(String name);
}
