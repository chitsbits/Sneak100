import java.awt.*;

// Framerate Class
class FrameRate { 
    
    String frameRate; //to display the frame rate to the screen
    long lastTimeCheck; //store the time of the last time the time was recorded
    long deltaTime; //to keep the elapsed time between current time and last time
    int frameCount; //used to count how many frame occurred in the elapsed time (fps)
    
    public FrameRate() { 
        lastTimeCheck = System.currentTimeMillis();
        frameCount=0;
        frameRate="0 fps";
    }
    
    public void update() { 
        long currentTime = System.currentTimeMillis();  //get the current time
        deltaTime += currentTime - lastTimeCheck; //add to the elapsed time
        lastTimeCheck = currentTime; //update the last time var
        frameCount++; // every time this method is called it is a new frame
        if (deltaTime>=1000) { //when a second has passed, update the string message
            frameRate = frameCount + " fps" ;
            frameCount=0; //reset the number of frames since last update
            deltaTime=0;  //reset the elapsed time     
        }
    }
    
    public void draw(Graphics g, int x, int y) {
     g.setColor(Color.RED);
        g.drawString(frameRate,x,y); //display the frameRate
    }
    
    
}