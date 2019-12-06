
// Clock class that uses miliseconds instead of nano, for tasks that don't need precsion
public class Clock2 {

	 long lastTimeCheck; //store the time of the last time the time was recorded
	 long elapsedTime; //total elapsed time
	 
	 public Clock2() {
		 lastTimeCheck = System.currentTimeMillis();
	 }
	 
	 public void update() { 
	    long currentTime = System.currentTimeMillis();  //get the current time
	    elapsedTime += currentTime - lastTimeCheck; //add to the elapsed time
	    lastTimeCheck = currentTime; //update the last time var
	 }
	 
	 public double getElapsedTime() {
		 return elapsedTime;
	 }
}