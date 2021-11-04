package com.scaleton.dfinity.samples.ws;

import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.websocket.server.ServerEndpointConfig;

import com.scaleton.dfinity.agent.ProxyBuilder;
import com.scaleton.dfinity.types.Principal;

import io.helidon.config.Config;
import io.helidon.config.ConfigMappingException;
import io.helidon.config.MissingValueException;
import io.helidon.media.jsonp.JsonpSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.staticcontent.StaticContentSupport;
import io.helidon.webserver.tyrus.TyrusSupport;

import com.scaleton.dfinity.agent.Agent;
import com.scaleton.dfinity.agent.AgentBuilder;
import com.scaleton.dfinity.agent.ReplicaTransport;
import com.scaleton.dfinity.agent.http.ReplicaApacheHttpTransport;

public class Server {
	private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

	public static void main(String[] args) throws MissingValueException, ConfigMappingException, URISyntaxException {
		// By default this will pick up application.yaml from the classpath
		Config config = Config.create();

		ReplicaTransport transport = ReplicaApacheHttpTransport.create(config.get("dfinity").get("url").asString().get());
		Agent agent = new AgentBuilder()
		.transport(transport)
		.build();

		HelloProxy demoProxy = ProxyBuilder.create(agent,Principal.fromString(config.get("dfinity").get("canister").asString().get())).getProxy(HelloProxy.class);	

		Routing routing = Routing.builder()
				.register("/rest", new MessageQueueService(demoProxy))
                .register("/websocket",
                        TyrusSupport.builder().register(
                                ServerEndpointConfig.Builder.create(MessageBoardEndpoint.class, "/board")
                                .build())
                                .build())
                .register("/web", StaticContentSupport.builder("/WEB").build())
				.build();

		WebServer server = WebServer.builder(routing).config(config.get("server"))
				.addMediaSupport(JsonpSupport.create()).
				build();
		
		server.start().thenAccept(ws -> LOGGER.info("Server started at: http://localhost:" + ws.port()));
		
        // Server threads are not demon. NO need to block. Just react.
        server.whenShutdown()
                .thenRun(() -> LOGGER.info("WEB server is DOWN. Good bye!"));

	}

}
