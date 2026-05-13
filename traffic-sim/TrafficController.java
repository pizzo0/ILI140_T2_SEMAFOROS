// TrafficController.java

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class TrafficController
{
    private List<TrafficLightGroup> groups =
        new ArrayList<TrafficLightGroup>();

    private TrafficLightGroup activeGroup;

    /*
        MQ
    */

    private Connection connection;
    private Session session;

    private MessageConsumer consumer;

    /*
        CICLO ACTUAL
    */

    private long cycleStart = 0;

    /*
        TIEMPO ACTUAL DEL CICLO
        (SE PUEDE EXTENDER)
    */

    private long currentCycleDuration =
        BASE_GREEN_TIME;

    /*
        CUANTAS VECES SE HA EXTENDIDO
        ESTE CICLO
    */

    private int extensionsUsed = 0;

    /*
        CONFIG
    */

    /*
        CHANGE_GREEN:
        CAMBIAR UN GRUPO A VERDE
    */

    private static final long BASE_GREEN_TIME =
        5000;

    /*
        EXTEND_GREEN:
        EXTENDER VERDE
    */

    private static final long EXTEND_TIME =
        2500;

    /*
        MAXIMO DE EXTENSIONES
        POR CICLO
    */

    private static final int MAX_EXTENSIONS =
        4;

    public TrafficController()
    {
        initializeMQ();
    }

    public void addGroup(
        TrafficLightGroup group
    )
    {
        groups.add(group);

        if (activeGroup == null) {

            /*
                CHANGE_GREEN
            */

            activeGroup = group;

            group.setGreen();

            cycleStart =
                System.currentTimeMillis();
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

            while (
                (message =
                    consumer.receiveNoWait())
                    != null
            ) {

                if (
                    !(message instanceof TextMessage)
                ) {
                    continue;
                }

                String text =
                    ((TextMessage) message)
                        .getText();

                String sensor =
                    extract(
                        text,
                        "sensor"
                    );

                int cars =
                    Integer.parseInt(
                        extract(
                            text,
                            "cars"
                        )
                    );

                for (
                    TrafficLightGroup group
                    : groups
                ) {

                    if (
                        group.ownsSensor(
                            sensor
                        )
                    ) {

                        group.addTraffic(
                            cars
                        );

                        System.out.println(
                            "["
                            + group.getId()
                            + "] +"
                            + cars
                            + " autos"
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
        if (activeGroup == null) {
            return;
        }

        long now =
            System.currentTimeMillis();

        long elapsed =
            now - cycleStart;

        long remaining =
            currentCycleDuration
            - elapsed;

        /*
            TODAVIA QUEDA TIEMPO
        */

        if (remaining > 0) {
            return;
        }

        /*
            INTENTAR EXTENDER
        */

        boolean extended =
            evaluateExtension();

        /*
            SI SE EXTENDIO,
            CONTINUAR MISMO CICLO
        */

        if (extended) {
            return;
        }

        /*
            CAMBIO DE CICLO
        */

        TrafficLightGroup next =
            getNextGroup();

        if (next != null) {

            switchGroup(next);
        }
    }

    private boolean evaluateExtension()
    {
        /*
            YA LLEGO AL LIMITE
        */

        if (
            extensionsUsed >=
            MAX_EXTENSIONS
        ) {
            return false;
        }

        int activeTraffic =
            activeGroup.getTrafficScore();

        int waitingTraffic = 0;

        for (
            TrafficLightGroup group
            : groups
        ) {

            if (group == activeGroup) {
                continue;
            }

            waitingTraffic +=
                group.getTrafficScore();
        }

        /*
            EXTEND_GREEN

            SI:
            activeTraffic > waitingTraffic * 4
        */

        if (
            activeTraffic >
            waitingTraffic * 4
        ) {

            currentCycleDuration +=
                EXTEND_TIME;

            extensionsUsed++;

            System.out.println(
                "[EXTEND_GREEN] "
                + activeGroup.getId()
                + " +5s"
            );

            return true;
        }

        return false;
    }

    private TrafficLightGroup getNextGroup()
    {
        TrafficLightGroup best =
            null;

        int bestScore = -1;

        for (
            TrafficLightGroup group
            : groups
        ) {

            /*
                IGNORAR EL ACTUAL
            */

            if (group == activeGroup) {
                continue;
            }

            if (
                group.getTrafficScore()
                > bestScore
            ) {

                best = group;

                bestScore =
                    group.getTrafficScore();
            }
        }

        /*
            SI TODOS TIENEN 0,
            IGUAL CAMBIAR
        */

        if (best == null) {

            for (
                TrafficLightGroup group
                : groups
            ) {

                if (group != activeGroup) {
                    return group;
                }
            }
        }

        return best;
    }

    private void switchGroup(
        TrafficLightGroup next
    )
    {
        if (next == activeGroup) {
            return;
        }

        System.out.println(
            "===================="
        );

        System.out.println(
            "[CHANGE_RED] "
            + activeGroup.getId()
        );

        /*
            CHANGE_RED
        */

        activeGroup.setRed();

        /*
            EL GRUPO QUE SALE
            DE VERDE TERMINA
            SU CICLO
        */

        activeGroup.clearTraffic();

        System.out.println(
            "[CHANGE_GREEN] "
            + next.getId()
        );

        /*
            EL NUEVO GRUPO
            EMPIEZA UN NUEVO
            CICLO DESDE 0
        */

        next.clearTraffic();

        /*
            CHANGE_GREEN
        */

        next.setGreen();

        activeGroup = next;

        /*
            NUEVO CICLO
        */

        cycleStart =
            System.currentTimeMillis();

        currentCycleDuration =
            BASE_GREEN_TIME;

        extensionsUsed = 0;
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

            consumer =
                session.createConsumer(
                    destination
                );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extract(
        String json,
        String key
    )
    {
        String pattern =
            "\"" + key + "\":";

        int start =
            json.indexOf(pattern)
            + pattern.length();

        if (
            json.charAt(start)
            == '"'
        ) {

            start++;

            int end =
                json.indexOf(
                    "\"",
                    start
                );

            return json.substring(
                start,
                end
            );
        }

        int end =
            json.indexOf(
                ",",
                start
            );

        if (end == -1) {

            end =
                json.indexOf(
                    "}",
                    start
                );
        }

        return json.substring(
            start,
            end
        );
    }
}
