import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        Play game = new Play();
        Timer timer = new Timer();
        Thread t = new Thread(timer);
        t.setDaemon(true); // 메인스레드가 죽으면 같이 죽도록 데몬스레드로 설정
        t.start();
        while(true){
            timer.restart();
            game.start(timer,scan);
            System.out.println("총 플레이 타임: "+timer.getTime()+"초");
            System.out.println("다시하기 : 0, 종료 : 1 ");
            System.out.print(">>");
            if(scan.nextInt()!=0)break;
            scan.nextLine();
        }

    }
}