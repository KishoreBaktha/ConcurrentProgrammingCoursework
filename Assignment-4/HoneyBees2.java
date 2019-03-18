package com.company;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class  nest3  {
    int H;int count;
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    public nest3(int H)
    {
        this.H=H;
        count=0;
    }
    public  void eat() {
        lock.lock();
        if(count==H)
        {
            count=0;
            System.out.println("Beer consumed honey");
            try {
                notEmpty.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                notEmpty.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
            }
        }
    }
    public  void put(int id) {
        lock.lock();
        if (count==H)
        {
            System.out.println("Bee-  " + id +"wakes up beer");
                notEmpty.signal();
                lock.unlock();
        }
        else
        {
            count+=1;
            System.out.println("Bee-  " + id + " put-" + count);
            try {
                lock.unlock();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Bees2 extends Thread {
    nest3 RW;
    int id;
    Random r = new Random();
    public Bees2(nest3 RW,int id)
    {
        this.RW=RW;
        this.id=id;
    }
    public void run() {
        while(true){
            try {
               sleep(r.nextInt(1000));
            } catch (InterruptedException e) {};
            RW.put(id);
        }
    }
}

class Beer2 extends Thread {
    nest3 RW;
    Random r = new Random();
    public Beer2(nest3 RW)
    {
        this.RW=RW;
    }
    public void run() {
       while(true) {
            try {
                sleep(r.nextInt(1000));
            } catch (InterruptedException e) {};
            RW.eat();
        }
    }
}

public class HoneyBees2 {
    static Scanner scanner = new Scanner(System.in);
    static nest3 nestmonitor;
    static int H;
    // driver program -- two readers and one writer
    public static void main(String[] arg) {
      //  int rounds = Integer.parseInt(arg[0],10);
        System.out.println("Enter number of bees");
        String input = scanner.nextLine();
        System.out.println("Enter H(capacity of pot)");
        String Hinput = scanner.nextLine();
        H = Integer.parseInt(Hinput);
        nestmonitor=new nest3(H);
        int num_consumers;
        num_consumers = Integer.parseInt(input);
        for(int i=0;i<num_consumers;i++)
        new Bees2(nestmonitor,i).start();
        new Beer2(nestmonitor).start();
    }
}
