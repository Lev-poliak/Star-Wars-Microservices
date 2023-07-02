package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

import static java.lang.Thread.sleep;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvents}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvents}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

	private long finishLastAttack;
    private final Diary diary;
    public Ewoks ewoks;

    public C3POMicroservice() {
        super("C3PO");
        diary = Diary.getInstance();
        ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        messageBus.register(this);
        subscribeEvent(AttackEvent.class, attackEvent -> {
            int sleepDuration = attackEvent.getSleepDuration();
            List<Integer> ewoksList = attackEvent.getEwoks();
            ewoks.acquireOrWait(ewoksList);
            try {
                sleep(sleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ewoks.release(ewoksList);
            finishLastAttack = System.currentTimeMillis();
            diary.addTotalAttack();
            complete(attackEvent, true);
        });
        subscribeBroadcast(TerminateBroadcast.class, c -> {
            terminate();
            messageBus.unregister(this);
            diary.setC3poFinish(finishLastAttack);
            diary.setTerminateTime(this,System.currentTimeMillis());
        });
    }
}
