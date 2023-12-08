package kalimullinti;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BufferStack {
    private ConcurrentLinkedQueue<DataPackage> bufferStack;

    public BufferStack() {
        bufferStack = new ConcurrentLinkedQueue<DataPackage>();
    }

    /**
     * add dataPackage to bufferStack
     * @param dataPackage
     */
    public void addDataPackage(DataPackage dataPackage) {
        bufferStack.add(dataPackage);
    }

    /**
     * pop dataPackage from bufferStack
     * @return
     */
    public DataPackage pollDataPackage() {
        return bufferStack.poll();
    }

    public DataPackage peekDataPackage() {
        return bufferStack.peek();
    }

    public int getSize() {
        return bufferStack.size();
    }
}
