package bgu.spl.mics.application.services;

import java.util.Vector;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import static java.lang.Thread.sleep;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
    private Attack[] attacks;
	private Vector<Future<Boolean>> attackEventsFutures;
    private Diary diary;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
        this.attackEventsFutures = new Vector<>();
        this.diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        messageBus.register(this);
        subscribeEvent(StartEvent.class, startEvent -> {
            for (Attack attack: attacks){
                AttackEvent attackevent = new AttackEvent(attack);
                attackEventsFutures.add(sendEvent(attackevent));
            }
            //TODO: add Diary
            for (Future<Boolean> future: attackEventsFutures){
                future.get();
            }
            sendEvent(new DeactivationEvent()).get();
            sendEvent(new BombDestroyerEvent()).get();
            sendBroadcast(new TerminateBroadcast());
        });
        subscribeBroadcast(TerminateBroadcast.class,c-> {
            terminate();
            messageBus.unregister(this);
            diary.setTerminateTime(this, System.currentTimeMillis());
        });
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
