// TrafficLightGroup.java

import java.util.ArrayList;
import java.util.List;

public class TrafficLightGroup
{
    private String id;

    private List<TrafficLight> lights =
        new ArrayList<TrafficLight>();

    private List<String> sensors =
        new ArrayList<String>();

    private int trafficScore = 0;

    private long greenSince = 0;

    public TrafficLightGroup(String id)
    {
        this.id = id;
    }

    public void addLight(
        TrafficLight light
    )
    {
        lights.add(light);
    }

    public void addSensor(
        String sensor
    )
    {
        sensors.add(sensor);
    }

    public boolean ownsSensor(
        String sensor
    )
    {
        return sensors.contains(sensor);
    }

    public void addTraffic(
        int amount
    )
    {
        trafficScore += amount;
    }

    public void setGreen()
    {
        for (
            TrafficLight light
            : lights
        ) {
            light.setState(
                TrafficLight.State.GREEN
            );
        }

        greenSince =
            System.currentTimeMillis();

        System.out.println(
            "[GROUP "
            + id
            + "] GREEN"
        );
    }

    public void setRed()
    {
        for (
            TrafficLight light
            : lights
        ) {
            light.setState(
                TrafficLight.State.RED
            );
        }

        System.out.println(
            "[GROUP "
            + id
            + "] RED"
        );
    }

    public int getTrafficScore()
    {
        return trafficScore;
    }

    public void clearTraffic()
    {
        trafficScore = 0;
    }

    public long getGreenSince()
    {
        return greenSince;
    }

    public String getId()
    {
        return id;
    }
}
