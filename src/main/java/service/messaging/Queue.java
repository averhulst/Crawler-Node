package service.messaging;

import java.util.List;
import java.util.Map;

public interface Queue {
    public String getMessage();

    public void publishMessage(String message);

    public void publishMessages(List<String> messages);

    public int getQueueSize();
    public void addHeaders(Map headers);
}
