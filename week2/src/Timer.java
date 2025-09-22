public class Timer implements Runnable{
    private volatile int time = 0;
    public int getTime(){
        return time;
    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            time++;
        }
    }
    public void restart(){
        time = 0;
    }
}
