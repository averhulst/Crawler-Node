package service.messaging;

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

            consumers.put("freshDomains",
                    new Consumer(connection.createChannel(), "freshDomains")
            );

            publishers.put("discoveredDomains",
                    new Publisher(connection.createChannel(), "discoveredDomains")
            );
            publishers.put("freshDomains",
                    new Publisher(connection.createChannel(), "freshDomains")
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void publishDiscoveredDomains(List discoveredDomains){
        String outgoingDomains = String.join(";", discoveredDomains);
        //publishers.get("discoveredDomains").publishMessage(outgoingDomains);
        // Hacking this until centralized hub can parse and publish new domains
        publishers.get("freshDomains").publishMessage(outgoingDomains);
    }
    public void publishFreshDomains(List discoveredDomains){
        String outgoingDomains = String.join(";", discoveredDomains);
        publishers.get("freshDomains").publishMessage(outgoingDomains);
    }

    public List<String> fetchFreshDomains(){
        String incomingDomains = consumers.get("freshDomains").getMessage();
        System.out.println("test pulled " + incomingDomains);
        return Arrays.asList(incomingDomains.split(";"));
    }
//    public void publishCrawledDomain(Domain){
//
//    }
}

