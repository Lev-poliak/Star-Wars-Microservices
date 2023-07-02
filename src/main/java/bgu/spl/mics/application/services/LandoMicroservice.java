package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import static java.lang.Thread.sleep;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {

    long sleepDuration;
    private Diary diary;

    public LandoMicroservice(long duration) {
        super("Lando");
        sleepDuration = duration;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        messageBus.register(this);
        subscribeEvent(BombDestroyerEvent.class, bombDestroyerEvent -> {
            //TODO: add ewoks
            try {
                sleep(sleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            complete(bombDestroyerEvent, true);
        });
        subscribeBroadcast(TerminateBroadcast.class, c -> {
            terminate();
            messageBus.unregister(this);
            diary.setTerminateTime(this,System.currentTimeMillis());
        });
    }
}
