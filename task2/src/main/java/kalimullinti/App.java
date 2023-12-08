package kalimullinti;
import java.io.File;
public final class App {
    private static final int NODES_AMOUNT = 10;
    private static final int DATA_AMOUNT = 4;

    private App() {
    }

    public static void main(String[] args) {
        RingProcessor ringProcessor = new RingProcessor(NODES_AMOUNT, DATA_AMOUNT, new File("TokenRing.log"));
        ringProcessor.startProcessing();
        ringProcessor.logAverageTine();
        System.exit(0);

    }
}
