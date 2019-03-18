package com.company;

//import jdk.management.cmm.SystemResourcePressureMXBean;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class HoneyBees {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of bees");
        int  num_producers= Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the capacity of pot(H)");
        int  H= Integer.parseInt(scanner.nextLine());
        Pot implementation = new Pot(H);
        Bees[] bees = new Bees[num_producers];
        for (int producer_count = 0; producer_count < num_producers; producer_count++) {
            bees[producer_count] = new Bees(implementation,producer_count);
            bees[producer_count].start();
        }
        Bear bear = new Bear(implementation,0);
        bear.start();
        for (int producer_count = 0; producer_count < num_producers; producer_count++) {
            bees[producer_count].join();
        }
        bear.join();
    }
}

class Bees extends Thread{

    protected Pot implementation;
    int id;

    public Bees (Pot implementation,int id){
        this.id=id;
        this.implementation = implementation;
    }

    @Override
    public void run(){
        try{
            while( true){
                implementation.put(id);
            }
        }catch(InterruptedException e){
        }
    }
}


class Bear extends Thread
{

    protected Pot implementation;
    int id;

    public Bear (Pot implementation,int id){
        this.implementation = implementation;
        this.id=id;
    }
    @Override
    public void run(){
        try{
            while( true){
                implementation.get();
            }
        }catch(InterruptedException e){
        }
    }
}

class Pot
{
    int count=0;int H;
    static Semaphore semProd = new Semaphore(1);
    static Semaphore semCon = new Semaphore(0);
    public Pot(int H)
    {
        this.H=H;
    }

    public void put(int id) throws InterruptedException {

        semProd.acquire();
        try {
                count+=1;
                if(count==H)
                {
                    System.out.println("Bee-" + id + "  contributes"+count);
                    System.out.println("Bee-" + id + " awakens beer");
                    semCon.release();
                   Thread.sleep(95);
                }
             else
            {
                System.out.println("Bee-" + id + "  contributes"+count);
                semProd.release();
                Thread.sleep(100);
              // Thread.sleep((int)Math.random()*1000000);
            }
        } finally {
        }
    }

    public void get() throws InterruptedException {
        semCon.acquire();
        try {
            System.out.println("Bear consumes honey");
            count=0;
            }
         finally {
            semProd.release();
        }
    }
}

