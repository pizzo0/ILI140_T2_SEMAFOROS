import java.util.ArrayList;
import java.util.List;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class TrafficController
{
    private List<TrafficLightGroup> groups = new ArrayList<>();
    private TrafficLightGroup activeGroup;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    private long cycleStart = 0;
    private long currentCycleDuration = BASE_GREEN_TIME;
    private int extensionsUsed = 0;

    private static final long BASE_GREEN_TIME = 5000;
    private static final long EXTEND_TIME = 2500;
    private static final int MAX_EXTENSIONS = 4;

    public TrafficController()
    {
        initializeMQ();
    }

    public void addGroup(TrafficLightGroup group)
    {
        groups.add(group);

        if (activeGroup == null) {
            activeGroup = group;
            group.setGreen();
            cycleStart = System.currentTimeMillis();
        }
    }

    public void act()
    {
        processMessages();
        evaluateCycle();
    }

    private void processMessages()
    {
        try {
            Message message;

            while ((message = consumer.receiveNoWait()) != null) {

                if (!(message instanceof TextMessage)) continue;

                String text = ((TextMessage) message).getText();

                String sensor = extract(text, "sensor");
                int cars = Integer.parseInt(extract(text, "cars"));

                for (TrafficLightGroup group : groups) {
                    if (group.ownsSensor(sensor)) {
                        group.addTraffic(cars);

                        System.out.println(
                            "[" + group.getId() + "] +" + cars + " autos"
                        );
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void evaluateCycle()
    {
        if (activeGroup == null) return;

        long now = System.currentTimeMillis();
        long elapsed = now - cycleStart;

        long remaining = currentCycleDuration - elapsed;

        if (remaining > 0) return;

        if (evaluateExtension()) return;

        TrafficLightGroup next = getNextGroup();

        if (next != null) {
            switchGroup(next);
        }
    }

    private boolean evaluateExtension()
    {
        if (extensionsUsed >= MAX_EXTENSIONS) return false;

        int activeTraffic = activeGroup.getTrafficScore();

        int waitingTraffic = 0;

        for (TrafficLightGroup group : groups) {
            if (group != activeGroup) {
                waitingTraffic += group.getTrafficScore();
            }
        }

        System.out.println(
            "[DEBUG] active=" + activeTraffic +
            " waiting=" + waitingTraffic +
            " ext=" + extensionsUsed
        );

        if (activeTraffic > 0 &&
            activeTraffic > waitingTraffic * 2)
        {
            currentCycleDuration += EXTEND_TIME;
            extensionsUsed++;

            System.out.println(
                "[EXTEND_GREEN] " + activeGroup.getId()
            );

            return true;
        }

        return false;
    }

    private TrafficLightGroup getNextGroup()
    {
        TrafficLightGroup best = null;
        int bestScore = -1;

        for (TrafficLightGroup group : groups) {
            if (group == activeGroup) continue;

            int score = group.getTrafficScore();

            if (score > bestScore) {
                best = group;
                bestScore = score;
            }
        }

        if (best != null) return best;

        for (TrafficLightGroup group : groups) {
            if (group != activeGroup) return group;
        }

        return null;
    }

    private void switchGroup(TrafficLightGroup next)
    {
        System.out.println("====================");

        System.out.println("[CHANGE_RED] " + activeGroup.getId());
        activeGroup.setRed();

        System.out.println("[CHANGE_GREEN] " + next.getId());
        next.setGreen();

        activeGroup.resetTraffic();
        next.resetTraffic();

        activeGroup = next;

        cycleStart = System.currentTimeMillis();
        currentCycleDuration = BASE_GREEN_TIME;
        extensionsUsed = 0;
    }

    private void initializeMQ()
    {
        try {
            ConnectionFactory factory =
                new ActiveMQConnectionFactory("tcp://localhost:61616");

            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination destination = session.createQueue("traffic.sensor");

            consumer = session.createConsumer(destination);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extract(String json, String key)
    {
        String pattern = "\"" + key + "\":";

        int start = json.indexOf(pattern) + pattern.length();

        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }

        int end = json.indexOf(",", start);

        if (end == -1) {
            end = json.indexOf("}", start);
        }

        return json.substring(start, end);
    }
}