package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	static private MessageBusImpl instance = null;
	private HashMap<MicroService , BlockingQueue<Message>> mapOfQues;
	private ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> subscribedEventMap;
	private ConcurrentHashMap<Class<? extends Message>, LinkedList<MicroService>> subscribedBroadcastMap;
	private Object lockUnregister;

private HashMap<Event,Future> futureMap;
	private final Object LockEventSubscribe = new Object();
	private final Object LockBroadcastSubscribe = new Object();
		private static class MessageBusImplSingletonHolder {
			private static MessageBusImpl instance = new MessageBusImpl();
		}

		private MessageBusImpl() {
			mapOfQues = new HashMap<>();
			subscribedEventMap = new ConcurrentHashMap<>();
			subscribedBroadcastMap = new ConcurrentHashMap<>();
			futureMap = new HashMap<>();
			lockUnregister = new Object();
		}

		public static MessageBusImpl getInstance() {
			return MessageBusImplSingletonHolder.instance;
		}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
			if (!subscribedEventMap.containsKey(type)) {
				synchronized (LockEventSubscribe) {
					if (!subscribedEventMap.containsKey(type)) {
						subscribedEventMap.put(type, new LinkedList<>());
					}
				}
			}
			subscribedEventMap.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (LockBroadcastSubscribe) {
			if (!subscribedBroadcastMap.containsKey(type)) {
				subscribedBroadcastMap.put(type, new LinkedList<>());
			}
		}
		subscribedBroadcastMap.get(type).add(m);
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
	futureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		LinkedList broadcastNet = subscribedBroadcastMap.get(b.getClass());
		Iterator it = broadcastNet.listIterator();
		//TODO write in for
		while(it.hasNext()){
			mapOfQues.get(it.next()).add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future();
		futureMap.put(e,future);
		Class type = e.getClass();
		//TODO check if subscribed before send event
		if(!subscribedEventMap.contains(type))
			throw new  NullPointerException("no MicroService subscribed to sent event");
		synchronized (subscribedEventMap.get(type)){
			LinkedList<MicroService> tempQuePoint = subscribedEventMap.get(type);
			MicroService currMicroserviceToQue = tempQuePoint.poll();
			mapOfQues.get(currMicroserviceToQue).add(e);
			tempQuePoint.add(currMicroserviceToQue);
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
	mapOfQues.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		mapOfQues.remove(m);
		synchronized (lockUnregister) {
			for (Class<? extends Message> it:subscribedEventMap.keySet()) {
				subscribedEventMap.get(it).remove(m);
			}
			for (Class<? extends Message> it:subscribedBroadcastMap.keySet()) {
				subscribedBroadcastMap.get(it).remove(m);
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return mapOfQues.get(m).take();
	}
}