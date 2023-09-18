package cn.itcast.mq.spring;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testSimpleQueue() {
        String queueName = "simple.queue";

        String message = "hello, spring amqp!";

        rabbitTemplate.convertAndSend(queueName, message);
    }



    @Test
    public void testWorkQueue() throws InterruptedException {
        String queueName = "simple.queue";

        String message =  "hello, message_";

        for (int i = 0; i < 50; i ++) {
            // 发送消息
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(20);
        }
    }


    @Test
    public void testFanoutExchange() {
        String exchangeName = "chen.fanout";

        String msg = "hello, everyone!";

        rabbitTemplate.convertAndSend(exchangeName, "", msg);

    }


    @Test
    public void testSendDirectExchange() {
        // 交换机名称
        String exchangeName = "chen.direct";
        // 消息
        String message = "红色警报！日本乱排核废水，导致海洋生物变异，惊现哥斯拉！";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "blue", message);
    }


    /**
     * topicExchange
     */
    @Test
    public void testSendTopicExchange() {
        // 交换机名称
        String exchangeName = "chen.topic";
        // 消息
        String message = "喜报！孙悟空大战哥斯拉，胜!";
        // 发送消息
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);
    }

}
