package org.gokhanka.sales.salesservice.sales.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.gokhanka.sales.salesservice.sales.data.AdjustmentToSale;
import org.gokhanka.sales.salesservice.sales.data.Message;
import org.gokhanka.sales.salesservice.sales.data.MultiOccurSale;

public class MessageProcessor extends Thread {

    private LinkedBlockingQueue<Message> inQueue            = null;
    private ExecutorService              pool               = null;
    private static Object                mutex              = new Object();
    private boolean                      keepWalking        = true;
    private static MessageProcessor      instance           = null;
    private static AtomicInteger         requestCount;
    private static int                   reportingThreshold = 10;
    private static int                   serviceStopPoint   = 50;

    /**
     * Constructor of the singleton. As the requirement stated so single threaded consumer pool is used
     */
    private MessageProcessor() {
        this.inQueue = new LinkedBlockingQueue<Message>(); // memory can be a problem when consumer is too slow
        requestCount = new AtomicInteger(0);
        DaemonThreadFactory dtf = new DaemonThreadFactory();
        //  this.pool = Executors.newFixedThreadPool(n,dtf); 
        this.pool = Executors.newSingleThreadExecutor(dtf); //single threaded consumer
        this.pool.execute(this);
    }

    /** 
     * Singleton Class access method. 
     * @return
     */
    public static MessageProcessor getInstance() {
        if (instance == null) {
            synchronized (mutex) {
                if (instance == null)
                    instance = new MessageProcessor();
            }
        }
        return instance;
    }

    public static int getReportingThreshold() {
        return reportingThreshold;
    }

    public static int getServiceStopPoint() {
        return serviceStopPoint;
    }

    public static void setReportingThreshold(int newVal) {
        reportingThreshold = newVal;
    }

    public static void setServiceStopPoint(int newVal) {
        serviceStopPoint = newVal;
    }

    private static void resetCounter() {
        requestCount.set(0);
    }

    /**
     * Where the incoming messages are put to queue (inQueue)
     * @param msg
     * @return
     */
    public boolean putToQueue(Message msg) {
        boolean result = true;
        result = this.inQueue.offer(msg);
        return result;
    }

    /**
     * to halt when the specific threshold exceeded
     */
    public void stopWorking() {
        this.keepWalking = false;
        this.interrupt();
        this.pool.shutdownNow();
    }

    /**
     * for testing purposes it may be needed to make the class awake
     */
    public void reWork() {
        this.keepWalking = true;
        resetCounter();
        DaemonThreadFactory dtf = new DaemonThreadFactory();
        //  this.pool = Executors.newFixedThreadPool(n,dtf); 
        this.pool = Executors.newSingleThreadExecutor(dtf); //single threaded consumer
        this.pool.execute(this);
    }

    public boolean isWorking() {
        return this.keepWalking;
    }

    /**
     * Helper Method for the Consumer to process a message according to its type
     * @param msg
     */
    private void processMessage(Message msg) {
        switch (msg.getType()) {
        case SALE:
            DataStore.getInstance().storeSale(msg.getSale(), 1);
            System.out.println("Single Occurence of sale is processed");
            break;
        case MULTI_OCCUR_SALE:
            DataStore.getInstance().storeSale(msg.getSale(),
                                              ((MultiOccurSale) msg.getSale()).getNumberOfOccurence());
            System.out.println("Multiple Occurence of sales are processed");
            break;
        case ADJUSTMENT_OF_SALE:
            DataStore.getInstance().adjustmentToSale(((AdjustmentToSale) msg.getSale()));
            System.out.println("Adjustment Done to Product");
            break;
        default:
            break;
        }
        checkPoint();
    }

    /**
     * Checking if the thresholds are reached
     */
    private void checkPoint() {
        int whereWeAre = requestCount.incrementAndGet();
        if (whereWeAre % reportingThreshold == 0) {
            DataStore.getInstance().printSaleReportToConsole();
        }
        if (whereWeAre % serviceStopPoint == 0) {
            stopWorking();
            DataStore.getInstance().printAdjReportToConsole();
        }
    }

    /**
     * Run method for the Consumer Thread
     */
    @Override
    public void run() {
        Message message = null;
        while (this.keepWalking) {
            try {
                message = inQueue.poll(1, TimeUnit.SECONDS);
                if (message != null)
                    processMessage(message);
            } catch (Exception e) {

            }
        }
    }
}
