import java.util.Scanner;

public class BasicCharacter extends Character{
    public BasicCharacter(String name, int hp, int energy, int damage, Scanner scan) {
        super(name, hp, energy, damage,scan);
    }
    @Override
    public int useSkill(){ //스킬 사용 이후 최종 데미지를 반환
        System.out.println("기본 캐릭터는 스킬을 사용할 수 없습니다. 스킬이 무방비 상태로 대체됩니다.");
        return 0;
    }

}
