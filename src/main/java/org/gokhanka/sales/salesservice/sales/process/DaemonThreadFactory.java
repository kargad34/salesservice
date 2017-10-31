package org.gokhanka.sales.salesservice.sales.process;

import java.util.concurrent.ThreadFactory;
/**
 * in order to bind the executor to the main thread's lifecycle
 * this threadfactory is used
 * @author gokhanka
 *
 */
public class DaemonThreadFactory implements ThreadFactory {
    
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    }
}