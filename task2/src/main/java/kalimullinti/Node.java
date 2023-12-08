package kalimullinti;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public final class Node implements Runnable {
    private final int nodeId;

    private final int coreId;
    private Semaphore semaphore;

    private BufferStack bufferStack = new BufferStack();

    private List<DataPackage> allData;

    private RingProcessor ringProcessor;

    //private static volatile boolean exit = false;
    public Node(int nodeId, int coreId, int threadsForNode, RingProcessor ringProcessor) {
        this.nodeId = nodeId;

        this.coreId = coreId;

        this.semaphore = new Semaphore(threadsForNode);

        this.ringProcessor = ringProcessor;

        if (nodeId == coreId) {
            allData = new ArrayList<>();
        }
    }

    public int getId() {
        return nodeId;
    }

    /**
     * adding dataPackage to node's bufferStack
     * @param dataPackage
     */
    public void setData(DataPackage dataPackage) {
        dataPackage.setBufferTime(System.nanoTime());
        bufferStack.addDataPackage(dataPackage);
    }

    public DataPackage getData() {
        return bufferStack.peekDataPackage();

    }

    public BufferStack getBuffer() {
        return bufferStack;
    }
    private int getItemCount(List list) {
        if (list == null) return 0;
        return list.size();
    }

    /**
     * logic of ring's dataPackage movement
     */
    private void dataPipeline() {
        DataPackage dataPackage = bufferStack.pollDataPackage();

        if (dataPackage == null) {
            return;
        }

        long currentTime = System.nanoTime();
        ringProcessor.addBufferTimeList(currentTime - dataPackage.getBufferTime());

        //if nodeId == destination then change status and move to coreNode
        if (nodeId == dataPackage.getDestinationNode()) {
            ringProcessor.addTimeList(currentTime - dataPackage.getStartTime());
            dataPackage.setStatus("DONE");
            ringProcessor.reachedDestination(dataPackage, nodeId);
            try {
                Thread.sleep(1);
                ringProcessor.moveDataPackage(dataPackage, nodeId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        //else if nodeId == coreNode and status == Done then add to coreNodeData
        } else if (dataPackage.getStatus().equals("DONE") && nodeId == coreId) {
            allData.add(dataPackage);
        //else if (nodeId != destinationId) or (status != done and nodeId == coreId)
            // or (status == Done and nodeId != coreId)
        } else {
            try {
                Thread.sleep(1);
                ringProcessor.moveDataPackage(dataPackage, nodeId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Начало работы узла. То есть из Node.bufferStack берётся пакет с данными
     * и отправляется на обработку, после чего передаётся следующему узлу.
     * Тут заключена логика, согласно которой обрабатываться может только 3 пакета данных одновременно.
     */
    @Override
    public void run() {
        while (true) {
            try {
                semaphore.acquire();
                dataPipeline();
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


