package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a forest creature summoned when HanSolo and C3PO receive AttackEvents.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Ewok {
	int serialNumber;
	boolean available;

	Ewok(int serialNumber){
	    this.serialNumber = serialNumber;
	    available = true;
    }
	
    /**
     * Acquires an Ewok
     */
    // TODO: check if we can add exceptions throw
    public void acquire() throws IllegalAccessException {
        if (!available){
            throw new IllegalAccessException("Trying to acquire an unavailable ewok");
        }
		available = false;
    }

    /**
     * release an Ewok
     */
    public void release() throws IllegalAccessException {
        synchronized(this) {
            if (available) {
                throw new IllegalAccessException("released an Ewok that was already available");
            }
            available = true;
            notifyAll();
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void waitUntilAvailable() {
        synchronized(this) {
            while (!available) {
                try {
                    wait();
                } catch (InterruptedException ignore) {}
            }
        }
    }

    public int getSerialNumber(){
        return serialNumber;
    }
}
