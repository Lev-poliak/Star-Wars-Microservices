package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.HashMap;

/**
 * The MicroService is an abstract class that any micro-service in the system
 * must extend. The abstract MicroService class is responsible to get and
 * manipulate the singleton {@link MessageBus} instance.
 * <p>
 * Derived classes of MicroService should never directly touch the message-bus.
 * Instead, they have a set of internal protected wrapping methods (e.g.,
 * {@link #sendBroadcast(bgu.spl.mics.Broadcast)}, {@link #sendBroadcast(bgu.spl.mics.Broadcast)},
 * etc.) they can use. When subscribing to message-types,
 * the derived class also supplies a {@link Callback} that should be called when
 * a message of the subscribed type was taken from the micro-service
 * message-queue (see {@link MessageBus#register(bgu.spl.mics.MicroService)}
 * method). The abstract MicroService stores this callback together with the
 * type of the message is related to.
 * 
 * Only private fields and methods may be added to this class.
 * <p>
 */
public abstract class MicroService implements Runnable {

    protected HashMap<Class<? extends Message>, Callback> callbacks;
    protected final String name;
    protected boolean isTerminated;
    protected final MessageBus messageBus;

    /**
     * @param name the micro-service name (used mainly for debugging purposes)
     */
    public MicroService(String name) {
    	callbacks = new HashMap<>();
    	this.name = name;
    	isTerminated = false;
    	messageBus = MessageBusImpl.getInstance();

    }

    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
    	messageBus.subscribeEvent(type, this);
    	callbacks.put(type, callback);
    }

    /**
     * Subscribes to broadcast message of type {@code type} with the callback
     * {@code callback}. This means two things:
     * 1. Subscribe to broadcast messages in the singleton event-bus 
     * 2. Store the  callback so that when broadcast messages received it will be called.
     * calling the callback means running the method
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The type of broadcast message to subscribe to.
     * @param callback The callback that should be called when messages 
     * are taken from this micro-service message queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        messageBus.subscribeBroadcast(type, this);
        callbacks.put(type, callback);
    }

    protected final <T> Future<T> sendEvent(Event<T> e) {
        return messageBus.sendEvent(e);
    }

    /**
     * send the broadcast message to all the services subscribed to it.
     */
    protected final void sendBroadcast(Broadcast b) {
        messageBus.sendBroadcast(b);
    }

    /**
     * Completes event using messegebus
     */
    protected final <T> void complete(Event<T> e, T result) {
    	messageBus.complete(e, result);
    }

    /**
     * this method is called once when the event loop starts.
     */
    protected abstract void initialize();

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     */
    protected final void terminate() {
    	isTerminated = true;
    }

    public final String getName() {
        return name;
    }

    /**
     * The entry point of the micro-service. 
     */
    @Override
    public final void run() {
    	initialize();
    	while(!isTerminated){
            Message message;
            try {
                message = messageBus.awaitMessage(this);
                callbacks.get(message.getClass()).call(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
