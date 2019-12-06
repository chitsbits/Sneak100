import java.awt.*;

// Clock class
class Clock {
    long elapsedTime;
    long deltaTime;
    long lastTimeCheck;
    
    public Clock() { 
        lastTimeCheck=System.nanoTime();
        elapsedTime = 0;
        deltaTime = 0;
    }
    
    public void update() {
        long currentTime = System.nanoTime();  //if the computer is fast you need more precision
        deltaTime = currentTime - lastTimeCheck;
        elapsedTime += deltaTime;
        lastTimeCheck = currentTime;
    }
    
    //return delta time in milliseconds
    public double getDeltaTime() {
        
        return deltaTime/1.0E9;
    }
    
    public double getElapsedTime() {
     return elapsedTime;
    }
}
