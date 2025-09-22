import java.util.Scanner;

public class Play {
    ActionState attack = ActionState.ATTACK;
    ActionState defense = ActionState.DEFENSE;
    ActionState hiddenSkill = ActionState.HIDDEN_SKILL;
    ActionState specialSkill = ActionState.SPECIAL_SKILL;
    ActionState chargeEnergy = ActionState.CHARGE_ENERGY;


    private int setComputerChoice(Character com){
        if(com.getEnergy()>0){
            return (int)(Math.random() * 3) + 1;
        }
        else return (int)(Math.random() * 2) + 2;
    }
    Scanner scan = new Scanner(System.in);
    Contents con = new Contents();
    public void start(){
        con.startContents();
        System.out.println("플레이어 이름을 입력하세요:");
        System.out.print("이름: ");
        String playerName = scan.nextLine();
        System.out.println();
        System.out.println("캐릭터를 선택하세요 (1~3):");
        System.out.print("번호 입력: ");
        // 1 - 기본캐릭터 2 - GripReaper 3 - SuperPowerGripReaper
        int character = scan.nextInt();
        scan.nextLine();
        System.out.println();
        System.out.println("난이도를 선택하세요 (1~3):");
        System.out.print("번호 입력: ");
        //난이도
        int difficult = scan.nextInt();
        scan.nextLine();
        System.out.println();
        //시작 세팅
        Character player,computer;
        if(character == 1){
            player = new BasicCharacter(playerName,10,1,1);
        }
        else if(character ==2){
            player = new GripReaper(playerName,10,1,1);
        }
        else player = new SuperPowerGripReaper(playerName,10,1,1);
        computer = new BasicCharacter("computer(AI)",10,1, difficult);
        int round = 0;
        int playerEnergy;
        int playerHp;
        int computerEnergy;
        int computerHp;
        int choice;
        int playerState;
        int computerState;

        while(true){
            round++;
            playerEnergy = player.getEnergy();
            playerHp = player.getHp();
            computerEnergy = computer.getEnergy();
            computerHp = computer.getHp();
            if(computer.getHp()<=0){
                System.out.println(playerName +" 승리!!!");
                break;
            }
            if(player.getHp()<=0){
                System.out.println("computer(AI)"+" 승리!!!");
                break;
            }
            System.out.println("==============================================");
            System.out.println(round + " 라운드 전투 시작!");
            System.out.println("----------------------------------------------");
            System.out.println("플레이어: " + player.getName() +
                    " | HP: " + playerHp +
                    " | Energy: " + playerEnergy);
            System.out.println("컴퓨터  : " + computer.getName() +
                    " | HP: " + computerHp +
                    " | Energy: " + computerEnergy);
            System.out.println("----------------------------------------------");
            System.out.println("• 매 라운드 선택: [1] 공격  [2] 방어  [3] 에너지 모으기  [4] 필살기  [5] 히든 필살기(슈퍼 저승사자만)");
            System.out.println("==============================================");
            computer.setState(setComputerChoice(computer));
            System.out.println("Computer 선택 완료!");
            while(true){
                System.out.print("다음 행동 선택 >>");
                choice = scan.nextInt();
                scan.nextLine();
                if(choice==hiddenSkill.getCode()&&!player.hasHidden()){
                    System.out.println("해당 캐릭터는 히든 필살기가 없습니다!");
                    continue;
                }
                if(choice==attack.getCode()&&player.getEnergy()<1){
                    System.out.println("에너지가 부족합니다! 기를 모으세요.");
                    continue;
                }
                break;
            }
            player.setState(choice);
            computerState = computer.getState();
            playerState = player.getState();
            System.out.println("==============================================");
            System.out.println(round + " 라운드 전투 결과");
            System.out.println("플레이어 선택: [" + playerState + "] " + ActionState.getDescriptionByCode(playerState));
            System.out.println("컴퓨터   선택: [" + computerState + "] " + ActionState.getDescriptionByCode(computerState));
            System.out.println("==============================================");

            if(computerState==attack.getCode()){// 컴퓨터 공격 경우
                if(playerState!=defense.getCode())player.setHp(playerHp-computer.getDamage());
                computer.useEnergy();
            }
            else if(computerState==chargeEnergy.getCode()){ // 컴퓨터 에너지 충전 경우
                computer.chargeEnergy();
            }
            if(playerState==attack.getCode()){//플레이어 공격
                if(computerState!=defense.getCode())computer.setHp(computerHp-player.getDamage());
                player.useEnergy();
            }
            else if(playerState==chargeEnergy.getCode()){
                player.chargeEnergy();
            }
            else if(playerState==specialSkill.getCode()){
                if(computerState!=defense.getCode())computer.setHp(computerHp-player.useSkill());
            }
            else if(playerState ==hiddenSkill.getCode()){
                if(computerState!=defense.getCode())computer.setHp(computerHp-player.hidden());
            }


        }

    }
}
