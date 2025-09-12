    public abstract class  Character {
        private String name;
        private int hp;
        private int energy;
        private int damage;
        private int state; //1-공격 2-방어 3-기모으기 4-필살기(에너지 3개사용) 5-히든 필살기(superman만 사용가능)
        private boolean hasHidden;
        public Character(String name, int hp, int energy, int damage) {
            this.name = name;
            this.hp = hp;
            this.energy = energy;
            this.damage = damage;


        }

        public void setHp(int hp){
            this.hp = hp;
        }

        public void setHasHidden(boolean hasHidden){
            this.hasHidden = hasHidden;
        }

        public void setState(int state){
            this.state = state;
        }

        public String getName() {
            return name;
        }

        public int getHp() {
            return hp;
        }

        public int getDamage(){
            return damage;
        }
        public int getEnergy(){
            return energy;
        }
        public boolean getHasHidden(){
            return hasHidden;
        }

        public void chargeEnergy(){
            energy++;
        }

        public int getState(){
            return state;
        }
        public abstract int useSkill(); //스킬 사용 이후 최종 데미지를 반환

        public int hidden() {
            System.out.println("해당 캐릭터는 히든 스킬이 없습니다.");
            return 0;                      // 데미지 0
        }
        public void useEnergy(){
            energy--;
        }

    }
