package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    static Vector<Ewok> ewokList;
    private static final Ewoks instance = new Ewoks();

    private Ewoks() {
        ewokList = new Vector<>();
    }

    public static Ewoks getInstance(int numOfEwoks) {
        ewokList.add(new Ewok(0)); //add ewok with serial number 0 for better readability
        for (int i = 0; i < numOfEwoks; i++){
            ewokList.add(new Ewok(i));
        }
        return Ewoks.instance;
    }

    public static Ewoks getInstance() {
        return Ewoks.instance;
    }

    public synchronized void acquireOrWait(List<Integer> serialNumbers){
        for(int i = 0; i < serialNumbers.size(); i++){
            Ewok currentEwok = ewokList.get(serialNumbers.get(i));
            if (!currentEwok.isAvailable()){
                currentEwok.waitUntilAvailable();
                i = 0;
            }
        }
        for(int serialNumber: serialNumbers){
            try {
                ewokList.get(serialNumber).acquire();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void release(List<Integer> serialNumbers) {
        for (int serialNumber: serialNumbers){
            Ewok currentEwok = ewokList.get(serialNumber);
            try {
                currentEwok.release();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

//    private void release(int serialNumber) throws IllegalAccessException {
//        Ewok ewok = ewokList.get(serialNumber);
//        synchronized(ewokList.get(serialNumber)) {
//            if (ewok.isAvailable()) {
//                throw new IllegalAccessException("released an Ewok that was already available");
//            }
//            ewok.release();
//            notify();
//        }
//    }
//
//    private void waitUntilAvailable(int serialNumber) {
//        Ewok ewok = ewokList.get(serialNumber);
//        synchronized(ewokList.get(serialNumber)){
//            while(!ewok.isAvailable()){
//                try {
//                    wait();
//                } catch (InterruptedException ignore) {}
//            }
//        }
//    }

}
