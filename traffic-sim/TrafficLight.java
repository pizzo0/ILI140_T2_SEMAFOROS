import greenfoot.*;

public class TrafficLight extends Actor
{
    public enum State {
        RED,
        GREEN
    }

    private State state =
        State.RED;

    public TrafficLight()
    {
        updateImage();
    }

    public void setState(State state)
    {
        this.state = state;

        updateImage();
    }

    public State getState()
    {
        return state;
    }

    private void updateImage()
    {
        GreenfootImage img =
            new GreenfootImage(20, 50);

        img.setColor(Color.BLACK);

        img.fillRect(0, 0, 20, 50);

        img.setColor(
            state == State.RED
                ? Color.RED
                : Color.DARK_GRAY
        );

        img.fillOval(2, 5, 16, 16);

        img.setColor(
            state == State.GREEN
                ? Color.GREEN
                : Color.DARK_GRAY
        );

        img.fillOval(2, 28, 16, 16);

        setImage(img);
    }
}
