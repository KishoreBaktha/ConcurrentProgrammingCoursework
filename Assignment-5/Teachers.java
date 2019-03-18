package com.company;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;


public class Teachers {
    private ServerSocket serverSocket;
    private int classSize;
    final int serverPort = 8000;


    Object portLock = new Object();
    LinkedList<Integer> portList;

    Object clientCountLock = new Object();

    Object clientsServedLock = new Object();
    private int clientsServed;


    public Teachers( int classSize) {

        this.classSize = classSize;
        this.portList = new LinkedList<Integer>();

        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {

        Teachers teachers = new Teachers( 7);
        teachers.acceptRequests();
        teachers.signalStartToFirstPeer();
        System.out.println("DONE!");
    }


    public void signalStartToFirstPeer() {
        int firstPeerPort = 0;

        Random rand = new Random();
        int randomPeerNumber = 0;

        synchronized (portLock) {
            randomPeerNumber = rand.nextInt(portList.size());
            firstPeerPort = portList.remove(randomPeerNumber);

        }
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", firstPeerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FindLabPartnerRequest newRequest = new FindLabPartnerRequest(portList);

        Gson gson = new Gson();
        String json = gson.toJson(newRequest);
        System.out.println(json);
        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            out.write(json);
            out.write("\n");
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();

        }
    }


    public void acceptRequests() {
        int clientCount = 1;
        boolean allClientsServed = false;
        while(true) {
            try {
                System.out.println("Starting to accept connections");
                Socket socket = serverSocket.accept();
                System.out.println("Socket Created");
                RequestHandler requestHandler = new RequestHandler(socket, clientCount);
                requestHandler.start();

                synchronized (clientCountLock) {
                    if(clientCount == classSize) {
                        break;
                    }
                    clientCount++;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }


        while(!allClientsServed) {
            synchronized (clientsServedLock) {

                if (clientsServed >= classSize) {
                    allClientsServed = true;
                    continue;
                }
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(this.portList.toString());


    }

    public class RequestHandler  extends Thread{
        private Socket socket;
        private int clientId;
        private int clientPort;
        public RequestHandler(Socket socket, int clientId) throws IOException {
            this.socket = socket;
            this.clientId = clientId;

        }

        @Override
        public void run() {
            this.clientPort = serverPort + clientId;
            synchronized (portLock) {
                portList.add(new Integer(clientPort));
            }

            PortRequest request = readPortRequest(socket);
            sendPortResponse(socket, clientPort);
            synchronized (clientsServedLock) {
                clientsServed++;
            }
        }


        private PortRequest readPortRequest(Socket socket) {
            StringBuilder sb = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String labPartnerRequest;
                sb = new StringBuilder();
                labPartnerRequest = reader.readLine();
                sb.append(labPartnerRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(sb.toString());
            System.out.println("here 2");
            Gson gson = new Gson();
            return gson.fromJson(sb.toString(), PortRequest.class);
        }

        private void sendPortResponse(Socket socket, int port) {
            PortResponse response = new PortResponse(port);
            Gson gson = new Gson();
            String json = gson.toJson(response);
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(socket.getOutputStream() , true);
                writer.println(json);
                writer.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}