//package com.company;
//
//import com.google.gson.Gson;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.LinkedList;
//import java.util.Scanner;
//import java.util.concurrent.BlockingQueue;
//
//
//public class Server {
//    private ServerSocket serverSocket;
//  //  private BlockingQueue<Socket> ArrayBlockingQueue;
//    private static Object socketsListLock = new Object();
//    private static LinkedList<Socket> socketsList = new LinkedList<Socket>();
//   // private String className;
//    private int classSize;
//    public Server( int classSize) {
//        int port = 8000;
//        this.classSize = classSize;
//        try {
//            serverSocket = new ServerSocket(port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String args[]) {
//
//        Server teacher = new Server(7);
//        teacher.startAcceptingRequests();
//    }
//
//    public void startAcceptingRequests() {
//        int clientId = 0;
//        while(true) {
//            try {
//                 if(clientId==classSize)
//                 {
//                     clientId=0;
//                     socketsList = new LinkedList<Socket>();
//                 }
//                System.out.println("Starting to accept connections");
//                Socket socket = serverSocket.accept();
//                RequestHandler requestHandler = new RequestHandler(socket, clientId);
//               Thread.sleep(500);
//                requestHandler.start();
//                clientId++;
//                System.out.println(clientId);
//
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public class RequestHandler  extends Thread{
//        private Socket socket;
//        private int clientId;
//        public RequestHandler(Socket socket, int clientId) throws IOException {
//            this.socket = socket;
//            this.clientId = clientId;
//
//        }
//
//        @Override
//        public void run() {
//            findLabPartner();
//        }
//
//        private boolean findLabPartner() {
//            Socket partnerSocket = null;
//            synchronized (socketsListLock) {
//                partnerSocket = socketsList.pollFirst();
//            }
//            if(partnerSocket == null) {
//
//                if((clientId + 1) == (classSize)) {
//                    if(classSize % 2 == 0) {
//                        //nothing
//                    } else {
//                        LabPartnerRequest thisStudentsRequest = readRequest(this.socket);
//                        sendResponse(this.socket, thisStudentsRequest);
//                        return true;
//                    }
//                }
//
//                System.out.println("list empty");
//                synchronized (socketsListLock) {
//                    //Stand in line
//                    socketsList.add(this.socket);
//                }
//                return false;
//            } else {
//                LabPartnerRequest partnerRequest = readRequest(partnerSocket);
//                LabPartnerRequest thisStudentsRequest = readRequest(this.socket);
//                sendResponse(this.socket, partnerRequest);
//                sendResponse(partnerSocket, thisStudentsRequest);
//            }
//            return true;
//        }
//
//        private LabPartnerRequest readRequest(Socket socket) {
//            StringBuilder sb = null;
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String labPartnerRequest;
//                sb = new StringBuilder();
//                labPartnerRequest = reader.readLine();
//                sb.append(labPartnerRequest);
//                System.out.print(labPartnerRequest);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Gson gson = new Gson();
//            return gson.fromJson(sb.toString(), LabPartnerRequest.class);
//        }
//
//        private void sendResponse(Socket socket, LabPartnerRequest partnerRequest) {
//            LabPartnerResponse response = new LabPartnerResponse(partnerRequest.getStudentName(), partnerRequest.getStudentId());
//            Gson gson = new Gson();
//            String json = gson.toJson(response);
//            PrintWriter writer = null;
//            try {
//                writer = new PrintWriter(socket.getOutputStream() , true);
//                writer.println(json);
//                writer.flush();
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
//class LabPartnerResponse {
//    private String labPartnerName;
//    private int labPartnerId;
//
//    public LabPartnerResponse(String labPartnerName, int labPartnerId) {
//        this.labPartnerName = labPartnerName;
//        this.labPartnerId = labPartnerId;
//    }
//}
//class LabPartnerRequest {
//    private String studenName;
//    private int studentId;
//    public LabPartnerRequest(String studenName, int studentId) {
//        this.studenName = studenName;
//        this.studentId = studentId;
//    }
//
//    public String getStudentName() {
//        return this.studenName;
//    }
//
//    public int getStudentId() {
//        return this.studentId;
//    }
//}