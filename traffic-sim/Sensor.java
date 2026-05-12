import greenfoot.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Sensor extends Actor
{
    private Session session;
    private MessageProducer producer;

    public Sensor() {
        try {
            ConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://p0-ROG:61616");

            Connection connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue("traffic.queue");
            producer = session.createProducer(destination);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void act() {
        if (isTouching(Car.class)) {
            sendTrafficData();
            Greenfoot.delay(20);
        }
    }

    private void sendTrafficData() {
        try {
            TextMessage msg = session.createTextMessage("{\"cars\":1}");
            producer.send(msg);
            System.out.println("Mensaje enviado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
