import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoadNode
{
    public enum NodeType {
        NORMAL,
        STOP_LINE,
        EXIT
    }

    private int x;
    private int y;

    private NodeType type;

    private TrafficLight trafficLight;

    private List<PathOption> nextNodes = new ArrayList<>();

    private static final Random random = new Random();

    public RoadNode(int x, int y)
    {
        this(x, y, NodeType.NORMAL);
    }

    public RoadNode(int x, int y, NodeType type)
    {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public void connectTo(RoadNode node, int weight)
    {
        nextNodes.add(new PathOption(node, weight));
    }

    public RoadNode getNextNode()
    {
        if (nextNodes.isEmpty()) {
            return null;
        }

        int totalWeight = 0;

        for (PathOption option : nextNodes) {
            totalWeight += option.weight;
        }

        int value = random.nextInt(totalWeight);

        int current = 0;

        for (PathOption option : nextNodes) {
            current += option.weight;

            if (value < current) {
                return option.node;
            }
        }

        return nextNodes.get(0).node;
    }

    public boolean canPass()
    {
        if (trafficLight == null) {
            return true;
        }

        return trafficLight.getState() == TrafficLight.State.GREEN;
    }

    public void setTrafficLight(TrafficLight trafficLight)
    {
        this.trafficLight = trafficLight;
    }

    public TrafficLight getTrafficLight()
    {
        return trafficLight;
    }

    public NodeType getType()
    {
        return type;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    private static class PathOption
    {
        RoadNode node;
        int weight;

        PathOption(RoadNode node, int weight)
        {
            this.node = node;
            this.weight = weight;
        }
    }
}
