package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LandoMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private AtomicInteger totalAttacks ;
    private long hanSoloFinish;
    private long c3poFinish;
    private long r2d2Deactivate;
    private HashMap<MicroService,Long> terminateTime;
    static private Diary instance = new Diary();

    private Diary(){
        totalAttacks = new AtomicInteger(0);
        terminateTime = new HashMap<>();
    }
    public static Diary getInstance(){ return Diary.instance; }


    public void addTotalAttack() {
        int val;
        do {
            val = totalAttacks.get();
        }while (!totalAttacks.compareAndSet(val, val + 1));
    }
    public void setTerminateTime(MicroService m,Long finish){ terminateTime.put(m,finish); }

    public long getTerminateTime(MicroService m){return terminateTime.get(m);}

    public int getTotalAttack(){ return totalAttacks.get();}

    public void setHanSoloFinish(long finish){
        hanSoloFinish = finish;
    }

    public long getHanSoloFinish(){return hanSoloFinish;}

    public void setC3poFinish (long finish){
        c3poFinish = finish;
    }

    public long getC3poFinish(){return c3poFinish;}

    public void setR2d2Deactivate(long finish){
        r2d2Deactivate = finish;
    }

    public long getR2d2Deactivate(){return r2d2Deactivate;}


    public long getHanSoloTerminate() {
        for (MicroService microService: terminateTime.keySet()){
            if (microService.getClass() == HanSoloMicroservice.class){
                return terminateTime.get(microService);
            }
        }
        return 0;
    }

    public long getC3POTerminate() {
        for (MicroService microService: terminateTime.keySet()){
            if (microService.getClass() == C3POMicroservice.class){
                return terminateTime.get(microService);
            }
        }
        return 0;
    }

    public long getLandoTerminate() {
        for (MicroService microService: terminateTime.keySet()){
            if (microService.getClass() == LandoMicroservice.class){
                return terminateTime.get(microService);
            }
        }
        return 0;
    }

    public long getR2D2Terminate() {
        for (MicroService microService: terminateTime.keySet()){
            if (microService.getClass() == R2D2Microservice.class){
                return terminateTime.get(microService);
            }
        }
        return 0;
    }

    public void resetNumberAttacks() {
        int val;
        do {
            val = totalAttacks.get();
        }while (!totalAttacks.compareAndSet(val, 0));
    }
}
