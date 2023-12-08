package kalimullinti;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.io.File;

public class AppTest {
    @Test
    void test1() {
        int nodes_amount = 10;
        int data_amount = 3;
        RingProcessor ringProcessor = new RingProcessor(nodes_amount, data_amount, new File("TestTokenRing.log"));
        Assertions.assertThat(ringProcessor.getNodesAmount()).isEqualTo(10);
    }

    @Test
    void test2() {
        int nodes_amount = 50;
        int data_amount = 15;
        RingProcessor ringProcessor = new RingProcessor(nodes_amount, data_amount, new File("TestTokenRing.log"));
        Assertions.assertThat(ringProcessor.getNodesAmount()).isEqualTo(50);
    }

    @Test
    void test3() {
        DataPackage dataPackage = new DataPackage(1, "TestData");
        Assertions.assertThat(dataPackage.getData()).isEqualTo("TestData");
    }

    @Test
    void test4() {
        DataPackage dataPackage = new DataPackage(1, "TestData");
        Assertions.assertThat(dataPackage.getStatus()).isEqualTo("NOT_DONE");
        dataPackage.setStatus("TEST_DONE");
        Assertions.assertThat(dataPackage.getStatus()).isEqualTo("TEST_DONE");
    }

    @Test
    void test5() {
        DataPackage dataPackage = new DataPackage(1, "TestData");
        Assertions.assertThat(dataPackage.getDestinationNode()).isEqualTo(1);
    }

    @Test
    void test6() {
        DataPackage dataPackage = new DataPackage(1, "TestData");
        Assertions.assertThat(dataPackage.getBufferTime()).isEqualTo(0);
    }


    @Test
    void test7() {
        DataPackage dataPackage1 = new DataPackage(1, "TestData1");
        DataPackage dataPackage2 = new DataPackage(1, "TestData2");

        BufferStack bufferStack = new BufferStack();

        bufferStack.addDataPackage(dataPackage1);
        Assertions.assertThat(bufferStack.getSize()).isEqualTo(1);
        bufferStack.addDataPackage(dataPackage2);
        Assertions.assertThat(bufferStack.getSize()).isEqualTo(2);
        bufferStack.peekDataPackage();
        Assertions.assertThat(bufferStack.getSize()).isEqualTo(2);
        bufferStack.pollDataPackage();
        Assertions.assertThat(bufferStack.getSize()).isEqualTo(1);
    }

    @Test
    void test8() {
        int nodes_amount = 2;
        int data_amount = 1;
        RingProcessor ringProcessor = new RingProcessor(nodes_amount, data_amount, new File("TestTokenRing.log"));

        Node nodeTest = new Node(3, ringProcessor.getCoreId(), 3, ringProcessor);
        Assertions.assertThat(nodeTest.getId()).isEqualTo(3);
        DataPackage dataPackage1 = new DataPackage(1, "TestData1");
        nodeTest.setData(dataPackage1);
        Assertions.assertThat(nodeTest.getData().getData()).isEqualTo("TestData1");
    }
}
