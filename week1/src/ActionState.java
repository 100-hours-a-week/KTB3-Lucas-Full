public enum ActionState {
    ATTACK(1, "공격"),
    DEFENSE(2, "방어"),
    CHARGE_ENERGY(3, "에너지 모으기"),
    SPECIAL_SKILL(4, "필살기"),
    HIDDEN_SKILL(5, "히든 필살기");

    private final int code;
    private final String description;

    ActionState(int code, String description) {
        this.code = code;
        this.description = description;
    }
    public static String getDescriptionByCode(int code) {
        for (ActionState action : ActionState.values()) {
            if (action.code == code) {
                return action.description;
            }
        }
        return "알 수 없음";
    }
    public int getCode() { return code; }
    public String getDescription() { return description; }
}