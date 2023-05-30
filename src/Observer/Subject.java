package Observer;

import java.util.ArrayList;
import java.util.List;

public class Subject {
	
	private List<Observer> observerCollection = new ArrayList<>();
	
	public void registerObserver(Observer observer) {
		this.observerCollection.add(observer);
	}
	
	public void unregisterObserver(Observer observer){
		this.observerCollection.remove(observer);
	}
	
	public void notifyObservers(Object object) {
		for (Observer observer : observerCollection) {
			observer.notify(object);
		}
	}
	
	public void notifyObservers() {
		for (Observer observer : observerCollection) {
			observer.notify();
		}
	}

}
