public class SimpleThreads {

    // Display a message, preceded by the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop
        implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };
            try {
                for (int i = 0; i < importantInfo.length; i++) {
                    // Pause for 4 seconds
                    Thread.sleep(4000);
                    // Print a message
                    threadMessage(importantInfo[i]);
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    private static class PrimeCounter
        implements Runnable {
        public void run() {
            threadMessage("Starting prime number computation...");
            long count = 0;
            for (long n = 2; n < Long.MAX_VALUE; n++) {
                if (Thread.interrupted()) {
                    threadMessage("Interrupted! Primes found so far: " + count);
                    return;
                }
                if (isPrime(n)) {
                    count++;
                }
            }
            threadMessage("Computation complete! Total primes found: " + count);
        }

        private boolean isPrime(long n) {
            for (long i = 2; i * i <= n; i++) {
                if (n % i == 0) return false;
            }
            return true;
        }
    }

    public static void main(String args[])
        throws InterruptedException {

        long patience = 1000 * 60 * 60;

        long cpuPatience = 1000 * 10;

        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        if (args.length > 1) {
            try {
                cpuPatience = Long.parseLong(args[1]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Second argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(new MessageLoop());

        t.start();

        threadMessage("Waiting for MessageLoop thread to finish");

        while (t.isAlive()) {
            threadMessage("Still waiting...");
            t.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {
                threadMessage("Tired of waiting!");
                t.interrupt();
                t.join();
            }
        }
        threadMessage("Finally!");

        threadMessage("Starting PrimeCounter thread (CPU-intensive)");
        long cpuStartTime = System.currentTimeMillis();
        Thread cpuThread = new Thread(new PrimeCounter());
        cpuThread.start();

        threadMessage("Waiting for PrimeCounter thread to finish");

        while (cpuThread.isAlive()) {
            threadMessage("CPU task still running...");
            cpuThread.join(1000);
            if (((System.currentTimeMillis() - cpuStartTime) > cpuPatience) && cpuThread.isAlive()) {
                threadMessage("CPU time limit exceeded, interrupting PrimeCounter!");
                cpuThread.interrupt();
                cpuThread.join();
            }
        }
        threadMessage("PrimeCounter done!");
    }
}
