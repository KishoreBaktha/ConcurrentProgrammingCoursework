package com.company;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main
{

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of baby birds");
        String input = scanner.nextLine();
        System.out.println("Enter W(number of worms)");
        String Winput = scanner.nextLine();
        int num_consumers,W;
        num_consumers = Integer.parseInt(input);
        W = Integer.parseInt(Winput);
        Dish implementation = new Dish(W);
        Parent parent=new Parent(implementation,0);
        parent.start();
        BabyBirds[] babybirds = new BabyBirds[num_consumers];

        for (int consumer_count = 0; consumer_count < num_consumers; consumer_count++) {
            babybirds[consumer_count] = new BabyBirds(implementation,consumer_count);
            babybirds[consumer_count].start();
        }
       parent.join();
        for (int consumer_count = 0; consumer_count < num_consumers; consumer_count++) {
            babybirds[consumer_count].join();
        }
        }
}

 class Parent extends Thread{

    protected Dish implementation;
    int id;

    public Parent (Dish implementation,int id){
        this.id=id;
        this.implementation = implementation;
    }

    @Override
    public void run(){
        try{
            while(true){
                implementation.put(id);
            }
        }catch(InterruptedException e){
        }
    }
}


 class BabyBirds extends Thread
{

    protected Dish implementation;
    int id;

    public BabyBirds (Dish implementation,int id){
        this.implementation = implementation;
        this.id=id;
    }
    @Override
    public void run(){
        try{
            while( true){
                implementation.get(id);
            }
        }catch(InterruptedException e){
        }
    }
}

class Dish {

    int count;int W;
    static Semaphore semProd = new Semaphore(0);
    static Semaphore semCon = new Semaphore(1);
    public Dish(int W)
    {
        count=W;
        this.W=W;
    }

    public void put(int id) throws InterruptedException {

        semProd.acquire();
        try {
                count+=W;
                System.out.println("Parent Bird-" + id + " produced-  " + count);
        } finally {
        }
    }

    public void get(int id) throws InterruptedException {
        semCon.acquire();
            try {
                if (count==0) {
                    System.out.println("Baby Bird- " + id + "wakes up Parent bird");
                    semProd.release();
                    Thread.sleep(10);
                }
                    System.out.println("Baby Bird- " + id + "consumed-" + count);
                   count-=1;
                    semCon.release();
                        Thread.sleep(100);
                //   Thread.sleep((int)Math.random()*10000);
            } finally {
            }
        }
    }

