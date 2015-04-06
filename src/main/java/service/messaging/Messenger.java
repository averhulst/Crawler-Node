package service.messaging;

public interface Messenger {
    public Queue getQueue(String queueName);
}
