package com.company;

public class Peer {

    public static void main(String args[]) {
        Students student_one = new Students("Sam",0);
        Students student_two = new Students("David",1);
        Students student_three = new Students("Aswin",2);
        Students student_four = new Students("Rahul",3);
       Students student_five = new Students("Rohit",4);
        Students student_six = new Students("Vivek",5);
        Students student_seven = new Students("Mohit",6);
        student_one.start();
        student_two.start();
        student_three.start();
        student_four.start();
        student_five.start();
        student_six.start();
        student_seven.start();
    }
}