/**
 * Created with IntelliJ IDEA.
 * User: buttes
 * Date: 10/29/14
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
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
