package service.messaging;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
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
            publishers.put("crawlResults",
                    new Publisher(connection.createChannel(), "crawlResults")
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void publishDiscoveredDomains(List<URL> discoveredDomains){
        StringBuilder outgoingDomains = new StringBuilder();
        for(URL url : discoveredDomains){
            outgoingDomains.append(url + ";");
        }
        //publishers.get("discoveredDomains").publishMessage(outgoingDomains);
        // Hacking this until centralized hub can parse and publish new domains
        publishers.get("discoveredDomains").publishMessage(outgoingDomains.toString());
    }

    public List<String> fetchFreshDomains(){
        String incomingDomains = consumers.get("freshDomains").getMessage();
        if(incomingDomains.length() > 0){
            System.out.println("pulled down domain:" + incomingDomains);
        }
        return Arrays.asList(incomingDomains.split(";"));
    }

    public void publishCrawledDomainResult(JSONObject crawlResult){
        publishers.get("crawlResults").publishMessage(crawlResult.toString());
    }
}

