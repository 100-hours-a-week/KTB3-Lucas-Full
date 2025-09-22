public class GripReaper extends Character{

    public GripReaper(String name, int hp, int energy, int damage) {
        super(name, hp, energy, damage);
    }

    @Override
    public int useSkill(){ // 40%확률로 상대 즉결 처형 or 2 데미지
        if(Math.random()<0.4){
            System.out.println("*#*# 즉결 처형 발동...!");
            return 99999;
        }
        else {
            System.out.println("...즉결 처형 실패! (2의 데미지가 들어갑니다.)");
            return 2;
        }
    }
}
