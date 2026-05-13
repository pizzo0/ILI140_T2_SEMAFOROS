import greenfoot.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyWorld extends World
{
    public static TrafficLight northLight;
    public static TrafficLight southLight;

    public static TrafficLight eastLight;
    public static TrafficLight westLight;

    private TrafficController controller;

    private Random random = new Random();

    private int spawnTimer = 0;

    private List<RoadNode> spawnNodes = new ArrayList<>();

    /*
        SPAWN MANUAL
    */

    private int manualSpawnCooldown = 0;

    private static final int MANUAL_SPAWN_DELAY = 50;

    /*
        CONFIG MAPA
    */

    public static final int MIN = 0;
    public static final int MAX = 800;

    // carriles horizontales
    public static final int AXIS_TOP = 420;       // izquierda -> derecha
    public static final int AXIS_BOTTOM = 380;    // derecha -> izquierda

    // carriles verticales
    public static final int AXIS_LEFT = 380;      // arriba -> abajo
    public static final int AXIS_RIGHT = 420;     // abajo -> arriba

    // distancia antes del centro
    public static final int STOP = 300;

    // tamaño interseccion
    public static final int CENTER_MIN = 330;
    public static final int CENTER_MAX = 470;

    /*
        SPAWNS
    */

    private RoadNode westSpawn;
    private RoadNode eastSpawn;

    private RoadNode northSpawn;
    private RoadNode southSpawn;

    public MyWorld()
    {
        super(MAX, MAX, 1);

        drawRoads();

        createTrafficLights();

        createSensors();

        createRoadNetwork();

        controller =
            new TrafficController();

        configureTrafficGroups();
    }

    public void act()
    {
        controller.act();

        if (manualSpawnCooldown > 0) {
            manualSpawnCooldown--;
        }

        /*
            SPAWN MANUAL
        */

        if (manualSpawnCooldown <= 0) {

            if (Greenfoot.isKeyDown("a")) {

                definedSpawnCar(westSpawn);

                manualSpawnCooldown =
                    MANUAL_SPAWN_DELAY;
            }

            else if (Greenfoot.isKeyDown("d")) {

                definedSpawnCar(eastSpawn);

                manualSpawnCooldown =
                    MANUAL_SPAWN_DELAY;
            }

            else if (Greenfoot.isKeyDown("w")) {

                definedSpawnCar(northSpawn);

                manualSpawnCooldown =
                    MANUAL_SPAWN_DELAY;
            }

            else if (Greenfoot.isKeyDown("s")) {

                definedSpawnCar(southSpawn);

                manualSpawnCooldown =
                    MANUAL_SPAWN_DELAY;
            }

            else if (Greenfoot.isKeyDown("r")) {

                spawnCar();

                manualSpawnCooldown =
                    MANUAL_SPAWN_DELAY;
            }
        }

        /*
            SPAWN AUTOMATICO
        */

        /*
        spawnTimer++;

        if (spawnTimer >= 90) {

            spawnCar();

            spawnTimer = 0;
        }
        */
    }

    private void configureTrafficGroups()
    {
        TrafficLightGroup vertical =
            new TrafficLightGroup(
                "VERTICAL"
            );

        vertical.addLight(northLight);
        vertical.addLight(southLight);

        vertical.addSensor("NORTH");
        vertical.addSensor("SOUTH");

        TrafficLightGroup horizontal =
            new TrafficLightGroup(
                "HORIZONTAL"
            );

        horizontal.addLight(eastLight);
        horizontal.addLight(westLight);

        horizontal.addSensor("EAST");
        horizontal.addSensor("WEST");

        controller.addGroup(vertical);
        controller.addGroup(horizontal);
    }

    private void createTrafficLights()
    {
        northLight = new TrafficLight();
        southLight = new TrafficLight();

        eastLight = new TrafficLight();
        westLight = new TrafficLight();

        addObject(
            northLight,
            AXIS_LEFT - 15,
            STOP
        );

        addObject(
            southLight,
            AXIS_RIGHT + 15,
            MAX - STOP
        );

        addObject(
            eastLight,
            MAX - STOP,
            AXIS_BOTTOM - 15
        );

        addObject(
            westLight,
            STOP,
            AXIS_TOP + 15
        );
    }

    private void createSensors()
    {
        addObject(
            new Sensor("NORTH"),
            AXIS_LEFT,
            STOP - 200
        );

        addObject(
            new Sensor("SOUTH"),
            AXIS_RIGHT,
            MAX - STOP + 200
        );

        addObject(
            new Sensor("EAST"),
            MAX - STOP + 200,
            AXIS_BOTTOM
        );

        addObject(
            new Sensor("WEST"),
            STOP - 200,
            AXIS_TOP
        );
    }

    private void createRoadNetwork()
    {
        /*
            SPAWNS
        */

        // izquierda -> derecha
        westSpawn =
            new RoadNode(
                MIN,
                AXIS_TOP
            );

        // derecha -> izquierda
        eastSpawn =
            new RoadNode(
                MAX,
                AXIS_BOTTOM
            );

        // arriba -> abajo
        northSpawn =
            new RoadNode(
                AXIS_LEFT,
                MIN
            );

        // abajo -> arriba
        southSpawn =
            new RoadNode(
                AXIS_RIGHT,
                MAX
            );

        spawnNodes.add(westSpawn);
        spawnNodes.add(eastSpawn);

        spawnNodes.add(northSpawn);
        spawnNodes.add(southSpawn);

        /*
            STOP LINES
        */

        RoadNode westStop =
            new RoadNode(
                STOP,
                AXIS_TOP,
                RoadNode.NodeType.STOP_LINE
            );

        RoadNode eastStop =
            new RoadNode(
                MAX - STOP,
                AXIS_BOTTOM,
                RoadNode.NodeType.STOP_LINE
            );

        RoadNode northStop =
            new RoadNode(
                AXIS_LEFT,
                STOP,
                RoadNode.NodeType.STOP_LINE
            );

        RoadNode southStop =
            new RoadNode(
                AXIS_RIGHT,
                MAX - STOP,
                RoadNode.NodeType.STOP_LINE
            );

        westStop.setTrafficLight(westLight);
        eastStop.setTrafficLight(eastLight);

        northStop.setTrafficLight(northLight);
        southStop.setTrafficLight(southLight);

        /*
            NODOS CENTRALES
        */

        // izquierda -> derecha
        RoadNode westCenter =
            new RoadNode(
                AXIS_LEFT,
                AXIS_TOP
            );

        // derecha -> izquierda
        RoadNode eastCenter =
            new RoadNode(
                AXIS_RIGHT,
                AXIS_BOTTOM
            );

        // arriba -> abajo
        RoadNode northCenter =
            new RoadNode(
                AXIS_LEFT,
                AXIS_LEFT
            );

        // abajo -> arriba
        RoadNode southCenter =
            new RoadNode(
                AXIS_RIGHT,
                AXIS_RIGHT
            );

        /*
            CURVAS
        */

        // WEST
        RoadNode westToNorth =
            new RoadNode(
                AXIS_RIGHT,
                AXIS_LEFT
            );

        RoadNode westToSouth =
            new RoadNode(
                AXIS_LEFT,
                AXIS_RIGHT
            );

        // EAST
        RoadNode eastToNorth =
            new RoadNode(
                AXIS_RIGHT,
                AXIS_LEFT
            );

        RoadNode eastToSouth =
            new RoadNode(
                AXIS_LEFT,
                AXIS_RIGHT
            );

        // NORTH
        RoadNode northToWest =
            new RoadNode(
                AXIS_LEFT,
                AXIS_BOTTOM
            );

        RoadNode northToEast =
            new RoadNode(
                AXIS_RIGHT,
                AXIS_TOP
            );

        // SOUTH
        RoadNode southToWest =
            new RoadNode(
                AXIS_LEFT,
                AXIS_BOTTOM
            );

        RoadNode southToEast =
            new RoadNode(
                AXIS_RIGHT,
                AXIS_TOP
            );

        /*
            EXITS
        */

        RoadNode westExit =
            new RoadNode(
                MIN + 10,
                AXIS_BOTTOM,
                RoadNode.NodeType.EXIT
            );

        RoadNode eastExit =
            new RoadNode(
                MAX - 10,
                AXIS_TOP,
                RoadNode.NodeType.EXIT
            );

        RoadNode northExit =
            new RoadNode(
                AXIS_RIGHT,
                MIN + 10,
                RoadNode.NodeType.EXIT
            );

        RoadNode southExit =
            new RoadNode(
                AXIS_LEFT,
                MAX - 10,
                RoadNode.NodeType.EXIT
            );

        /*
            WEST -> DERECHA
        */

        westSpawn.connectTo(westStop, 100);

        // recto
        westStop.connectTo(westCenter, 70);
        westCenter.connectTo(eastExit, 100);

        // arriba
        westStop.connectTo(westToNorth, 15);
        westToNorth.connectTo(northExit, 100);

        // abajo
        westStop.connectTo(westToSouth, 15);
        westToSouth.connectTo(southExit, 100);

        /*
            EAST -> IZQUIERDA
        */

        eastSpawn.connectTo(eastStop, 100);

        // recto
        eastStop.connectTo(eastCenter, 70);
        eastCenter.connectTo(westExit, 100);

        // arriba
        eastStop.connectTo(eastToNorth, 15);
        eastToNorth.connectTo(northExit, 100);

        // abajo
        eastStop.connectTo(eastToSouth, 15);
        eastToSouth.connectTo(southExit, 100);

        /*
            NORTH -> ABAJO
        */

        northSpawn.connectTo(northStop, 100);

        // recto
        northStop.connectTo(northCenter, 70);
        northCenter.connectTo(southExit, 100);

        // izquierda
        northStop.connectTo(northToWest, 15);
        northToWest.connectTo(westExit, 100);

        // derecha
        northStop.connectTo(northToEast, 15);
        northToEast.connectTo(eastExit, 100);

        /*
            SOUTH -> ARRIBA
        */

        southSpawn.connectTo(southStop, 100);

        // recto
        southStop.connectTo(southCenter, 70);
        southCenter.connectTo(northExit, 100);

        // izquierda
        southStop.connectTo(southToWest, 15);
        southToWest.connectTo(westExit, 100);

        // derecha
        southStop.connectTo(southToEast, 15);
        southToEast.connectTo(eastExit, 100);
    }

    private void spawnCar()
    {
        RoadNode spawn =
            spawnNodes.get(
                random.nextInt(spawnNodes.size())
            );

        addObject(
            new Car(spawn),
            spawn.getX(),
            spawn.getY()
        );
    }

    private void definedSpawnCar(RoadNode spawn)
    {
        addObject(
            new Car(spawn),
            spawn.getX(),
            spawn.getY()
        );
    }

    private void drawRoads()
    {
        GreenfootImage bg = getBackground();

        bg.setColor(Color.GRAY);

        /*
            CARRETERA VERTICAL
        */

        bg.fillRect(
            CENTER_MIN,
            MIN,
            CENTER_MAX - CENTER_MIN,
            MAX
        );

        /*
            CARRETERA HORIZONTAL
        */

        bg.fillRect(
            MIN,
            CENTER_MIN,
            MAX,
            CENTER_MAX - CENTER_MIN
        );

        bg.setColor(Color.WHITE);

        /*
            LINEA CENTRAL VERTICAL
        */

        bg.fillRect(
            398,
            MIN,
            4,
            CENTER_MIN
        );

        bg.fillRect(
            398,
            CENTER_MAX,
            4,
            MAX
        );

        /*
            LINEA CENTRAL HORIZONTAL
        */

        bg.fillRect(
            MIN,
            398,
            CENTER_MIN,
            4
        );

        bg.fillRect(
            CENTER_MAX,
            398,
            MAX,
            4
        );
    }
}
