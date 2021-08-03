package com.scaleton.dfinity.samples.ws;

import java.util.concurrent.CompletableFuture;

import com.scaleton.dfinity.agent.annotations.Waiter;
import com.scaleton.dfinity.candid.annotations.QUERY;
import com.scaleton.dfinity.candid.annotations.UPDATE;

public interface HelloProxy {
	
	@QUERY
	public String peek();
	
	@UPDATE
	@Waiter(timeout = 30)
	public CompletableFuture<String> greet(String name);
}
