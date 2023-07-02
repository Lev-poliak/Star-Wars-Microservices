package bgu.spl.mics.application;

import bgu.spl.mics.Input;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Output;
import bgu.spl.mics.application.messages.StartEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;

/** This is the Main class of the application. You should parse the input file,
import bgu.spl.mics.Input;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.Output;
import bgu.spl.mics.application.messages.StartEvent;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

	static Thread leiaThread;
	static Thread c3poThread;
	static Thread hansoloThread;
	static Thread landoThread;
	static Thread r2d2Thread;

	static LeiaMicroservice leia;
	static C3POMicroservice c3po;
	static HanSoloMicroservice hansolo;
	static LandoMicroservice lando;
	static R2D2Microservice r2d2;

	public static void main(String[] args) {
		Input input = getInput(args[0]);

		Ewoks.getInstance(input.getEwoks());

		createMicroservices(input);
		createThreads();
		startThreads();
		startSimulation();
		joinWithThreads();
		createOutput(args[1]);
	}

	static void createMicroservices(Input input){
		leia = new LeiaMicroservice(input.getAttacks());
		c3po = new C3POMicroservice();
		hansolo = new HanSoloMicroservice();
		lando = new LandoMicroservice(input.getLando());
		r2d2 = new R2D2Microservice(input.getR2D2());
	}

	static void createThreads(){
		leiaThread = new Thread(leia);
		c3poThread = new Thread(c3po);
		hansoloThread = new Thread(hansolo);
		landoThread = new Thread(lando);
		r2d2Thread = new Thread(r2d2);
	}

	static void startThreads(){
		leiaThread.start();
		c3poThread.start();
		hansoloThread.start();
		landoThread.start();
		r2d2Thread.start();
	}

	static void startSimulation(){
		StartEvent startEvent = new StartEvent();
		MessageBusImpl.getInstance().sendEvent(startEvent);
	}

	static void joinWithThreads(){
		try {
			leiaThread.join();
			c3poThread.join();
			hansoloThread.join();
			landoThread.join();
			r2d2Thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static Input getInput(String path){
		Input input = null;
		Gson gson = new Gson();
		try (Reader reader = new FileReader(path)){
			input = gson.fromJson(reader, Input.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}

	static void createOutput(String path){
		Output output = new Output();
		getOutputFromDiary(output);
		Gson gson = new Gson();
		try (Writer writer = new FileWriter(path)){
			gson.toJson(output, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void getOutputFromDiary(Output output){
		Diary diary = Diary.getInstance();
		output.setTotalAttacks(diary.getTotalAttack());
		output.setHanSoloFinish(diary.getHanSoloFinish());
		output.setC3poFinish(diary.getC3poFinish());
		output.setR2D2Deactivate(diary.getR2d2Deactivate());
		output.setLeiaTerminate(diary.getTerminateTime(leia));
		output.setHanSoloTerminate(diary.getTerminateTime(hansolo));
		output.setC3POTerminate(diary.getTerminateTime(c3po));
		output.setR2D2Terminate(diary.getTerminateTime(r2d2));
		output.setLandoTerminate(diary.getTerminateTime(lando));
	}

}
