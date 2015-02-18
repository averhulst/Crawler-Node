package messaging;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.*;

public class MessagingService {
    private ConnectionFactory connectionFactory;
    private Map<String, Consumer> consumers;
    private Map<String, Publisher> publishers;

    public MessagingService(){
        this.consumers = new HashMap<>();
        this.publishers = new HashMap<>();

        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            Connection connection = connectionFactory.newConnection();

            consumers.put("freshDomainConsumer",
                    new Consumer(connection.createChannel(), "freshDomains")
            );

            publishers.put("discoveredDomainPublisher",
                    new Publisher(connection.createChannel(), "discoveredDomains")
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void publishDiscoveredDomains(List discoveredDomains){
        String outgoingDomains = String.join(";", discoveredDomains);
        publishers.get("discoveredDomainPublisher").publishMessage(outgoingDomains);
    }

    public List fetchFreshDomains(){
        String incomingDomains = consumers.get("freshDomainConsumer").fetchMessage();
        System.out.println("test pulled " + incomingDomains);
        return Arrays.asList(incomingDomains.split(";"));
    }
}

