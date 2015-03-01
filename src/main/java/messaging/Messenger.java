package messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public abstract class Messenger {
    private Channel channel;
    private QueueingConsumer consumer;
    private String queueName;

    protected Channel getChannel() {
        return channel;
    }

    protected void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected QueueingConsumer getConsumer() {
        return consumer;
    }

    protected void setConsumer(QueueingConsumer consumer) {
        this.consumer = consumer;
    }

    protected String getQueueName() {
        return queueName;
    }

    protected void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
