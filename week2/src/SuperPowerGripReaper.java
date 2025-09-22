import java.util.Scanner;

public class SuperPowerGripReaper extends GripReaper{
    public SuperPowerGripReaper(String name, int hp, int energy, int damage,Scanner scan) {
        super(name, hp, energy, damage,scan);
        setHasHidden(true);
    }
    @Override
    public int hidden(){
        System.out.println("히든 필살기 발동!! 다음 무기 중 하나를 선택하세요.");
        System.out.println("1. 에너지 건");
        System.out.println("2. 무딘 칼");
        System.out.println("3. 레이저 건");
        System.out.print("번호 입력 >> ");
        int choice  = scan.nextInt();
        if(choice==1)return 7;
        else if(choice==2) return 4;
        else return 9;
    }
}
