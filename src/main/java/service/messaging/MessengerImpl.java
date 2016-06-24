package service.messaging;

import application.crawler.Environment;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.util.*;

public class MessengerImpl implements Messenger{
    private ConnectionFactory connectionFactory;
    private Map<String, Queue> queues;
    private JedisPool pool = new JedisPool(Environment.REDIS_SERVER_ADDRESS, Environment.REDIS_SERVER_PORT);
    Jedis jedis = pool.getResource();

    public MessengerImpl(){
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Environment.MESSAGING_SERVICE_ADDRESS);
        connectionFactory.setUsername(Environment.MESSAGING_SERVICE_USER_NAME);
        connectionFactory.setPassword(Environment.MESSAGING_SERVICE_PASS);
        queues = new HashMap<String, Queue>();

        try {
            Connection connection = connectionFactory.newConnection();

            queues.put("freshDomains",
                    new QueueImpl(connection.createChannel(), "freshDomains")
            );
            queues.put("discoveredDomains",
                    new QueueImpl(connection.createChannel(), "discoveredDomains")
            );
            queues.put("crawlResults",
                    new QueueImpl(connection.createChannel(), "crawlResults")
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Queue getQueue(String queueName){
        return queues.get(queueName);
    }

    public void publishStatus(String status) {
        JSONObject json = new JSONObject(status);
        try {
            String key = json.get("crawlerId").toString();
            jedis.sadd(key, json.toString());
            jedis.expire(key, 180);
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (jedis != null){
                pool.returnResource(jedis);
            }

        }
    }

}


