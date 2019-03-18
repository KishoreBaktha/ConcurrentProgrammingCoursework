package com.company;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class  nest  {
    int W;int count;
    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    public nest(int W)
    {
        this.W=W;
        count=W;
    }
    public  void eat(int id) {
        lock.lock();
        if (count==0)
        {
            System.out.println("Consumer-  " + id +"wakes up producer");
               // notEmpty.signal();
                notFull.signal();
               lock.unlock();
        }
        else
        {
            System.out.println("Consumer-  " + id + "consumed-" + count);
            count-=1;
            try {
                lock.unlock();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
            }
        }
    }
    public  void put() {
       lock.lock();
        if(count==0)
        {
            count+=W;
            System.out.println("Producer produced:  " + count);
            try {
                notFull.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                notFull.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
        }

    }
}

class BabyBirds extends Thread {
    nest RW;
    int id;
    Random r = new Random();
    public BabyBirds(nest RW,int id)
    {
        this.RW=RW;
        this.id=id;
    }
    public void run() {
        while(true){
            try {
               sleep(r.nextInt(1000));
            } catch (InterruptedException e) {};
            RW.eat(id);
        }
    }
}

class ParentBird extends Thread {
    nest RW;
    Random r = new Random();
    public ParentBird(nest RW)
    {
        this.RW=RW;
    }
    public void run() {
       while(true) {
            try {
                sleep(r.nextInt(1000));
            } catch (InterruptedException e) {};
            RW.put();
        }
    }
}

public class Hungrybirds {
    static Scanner scanner = new Scanner(System.in);
    static nest nestmonitor;
    static int W;
    // driver program -- two readers and one writer
    public static void main(String[] arg) {
      //  int rounds = Integer.parseInt(arg[0],10);
        System.out.println("Enter number of baby birds");
        String input = scanner.nextLine();
        System.out.println("Enter W(number of worms)");
        String Winput = scanner.nextLine();
        W = Integer.parseInt(Winput);
        nestmonitor=new nest(W);
        int num_consumers;
        num_consumers = Integer.parseInt(input);
        new ParentBird(nestmonitor).start();
        for(int i=0;i<num_consumers;i++)
        new BabyBirds(nestmonitor,i).start();
    }
}
