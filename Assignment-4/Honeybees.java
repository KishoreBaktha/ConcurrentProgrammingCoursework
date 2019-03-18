package com.company;


//  Readers/Writers with concurrent read or exclusive write
//
// Usage:
//         javac rw.real.java
//         java Main rounds
import java.util.Random;
import java.util.Scanner;

class  nest2  {
    // Readers/Writers
    int H;int count;
    public nest2(int W)
    {
        this.H=W;
        count=0;
    }
    public synchronized void eat()
    {
        if (count==H) {
            System.out.println("Beer consumed the honey");
            count=0;
            notifyAll();
        }
        else
        {
            try { wait(); }
            catch (InterruptedException ex) {return;}
        }
    }
    public synchronized void put(int id)
    {
        if(count==H)
        {
            System.out.println("bee : "+id+"wakes up beer");
            notifyAll();
        }
        else
        {
            count+=1;
            System.out.println("Bee- " + id +" put-" + count);
            try {
                notifyAll();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Bees extends Thread {
    nest2 RW;int id;
    public Bees(nest2 RW,int id)
    {
        this.RW=RW;
        this.id=id;
    }
    Random r = new Random();
    public void run() {
        while(true){
            try {
                sleep(r.nextInt(1000));
            } catch (InterruptedException e) {};
            RW.put(id);
        }
    }
}

class Beer extends Thread {
    nest2 RW;
    Random r = new Random();
    public Beer(nest2 RW)
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

public class Honeybees {
    static Scanner scanner = new Scanner(System.in);
    static nest2 nestmonitor;
    static int H;
    // driver program -- two readers and one writer
    public static void main(String[] arg) {
        //  int rounds = Integer.parseInt(arg[0],10);
        System.out.println("Enter number of bees");
        String input = scanner.nextLine();
        System.out.println("Enter H(capacity of pot)");
        String Winput = scanner.nextLine();
        H= Integer.parseInt(Winput);
        nestmonitor=new nest2(H);
        int num_consumers;
        num_consumers = Integer.parseInt(input);
        for(int i=0;i<num_consumers;i++)
            new Bees(nestmonitor,i).start();
        new Beer(nestmonitor).start();
    }
}
