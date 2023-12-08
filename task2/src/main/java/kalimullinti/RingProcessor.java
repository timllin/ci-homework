package kalimullinti;


import java.io.IOException;
import java.util.Collections;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
/**
 * В конструкторе кольцо инициализируется, то есть создаются все узлы и данные на узлах.
 * В методе {@link RingProcessor#startProcessing()} запускается работа кольца - данные начинают
 * обрабатываться по часовой стрелке. Также производится логгирование в {@link RingProcessor#logs}.
 * Вся работа должна быть потокобезопасной и с обработкой всех возможных исключений. Если необходимо,
 * разрешается создавать собственные классы исключений.
 */
public final class RingProcessor {
    private final int nodesAmount;
    private final int dataAmount;
    private final int threadsForNode = 3;
    private final File logs;
    private final Logger logger;

    private List<Node> nodeList;

    /**
     * Сюда идёт запись времени прохода каждого пакета данных.
     * Используется в {@link RingProcessor#averageTime()} для подсчета среднего времени
     * прохода данных к координатору.
     */

    private List<Long> timeList;
    private List<Long> bufferTimeList;
    private int coreId;


    RingProcessor(int nodesAmount, int dataAmount, File logs) {
        this.nodesAmount = nodesAmount;

        this.dataAmount = dataAmount;

        this.logs = logs;

        logger = Logger.getLogger("ringLogger");
        try {
            FileHandler fileLog = new FileHandler(logs.getAbsolutePath());
            logger.addHandler(fileLog);
        } catch (IOException e) {
            e.printStackTrace();
        }


        timeList = Collections.synchronizedList(new ArrayList());
        bufferTimeList = Collections.synchronizedList(new ArrayList());

        init();
        startRingInfo();
    }

    public int getNodesAmount() {
        return nodesAmount;
    }

    public int getCoreId() {
        return coreId;
    }

    private void createNodes() {
        Random ran = new Random();
        coreId = ran.nextInt(nodesAmount);
        nodeList = Collections.synchronizedList(new ArrayList());

        for (int i = 0; i < nodesAmount; i++) {
            nodeList.add(new Node(i, coreId, threadsForNode, this));
        }
    }

    private int randomDataPackageNode(int cordinatorId) {
        Random ran = new Random();
        int nodeId = ran.nextInt(nodesAmount);
        while (nodeId == cordinatorId) {
            nodeId = ran.nextInt(nodesAmount);
        }
        return nodeId;
    }
    private void createData() {
        int from;
        int to;
        for (int i = 0; i < dataAmount; i++) {
            from = randomDataPackageNode(coreId);
            to = randomDataPackageNode(coreId);
            DataPackage dataPackage = new DataPackage(to, "Data_Package_" + i);
            nodeList.get(from).setData(dataPackage);
        }

    }
    public void addBufferTimeList(long time) {
        bufferTimeList.add(time);
    }

    public void addTimeList(long time) {
        timeList.add(time);
    }
    // Считается среднее время прохода.
    private double[] averageTime() {
        double[] times = new double[2];
        double time = timeList.stream().mapToDouble(d -> d).average().orElse(0.0);
        double bufferTime = bufferTimeList.stream().mapToDouble(d -> d).average().orElse(0.0);
        times[0] = time;
        times[1] = bufferTime;
        return times;
    }

    /**
     * creating nodes and dataPackages
     */
    private void init() {
        createNodes();
        createData();
    }

    private void startRingInfo() {
        logger.info("Nodes: " + nodeList.size());
        logger.info("Coordinator Node: " + coreId);

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            logger.info("Node_" + i + " - " + node.getBuffer().getSize() + " messages");
        }
    }

    public void reachedDestination(DataPackage dataPackage, int nodeId) {
        logger.info("Package: " + dataPackage.getData()  + " reached destinated node: " + nodeId);
    }

    /**
     * setting next node for dataPackage
     * @param dataPackage
     * @param fromNode
     */
    public void moveDataPackage(DataPackage dataPackage, int fromNode) {
        int toNode = (fromNode + 1) % nodesAmount;

        logger.info("Package: " + dataPackage.getData()  + " moved from: " + fromNode + " to: " + toNode);
        nodeList.get(toNode).setData(dataPackage);
    }

    public void logAverageTine() {
        double[] times = averageTime();
        logger.info("Average Travel Time: " + times[0]);
        logger.info("Average Buffer Time: " + times[1]);
    }
    public void startProcessing() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < nodesAmount; i++) {
            Node node = nodeList.get(i);

            for (int j = 0; j < threadsForNode; j++) {
                Thread thread = new Thread(node);
                threads.add(thread);
                thread.start();
            }
        }

        for (Thread thread : threads) {
            try {
                thread.join(5);
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }

    }
}

