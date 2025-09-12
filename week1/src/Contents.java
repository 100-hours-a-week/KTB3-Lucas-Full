public class Contents {

    public void startContents(){
        System.out.println("==============================================");
        System.out.println(" 콘솔 격투 게임에 오신 것을 환영합니다!");
        System.out.println("==============================================");
        System.out.println();
        System.out.println(" 게임 규칙 (간단 요약)");
        System.out.println("• 게임은 1라운드부터 끝날 때까지 진행됩니다.");
        System.out.println("• 매 라운드 선택: [1] 공격  [2] 방어  [3] 에너지 모으기  [4] 필살기  [5] 히든 필살기(특정 캐릭터만)");
        System.out.println("• 시작 능력치: HP = 10, Energy = 1");
        System.out.println("• 공격은 Energy를 1 소모하며, Energy가 없으면 공격할 수 없습니다!");
        System.out.println("• 기 모으기: Energy를 충전합니다.");
        System.out.println();
        System.out.println(" 캐릭터 유형");
        System.out.println("1) 기본 캐릭터        : 표준 능력치 (필살기 없음)");
        System.out.println("2) 저승사자     : 일정 확률로 즉결처형");
        System.out.println("3) 슈퍼 저승사자     : 일정 확률로 즉결처형 + 히든 필살기");
        System.out.println("   ※ 히든 필살기는 슈퍼 저승사자만 사용할 수 있습니다.");
        System.out.println();
        System.out.println("난이도 (컴퓨터 데미지)");
        System.out.println("1) 쉬움 : 1  |  2) 보통 : 2  |  3) 어려움 : 5");
        System.out.println();
        System.out.println("==============================================");
    }
}
