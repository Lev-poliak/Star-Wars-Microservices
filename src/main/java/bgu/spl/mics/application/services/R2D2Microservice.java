package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import static java.lang.Thread.sleep;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {

    long sleepDuration;
    private Diary diary;

    public R2D2Microservice(long duration) {
        super("R2D2");
        sleepDuration = duration;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        messageBus.register(this);
        subscribeEvent(DeactivationEvent.class, deactivationEvent -> {
            //TODO: add ewoks
            try {
                sleep(sleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            diary.setR2d2Deactivate(System.currentTimeMillis());
            complete(deactivationEvent, true);
        });
        subscribeBroadcast(TerminateBroadcast.class, c -> {
            terminate();
            messageBus.unregister(this);
            diary.setTerminateTime(this,System.currentTimeMillis());
        });
    }
}
