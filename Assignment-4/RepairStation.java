package com.company;

public class RepairStation {

    final static int VEHICLE_TYPE_A = 0;
    final static int VEHICLE_TYPE_B = 1;
    final static int VEHICLE_TYPE_C = 2;

    final static int NUM_VEHICLES = 10;
    final static int NUM_TRIPS = 5;
    final static int MAX_NUM_VEHICLES_A = 2;
    final static int MAX_NUM_VEHICLES_B = 2;
    final static int MAX_NUM_VEHICLES_C = 2;
    final static int MAX_NUM_VEHICLES_TOTAL = 4;

    static RepairStationMonitor repairStation;

    public static void main(String[] arg) {
        repairStation = new RepairStationMonitor(MAX_NUM_VEHICLES_A, MAX_NUM_VEHICLES_B, MAX_NUM_VEHICLES_C, MAX_NUM_VEHICLES_TOTAL);

        //Start NUM_VEHICLES vehicle processes
        for (int i = 0; i < NUM_VEHICLES; i++)
        {
            new Vehicle("" + i, NUM_TRIPS, (int) (Math.random() * 3)).start();
        }
    }

    // Vehicle thread
    private static class Vehicle extends Thread {
        int vehicleType;
        int numTrips;

        public Vehicle(String name, int numTrips, int vehicleType) {
            super(name);
            this.numTrips = numTrips;
            this.vehicleType = vehicleType;
        }

        @Override
        public void run() {
            // Have the vehicles try to get repaired numTrips times
            while (numTrips > 0) {
                try {
                    Thread.sleep((int) (Math.random() * 1000));
                    System.out.println("Vehicle nr " + getName() + " of type " + vehicleType + " approaching repair station.");
                    repairStation.startRepair(vehicleType);
                    System.out.println("Vehicle nr " + getName() + " of type " + vehicleType + " is getting repaired.");
                    Thread.sleep((int) (Math.random() * 500));
                    repairStation.finishRepair(vehicleType);
                    System.out.println("Vehicle nr " + getName() + " of type " + vehicleType + " finished repairing.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                numTrips--;
            }
        }
    }

    private static class RepairStationMonitor {
        int maxParallelVehicleTypes[] = new int[3];
        int maxParallelVehicles;

        int numParallelVehicleTypes[] = new int[3];
        int numParallelVehicles;

        public RepairStationMonitor(int maxParallelVehicleTypeA, int maxParallelVehicleTypeB, int maxParallelVehicleTypeC, int maxParallelVehicles) {
            this.maxParallelVehicleTypes[VEHICLE_TYPE_A] = maxParallelVehicleTypeA;
            this.maxParallelVehicleTypes[VEHICLE_TYPE_B] = maxParallelVehicleTypeB;
            this.maxParallelVehicleTypes[VEHICLE_TYPE_C] = maxParallelVehicleTypeC;
            this.maxParallelVehicles = maxParallelVehicles;
        }

        // Try to start a repair session.
        public synchronized void startRepair(int vehicleType) {
            while (true) {
                // If there is space for the vehicle, let it repair (increase condition variables).
                if (numParallelVehicles < maxParallelVehicles && numParallelVehicleTypes[vehicleType] < maxParallelVehicleTypes[vehicleType])
                {
                    numParallelVehicles++;
                    numParallelVehicleTypes[vehicleType]++;
                    break;
                }
                // Otherwise call wait to wait for another process to finish (Other process calls notify).
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Finish repair session (decrese condition variables).
        public synchronized void finishRepair(int vehicleType) {
            numParallelVehicles--;
            numParallelVehicleTypes[vehicleType]--;
            notifyAll();
        }
    }
}