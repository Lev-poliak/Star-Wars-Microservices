package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.List;

public class AttackEvent implements Event<Boolean> {
    private final Attack attack;

    public AttackEvent(Attack attack){
        this.attack = attack;
    }

    public int getSleepDuration(){
        return attack.getSleepDuration();
    }
    public List<Integer> getEwoks() { return attack.getEwoks(); }
}
