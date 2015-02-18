package messaging;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Consumer extends Messenger{

    public Consumer(Channel channel, String queueName) {
        setChannel(channel);
        setQueueName(queueName);
    }

    public String getMessage(){
        String rtnStr = "";
        try {
            boolean autoAck = false;
            getChannel().queueDeclare(getQueueName(), false, false, false, null);
            GetResponse response = getChannel().basicGet(getQueueName(), autoAck);
            if (response == null) {
                // nothing in queue? log this?
            } else {
                byte[] body = response.getBody();
                long deliveryTag = response.getEnvelope().getDeliveryTag();
                rtnStr = new String(body);
                getChannel().basicAck(deliveryTag, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rtnStr;
    }
    public void close(){
        try {
            getChannel().close();
            //connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}