import greenfoot.*;

public class Car extends Actor
{
    private RoadNode currentNode;
    private RoadNode targetNode;

    private double speed = 2.0;

    public Car(RoadNode startNode)
    {
        currentNode = startNode;
        targetNode = startNode.getNextNode();

        drawCar();

        setLocation(
            startNode.getX(),
            startNode.getY()
        );
    }

    public void act()
    {
        if (targetNode == null) {
            getWorld().removeObject(this);
            return;
        }

        move();
    }

    private void move()
    {
        if (
            targetNode.getType() == RoadNode.NodeType.STOP_LINE &&
            !targetNode.canPass()
        ) {
            double distance = distanceTo(
                targetNode.getX(),
                targetNode.getY()
            );

            if (distance < 20) {
                return;
            }
        }

        double dx = targetNode.getX() - getX();
        double dy = targetNode.getY() - getY();

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < speed) {

            setLocation(
                targetNode.getX(),
                targetNode.getY()
            );

            currentNode = targetNode;

            if (currentNode.getType() == RoadNode.NodeType.EXIT) {
                getWorld().removeObject(this);
                return;
            }

            targetNode = currentNode.getNextNode();

            return;
        }

        double moveX = (dx / distance) * speed;
        double moveY = (dy / distance) * speed;

        setLocation(
            (int)(getX() + moveX),
            (int)(getY() + moveY)
        );

        setRotation(
            (int)Math.toDegrees(
                Math.atan2(dy, dx)
            )
        );
    }

    private double distanceTo(int x, int y)
    {
        double dx = x - getX();
        double dy = y - getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    private void drawCar()
    {
        GreenfootImage image = new GreenfootImage(22, 12);

        image.setColor(Color.BLUE);
        image.fillRect(0, 0, 22, 12);

        image.setColor(Color.BLACK);
        image.drawRect(0, 0, 21, 11);

        setImage(image);
    }
}
