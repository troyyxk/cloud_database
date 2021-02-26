package ecs;

import java.util.concurrent.CountDownLatch;

public class ECSzkWatcher {
    private CountDownLatch zkAgentsSemaphores;
    public void setSemaphore(int serverNum) {
        this.zkAgentsSemaphores = new CountDownLatch(serverNum);
    }
}
