package service.messaging;

import java.util.List;

public interface Queue {
    public String getMessage();

    public void publishMessage(String message);

    public void publishMessages(List<String> messages);

    public int getQueueSize();
}
