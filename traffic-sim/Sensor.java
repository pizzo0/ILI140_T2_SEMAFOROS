// Sensor.java

import greenfoot.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Sensor extends Actor
{
    private String id;

    private int width = 40;
    private int height = 40;

    /*
        AUTOS CONTADOS EN EL INTERVALO
    */

    private Set<Car> countedCars =
        new HashSet<Car>();

    /*
        MQ
    */

    private Connection connection;
    private Session session;

    private MessageProducer producer;

    /*
        INTERVALO ENVIO
    */

    private long lastSend = 0;

    private static final long SEND_INTERVAL = 2500;

    public Sensor(String id)
    {
        this.id = id;

        initializeMQ();

        drawSensor();
    }

    public void act()
    {
        detectCars();

        long now =
            System.currentTimeMillis();

        if (
            now - lastSend >=
            SEND_INTERVAL
        ) {

            sendTrafficData();

            countedCars.clear();

            lastSend = now;
        }
    }

    private void detectCars()
    {
        List<Car> cars =
            getWorld().getObjects(Car.class);

        int left =
            getX() - width / 2;

        int right =
            getX() + width / 2;

        int top =
            getY() - height / 2;

        int bottom =
            getY() + height / 2;

        for (Car car : cars) {

            int x = car.getX();
            int y = car.getY();

            boolean inside =
                x >= left &&
                x <= right &&
                y >= top &&
                y <= bottom;

            if (inside) {
                countedCars.add(car);
            }
        }
    }

    private void sendTrafficData()
    {
        try {

            int cars =
                countedCars.size();

            String json =
                "{"
                    + "\"sensor\":\"" + id + "\","
                    + "\"cars\":" + cars
                + "}";

            TextMessage message =
                session.createTextMessage(
                    json
                );

            producer.send(message);

            System.out.println(
                "[SENSOR "
                + id
                + "] enviados "
                + cars
                + " autos"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeMQ()
    {
        try {

            ConnectionFactory factory =
                new ActiveMQConnectionFactory(
                    "tcp://localhost:61616"
                );

            connection =
                factory.createConnection();

            connection.start();

            session =
                connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE
                );

            Destination destination =
                session.createQueue(
                    "traffic.sensor"
                );

            producer =
                session.createProducer(
                    destination
                );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawSensor()
    {
        GreenfootImage img =
            new GreenfootImage(
                width,
                height
            );

        img.setColor(
            new Color(
                255,
                255,
                0,
                80
            )
        );

        img.fillRect(
            0,
            0,
            width,
            height
        );

        img.setColor(Color.YELLOW);

        img.drawRect(
            0,
            0,
            width - 1,
            height - 1
        );

        setImage(img);
    }
}