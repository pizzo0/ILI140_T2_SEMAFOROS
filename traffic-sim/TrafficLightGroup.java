import java.util.ArrayList;
import java.util.List;

public class TrafficLightGroup
{
    private String id;

    private List<TrafficLight> lights = new ArrayList<>();
    private List<String> sensors = new ArrayList<>();

    private int cycleTraffic = 0;

    public TrafficLightGroup(String id)
    {
        this.id = id;
    }

    public void addLight(TrafficLight light)
    {
        lights.add(light);
    }

    public void addSensor(String sensor)
    {
        sensors.add(sensor);
    }

    public boolean ownsSensor(String sensor)
    {
        return sensors.contains(sensor);
    }

    public void addTraffic(int amount)
    {
        cycleTraffic += amount;
    }

    public int getTrafficScore()
    {
        return cycleTraffic;
    }

    public void resetTraffic()
    {
        cycleTraffic = 0;
    }

    public void setGreen()
    {
        for (TrafficLight light : lights) {
            light.setState(TrafficLight.State.GREEN);
        }

        System.out.println("[GROUP " + id + "] GREEN");
    }

    public void setRed()
    {
        for (TrafficLight light : lights) {
            light.setState(TrafficLight.State.RED);
        }

        System.out.println("[GROUP " + id + "] RED");
    }

    public String getId()
    {
        return id;
    }
}