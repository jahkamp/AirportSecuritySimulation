/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package airportsecuritysimulation;

/**
 *
 * Inlab 6 Jared Kamp
 *
 */
import java.util.Random;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;

public class AirportSecuritySimulation {
    static int maxPassengers = 4;
    static int simulationTime = 500;
    static int numQueues = 3; // initial value
    static Random randomGenerator;
    static int passengersProcessed;
    static int passengersQueued;
    
    public static int doSimulation() {

        // print queue:
        LinkedList<PassengerParty> listOfQueues[] = new LinkedList[numQueues];
        int[] finalCountPassengersPerQueue = new int [numQueues];
        double[] avePassengersPerQueue = new double [numQueues];
        int[] maxPassengersPerQueue = new int [numQueues];
        // list of jobs:
        ArrayList<PassengerParty> passengerList = new ArrayList<PassengerParty>();

        for (int i = 0; i < numQueues; i++) {            
            listOfQueues[i] = new LinkedList<PassengerParty>();
        }

        // main simulation loop:
        int[] nextFreeTime = new int[numQueues];
        for (int i = 0; i < numQueues; i++) {
            finalCountPassengersPerQueue[i] = 0;            
            avePassengersPerQueue[i] = 0;
            maxPassengersPerQueue[i] = 0;
            
            nextFreeTime[i] = 0;
        }
        boolean done = false;
        int t = 1;
        while (!done) {
            // check to add new parties to the queue:
            if (t < simulationTime) {
                int newPassengers = randomGenerator.nextInt(maxPassengers) + 1;
                PassengerParty newParty = new PassengerParty(newPassengers);
                newParty.arrivalTime = t;
//              find the shortest queue
                int shortestQ = 0;
                int shortQLength = 10000;                                
                for (int i = 0; i < numQueues; i++) {                    
                    if (shortQLength > finalCountPassengersPerQueue[i]) {
                        shortestQ = i;
                        shortQLength = finalCountPassengersPerQueue[i];
                    }                    
                }
                listOfQueues[shortestQ].offer(newParty);
                finalCountPassengersPerQueue[shortestQ] += newPassengers;
                avePassengersPerQueue[shortestQ] += newPassengers;
                passengersQueued += newPassengers;
                if(maxPassengersPerQueue[shortestQ] < finalCountPassengersPerQueue[shortestQ])
                        maxPassengersPerQueue[shortestQ] = finalCountPassengersPerQueue[shortestQ];

                passengerList.add(newParty);   
                System.out.println("Minute " + t + ": " + newPassengers + " passengers entered Q" + (shortestQ + 1));               
            }
            done = (t > simulationTime);
            if (t % 2 == 0 && t < simulationTime) {
                int randQ = randomGenerator.nextInt(numQueues);
                if (!listOfQueues[randQ].isEmpty()) {
                    // send the passenger party through security                    
                    PassengerParty nextParty = listOfQueues[randQ].poll();
                    nextParty.finishTime = t;
                    
                    finalCountPassengersPerQueue[randQ] -= nextParty.numPassengers;
                    passengersProcessed += nextParty.numPassengers;                                    
                    System.out.println("A party of " + nextParty.numPassengers + " from Q" + (randQ + 1) + " proceeded through security.");                                   
                    // check to see if each listOfQueues is empty
                    done = done && listOfQueues[randQ].isEmpty();
                }
            }
            t++;
        }

        // simulation done, look at statistics:
        int longestWaitTime = 0;
        PassengerParty longestJob = null;
        Iterator<PassengerParty> jobIter = passengerList.iterator();

        double averageWaitTime = 0.0;
        while (jobIter.hasNext()) {
            PassengerParty nextJob = jobIter.next();
            int waitTime =  nextJob.finishTime - nextJob.arrivalTime;    
            if(waitTime > 0)
                averageWaitTime = averageWaitTime + waitTime;
            if (waitTime > longestWaitTime) {
                longestWaitTime = waitTime;
                longestJob = nextJob;
            }
        }        

        System.out.println(
                "\nTotal number of passengers queued: " + passengersQueued);
        System.out.println(
                "Total number of passengers through security: " + passengersProcessed);
//        System.out.println(
//                "passengers still in queue 1: " + finalCountPassengersPerQueue[0]);
//        System.out.println(
//                "passengers still in queue 2: " + finalCountPassengersPerQueue[1]);
//        System.out.println(
//                "passengers still in queue 3: " + finalCountPassengersPerQueue[2]);
        System.out.println(
                "Max passengers in queue 1: " + maxPassengersPerQueue[0]);
        System.out.println(
                "Max passengers in queue 2: " + maxPassengersPerQueue[1]);
        System.out.println(
                "Max passengers in queue 3: " + maxPassengersPerQueue[2]);
        System.out.println(
                "Average passengers in queue 1: " + avePassengersPerQueue[0]/simulationTime);
        System.out.println(
                "Average passengers in queue 2: " + avePassengersPerQueue[1]/simulationTime);
        System.out.println(
                "Average passengers in queue 3: " + avePassengersPerQueue[2]/simulationTime);
        System.out.println(
                "Longest wait time = " + longestWaitTime);
        System.out.println(
                "Average wait time = " + averageWaitTime/passengersQueued);

        return longestWaitTime;
    }

    public static void main(String[] args) {
        randomGenerator = new Random();
        doSimulation();
        //System.out.println("The system recommends you open " + numQueues + " more security lines.");
    }
}
