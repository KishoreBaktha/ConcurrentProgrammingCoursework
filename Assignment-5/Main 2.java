//package com.company;
//
//import com.google.gson.Gson;
//
//import java.io.*;
//import java.net.Socket;
//import java.nio.charset.StandardCharsets;
//
//public class Main {
//
//    public static void main(String args[]) {
//
//        Student student_one = new Student("Sam",0);
//        Student student_two = new Student("David",1);
//        Student student_three = new Student("Aswin",2);
//        Student student_four = new Student("Rahul",3);
//       Student student_five = new Student("Rohit",4);
//        Student student_six = new Student("Vivek",5);
//        Student student_seven = new Student("Mohit",6);
//        student_one.start();
//        student_two.start();
//        student_three.start();
//        student_four.start();
//        student_five.start();
//        student_six.start();
//        student_seven.start();
//    }
//}
//class Student extends Thread {
//    private String studentName;
//    private int studentId;
//
//    public Student(String studentName, int studentId) {
//        this.studentName = studentName;
//        this.studentId = studentId;
//    }
//
//
//    public void run() {
//        try {
//            Socket socket = new Socket("127.0.0.1",8000);
//            System.out.println("Client Socket Created");
//            sendLabPartnerRequest(socket);
//            System.out.println("Request Sent!");
//            receiveLabPartner(socket);
//            System.out.println();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendLabPartnerRequest(Socket socket) throws IOException {
//        LabPartnerRequest request = new LabPartnerRequest(studentName, studentId);
//        Gson gson = new Gson();
//        String json = gson.toJson(request);
//        try {
//            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
//            out.write(json);
//            out.write("\n");
//            out.flush();
//        } catch(Exception e) {
//            e.printStackTrace();
//
//        }
//    }
//
//    private String receiveLabPartner(Socket socket) {
//        StringBuilder sb = null;
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            String labPartner;
//            sb = new StringBuilder();
//            while((labPartner = reader.readLine()) != null) {
//                sb.append(labPartner);
//            }
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(studentName + " " + studentId +": I've received a lab partner! " + sb.toString());
//        return sb.toString();
//    }
//}