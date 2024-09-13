import java.util.Date;

public class Task implements Runnable{

    

    public void run(){
        System.out.println(new Date());
    }
}