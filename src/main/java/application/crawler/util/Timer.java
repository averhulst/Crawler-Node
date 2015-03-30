package application.crawler.util;

public class Timer {
    private long startTime = 0;
    private long stopTime = 0;

    public void start(){
        startTime = System.currentTimeMillis();
    }

    public void stop(){
        stopTime = System.currentTimeMillis();
    }

    public int getElapsedTime(){
        return (int)(stopTime - startTime);
    }
    public int getStartTime(){
        return (int)startTime;
    }
    public int getEndTime(){
        return (int)startTime;
    }
}
