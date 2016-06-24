package service.messaging;

import org.json.JSONObject;

public interface Messenger {
    public Queue getQueue(String queueName);

    public void publishStatus(String status);
}
