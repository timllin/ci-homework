package kalimullinti;

public final class DataPackage {
    private final int destinationNode;

    private final String data;

    private long startTime;
    private long bufferTime;
    private String status;

    DataPackage(int destinationNode, String data) {
        this.destinationNode = destinationNode;

        this.data = data;

        // Фиксируется время, когда создаётся пакет данных. Необходимо для
        // вычисления времени доставки до узла назначения.
        startTime = System.nanoTime();
        bufferTime = 0;
        status = "NOT_DONE";
    }


    public int getDestinationNode() {
        return destinationNode;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBufferTime(long bufferTime) {
        this.bufferTime = bufferTime;
    }

    public long getBufferTime() {
        return bufferTime;
    }
}


