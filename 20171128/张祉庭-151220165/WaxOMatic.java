import java.util.concurrent.*;
import java.util.Random;

//import static net.mindview.util.Print.*;


class Car {
    private boolean waxOn = false;
    private boolean WaxRandom;


    public synchronized void waxed() {

        waxOn = true; // Ready to buff
        Random random=new Random();
        WaxRandom=random.nextBoolean();
 //       System.out.println(WaxRandom);
        notifyAll();

    }

    public synchronized void buffed() {
        waxOn = false; // Ready for another coat of wax
 //       Random random=new Random();
        //      WaxRandom=random.nextBoolean();
        notifyAll();//唤醒其他
    }

    public synchronized void WaxOn1waitForBuffing()
            throws InterruptedException {
        while (waxOn == true||WaxRandom==false)
            wait();
    }

    public synchronized void WaxOn2waitForBuffing()
            throws InterruptedException {
        while(waxOn==true||WaxRandom==true)
            wait();
    }

    public synchronized void waitForWaxing()
            throws InterruptedException {
        while (waxOn == false)
            wait();
    }
}

class WaxOn1 implements Runnable {
    private Car car;

    public WaxOn1(Car c) {
        car = c;
    }

    public  void run() {
        synchronized(car)
        {
            try {
                while (!Thread.interrupted()) {
                    System.out.println("WaxOn1: Wax On! \n");
                    TimeUnit.MILLISECONDS.sleep(200);
                    car.waxed();//ready to buff
                    car.WaxOn1waitForBuffing();
                }
            } catch (InterruptedException e) {
                System.out.println("Exiting via interrupt");
            }
            System.out.println("Ending Wax On task");
        }
    }
}
class WaxOn2 implements Runnable {
    private Car car;

    public WaxOn2(Car c) {
        car = c;
    }

    public synchronized void run() {
        synchronized(car)
        {
            try {
                while (!Thread.interrupted()) {
                    System.out.println("WaxOn2: Wax On! \n");
                    TimeUnit.MILLISECONDS.sleep(200);
                    car.waxed();//ready to buff
                    car.WaxOn2waitForBuffing();
                }
            } catch (InterruptedException e) {
                System.out.println("Exiting via interrupt");
            }
            System.out.println("Ending Wax On task");
        }
    }

}
class WaxOff implements Runnable {
    private Car car;

    public WaxOff(Car c) {
        car = c;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                car.waitForWaxing();
                System.out.println("Wax Off! \n");
                TimeUnit.MILLISECONDS.sleep(200);
                car.buffed();
            }
        } catch (InterruptedException e) {
            System.out.println("Exiting via interrupt");
        }
        System.out.println("Ending Wax Off task");
    }
}

public class WaxOMatic {
    public static void main(String[] args) throws Exception {
        Car car = new Car();
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new WaxOff(car));//启动WaxOff
        exec.execute(new WaxOn1(car));//启动WaxOn1
        exec.execute(new WaxOn2(car));//启动WaxOn2
        TimeUnit.SECONDS.sleep(10); // Run for a while...
        exec.shutdownNow(); // Interrupt all tasks
    }
}