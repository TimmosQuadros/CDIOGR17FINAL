package Observer;

import java.util.ArrayList;
import java.util.List;

/*
 * Author Timm Daniel Rasmussen.
 */

public class Subject {

	// Collection to hold the registered observers
	private List<Observer> observerCollection = new ArrayList<>();

	// Register an observer with the subject
	public void registerObserver(Observer observer) {
		this.observerCollection.add(observer);
	}

	// Unregister an observer from the subject
	public void unregisterObserver(Observer observer){
		this.observerCollection.remove(observer);
	}

	// Notify all observers with an objec
	public void notifyObservers(Object object) {
		for (Observer observer : observerCollection) {
			observer.notify(object);
		}
	}

	// Notify all observers without an object
	public void notifyObservers() {
		for (Observer observer : observerCollection) {
			observer.notify();
		}
	}

}
