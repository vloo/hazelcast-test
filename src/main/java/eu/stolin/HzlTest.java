package eu.stolin;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Map;
import java.util.Random;
import java.util.Set;

public class HzlTest {

    private static volatile boolean running = true;
    private static Thread myThread = null;
    private static HazelcastInstance instance = null;

    public static void main(String[] args) {

        myThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("ctrl + c registered - Shutdown Hook is running");
                running = false;
                try {
                    HzlTest.myThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException("interrupted", e);
                }
                System.out.println("going to shut down hazelcast");
                instance.shutdown();
                System.out.println("Shutdown Hook finished");
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted", e);
                }
                Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                for (Thread thread : threadSet) {
                    System.out.println("remaining thread: " + thread.getName() + thread.getThreadGroup().toString());
                }
            }
        });


        Config cfg = new Config();
        cfg.setProperty("hazelcast.shutdownhook.enabled", "false");
        instance = Hazelcast.newHazelcastInstance(cfg);
        Map<Integer, String> mapCustomers = instance.getMap("customers");

        while (running) {
            Random random = new Random();
            try {
                mapCustomers.put(random.nextInt(), String.valueOf(random.nextInt()));
                System.out.println("Map Size:" + mapCustomers.size());
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException("interrupted", e);
            }

            System.out.println("running");
        }
        System.out.println("finished");
    }
}
