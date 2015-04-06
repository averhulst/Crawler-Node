package service.messaging;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import java.io.IOException;
import java.util.List;

public class QueueImpl implements Queue{
    private Channel channel;
    private String queueName;

    public QueueImpl(Channel channel, String queueName) {
        this.channel = channel;
        this.queueName = queueName;
    }

    public int getQueueSize(){
        int queueSize = -1;
        try {
            AMQP.Queue.DeclareOk queue = channel.queueDeclare(queueName, false, false, false, null);
            queueSize = queue.getMessageCount();
        } catch (IOException e) {
            //TODO log me
        }
        return queueSize;
    }

    public String getMessage(){
        String rtnStr = "";
        try {
            boolean autoAck = false;
            channel.queueDeclare(queueName, false, false, false, null);
            GetResponse response = channel.basicGet(queueName, autoAck);

            if (response != null) {
                long deliveryTag = response.getEnvelope().getDeliveryTag();
                channel.basicAck(deliveryTag, false);
                rtnStr = new String(response.getBody());
            }else{
                //TODO nothing in queue? log this?
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rtnStr;
    }

    public void publishMessage(String message){;
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void publishMessages(List<String> messages){
        for(String s : messages){
            publishMessage(s);
        }
    }
}
