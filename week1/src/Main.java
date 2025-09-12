import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        Play game = new Play();
        while(true){
            game.start();
            System.out.println("다시하기 : 0, 종료 : 1 ");
            System.out.print(">>");
            if(scan.nextInt()!=0)break;
            scan.nextLine();
        }

    }
}