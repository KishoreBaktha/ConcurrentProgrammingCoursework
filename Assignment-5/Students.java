package com.company;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Random;

public class Students extends Thread {
    private String studentName;
    private String labPartner;
    private int getLabPartnerId;
    private int studentId;
    private int port;

    public Students(String studentName, int studentId) {
        this.studentName = studentName;
        this.studentId = studentId;
    }


    public void run() {
        try {
            Socket socket = new Socket("127.0.0.1",8000);
            sendPortRequest(socket);
            PortResponse portResponse = receivePortResponse(socket);
            port = portResponse.getPort();
            socket.close();
            receivePeerRequests();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void receivePeerRequests() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        String requestString = "";

        try {
            serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
            requestString = receiveRequest(socket);

            if(requestString.contains("FindLabPartnerRequest")) {
                findLabPartner(requestString);
            } else if(requestString.contains("LabPartnerRequest")) {
                receiveLabPartnerRequest(requestString);
                sendLabPartnerResponse(socket);
            }

            if(requestString.contains("LabPartnerResponse")){
                receiveLabPartnerResponse(requestString);
                String response = receiveRequest(socket);
                receiveLabPartnerResponse(response);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void findLabPartner(String request) {
        Gson gson = new Gson();
        int randomPeerNumber;
        Random rand = new Random();
        FindLabPartnerRequest req = gson.fromJson(request, FindLabPartnerRequest.class);
        LinkedList<Integer> portList = req.getPortList();
        if(portList.isEmpty()) {
            System.out.println(studentName + " " + studentId + ": I'm partner with myself!");
            return;
            }
        randomPeerNumber = rand.nextInt(portList.size());
        int partnerPort = portList.remove(randomPeerNumber);
        try {
            Socket partnerSocket = new Socket("127.0.0.1", partnerPort);
            sendLabPartnerRequest(partnerSocket);
            String response = receiveRequest(partnerSocket);
            receiveLabPartnerResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(portList.isEmpty()) {
            return;
        }

        randomPeerNumber = rand.nextInt(portList.size());
        int nextStudentPort = portList.remove(randomPeerNumber);
        Socket nextStudentSocket = null;

        try {
            nextStudentSocket = new Socket("127.0.0.1", nextStudentPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FindLabPartnerRequest newRequest = new FindLabPartnerRequest(portList);
        sendFindLabPartnerRequest(nextStudentSocket, newRequest);
    }


    private void sendFindLabPartnerRequest(Socket socket, FindLabPartnerRequest request) {
        Gson gson = new Gson();
        String json = gson.toJson(request);
        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            out.write(json);
            out.write("\n");
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();

        }
    }




    private PortResponse receivePortResponse(Socket socket) {
        StringBuilder sb = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response;
            sb = new StringBuilder();
            response = reader.readLine();
            sb.append(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        System.out.println(sb.toString());
        System.out.println("here");
        return gson.fromJson(sb.toString(), PortResponse.class);

    }

    private void sendPortRequest(Socket socket) {
        PortRequest request = new PortRequest();
        Gson gson = new Gson();
        String json = gson.toJson(request);
        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            out.write(json);
            out.write("\n");
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLabPartnerRequest(Socket socket) throws IOException {
        LabPartnerRequest request = new LabPartnerRequest(studentName, studentId);
        Gson gson = new Gson();
        String json = gson.toJson(request);
        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            out.write(json);
            out.write("\n");
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();

        }
    }


    private void receiveLabPartnerRequest(String request) {
        Gson gson = new Gson();
        LabPartnerRequest req = gson.fromJson(request, LabPartnerRequest.class);
        this.labPartner = req.getStudentName();
        this.getLabPartnerId = req.getStudentId();
        System.out.println(studentName + " " + studentId +": I've received a lab partner! " + "LabPartner: " + labPartner);

    }

    private String receiveRequest(Socket socket){
        StringBuilder sb = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            sb = new StringBuilder();
            line = reader.readLine();
            sb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(sb.toString());
        return sb.toString();
    }

    private void receiveLabPartnerResponse(String request) {
        Gson gson = new Gson();
        LabPartnerResponse req = gson.fromJson(request, LabPartnerResponse.class);
        this.labPartner = req.getLabPartnerName();
        this.getLabPartnerId = req.getLabPartnerId();
        System.out.println(studentName + " " + studentId +": I've received a lab partner! " + "LabPartner: " + labPartner);

    }

    private void sendLabPartnerResponse(Socket socket) {
        LabPartnerResponse request = new LabPartnerResponse(studentName, studentId);
        Gson gson = new Gson();
        String json = gson.toJson(request);
        try {
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            out.write(json);
            out.write("\n");
            out.flush();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
 class PortResponse {
    private int port;
    private final String requestType = "portResponse";
    public PortResponse(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }
}
 class FindLabPartnerRequest {
    private LinkedList<Integer> portList;
    private final String requestType = "FindLabPartnerRequest";

    public FindLabPartnerRequest(LinkedList<Integer> portList) {
        this.portList = portList;

    }

    public LinkedList<Integer> getPortList() {
        return this.portList;
    }
}
 class PortRequest {
    private final String requestType = "portRequest";
}
 class LabPartnerResponse{
    private String labPartnerName;
    private int labPartnerId;
    private final String requestType = "LabPartnerResponse";

    public LabPartnerResponse(String labPartnerName, int labPartnerId) {
        this.labPartnerName = labPartnerName;
        this.labPartnerId = labPartnerId;
    }

    public String getLabPartnerName() {
        return this.labPartnerName;
    }

    public int getLabPartnerId() {
        return this.labPartnerId;
    }
}
 class LabPartnerRequest {
    private String studenName;
    private int studentId;
    private final String requestType = "LabPartnerRequest";
    public LabPartnerRequest(String studenName, int studentId) {
        this.studenName = studenName;
        this.studentId = studentId;
    }

    public String getStudentName() {
        return this.studenName;
    }

    public int getStudentId() {
        return this.studentId;
    }
}