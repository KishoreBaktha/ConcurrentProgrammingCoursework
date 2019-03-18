//
//package com.company;
//import java.util.Random;
//import java.util.Scanner;
//
//class  nest  {
//
//    int W;int count;
//    public nest(int W)
//    {
//        this.W=W;
//        count=W;
//    }
//    public synchronized void eat(int id) {
//        if (count==0) {
//            System.out.println("Consumer- " +id + " wakes up producer");
//            notify();
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
//            System.out.println("Consumer- " + id +" consumed-" + count);
//            count-=1;
//            try {
//                notifyAll();
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public synchronized void put() {
//        if(count==0)
//        {
//            count+=W;
//            System.out.println("Producer produced:  " + count);
//            notifyAll();
//        }
//        else
//        {
//            try { wait(); }
//            catch (InterruptedException ex) {return;}
//        }
//    }
//}
//
//class BabyBirds extends Thread {
//    nest RW;int id;
//    public BabyBirds(nest RW,int id)
//    {
//        this.RW=RW;
//        this.id=id;
//    }
//    Random r = new Random();
//    public void run() {
//        while(true){
//            try {
//                sleep(r.nextInt(1000));
//            } catch (InterruptedException e) {};
//            RW.eat(id);
//        }
//    }
//}
//
//class ParentBird extends Thread {
//    nest RW;
//    Random r = new Random();
//    public ParentBird(nest RW)
//    {
//        this.RW=RW;
//    }
//    public void run() {
//        while(true) {
//            try {
//                sleep(r.nextInt(1000));
//            } catch (InterruptedException e) {};
//            RW.put();
//        }
//    }
//}
//
//public class Main {
//    static Scanner scanner = new Scanner(System.in);
//    static nest nestmonitor;
//    static int W;
//    // driver program -- two readers and one writer
//    public static void main(String[] arg) {
//        //  int rounds = Integer.parseInt(arg[0],10);
//        System.out.println("Enter number of baby birds");
//        String input = scanner.nextLine();
//        System.out.println("Enter W(number of worms)");
//        String Winput = scanner.nextLine();
//        W = Integer.parseInt(Winput);
//        nestmonitor=new nest(W);
//        int num_consumers;
//        num_consumers = Integer.parseInt(input);
//        for(int i=0;i<num_consumers;i++)
//            new BabyBirds(nestmonitor,i).start();
//        new ParentBird(nestmonitor).start();
//    }
//}
