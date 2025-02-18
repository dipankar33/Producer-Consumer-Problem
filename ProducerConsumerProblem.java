package MultiThreading;

class SharedResource {
    private final int[] buffer;
    private int count, idx, out;

    SharedResource(int size) {
        buffer = new int[size];
        count = 0;
        idx = 0;
        out = 0;
    }

    public synchronized void produce(int item) throws InterruptedException {
        while (count == buffer.length) {
            wait(); // wait until buffer is full
        }

        buffer[idx] = item;
        idx = (idx + 1) % buffer.length; // ensure idx stays within bounds
        count++;
        System.out.println("Item produced: " + item);
        notify(); // notify consumer that item is available
    }

    public synchronized int consume() throws InterruptedException {
        while (count == 0) {
            wait(); // wait if buffer is empty
        }
        int item = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.println("Item consumed: " + item);
        notify();

        return item;
    }

    public int getCount() {
        return count;
    }
}

public class ProducerConsumerProblem {
    private static volatile boolean running = true; // Flag to stop threads

    public static void main(String[] args) {
        SharedResource buffer = new SharedResource(10);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // Stop the consumer thread once producer finishes
        setRunning(false); // Set the flag to false to stop threads
        consumerThread.interrupt(); // Interrupt if the consumer is in sleep state

        try {
            consumerThread.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean value) {
        running = value;
    }
}

class Producer implements Runnable {
    private final SharedResource buffer;
    private static final int MAX_COUNT = 100;

    Producer(SharedResource buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        for (int i = 0; i < MAX_COUNT; i++) {
            try {
                buffer.produce(i);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
        // Signal when producer finishes producing all items
        ProducerConsumerProblem.setRunning(false); // Use setter method to update running flag
    }
}

class Consumer implements Runnable {
    private final SharedResource buffer;

    Consumer(SharedResource buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (ProducerConsumerProblem.isRunning() || buffer.getCount() > 0) { // Continue until both conditions are false
            try {
                buffer.consume();
                Thread.sleep(150);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
        System.out.println("Consumer stopped");
    }
}
hello changing the code here
