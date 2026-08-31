public class PetService {
    private static final int MAX_STATUS = 100;
    private static final int STROKE_MOOD_AMOUNT = 10;

    public void gainExperience(Pet pet, int amount) {
        if (amount <= 0) {
            return;
        }

        pet.setExp(pet.getExp() + amount);

        while (pet.getExp() >= pet.getLevel() * 100) {
            int requiredExp = pet.getLevel() * 100;
            pet.setExp(pet.getExp() - requiredExp);
            pet.setLevel(pet.getLevel() + 1);
        }
    }

    public void feed(Pet pet, int amount) {
        if (amount <= 0) {
            return;
        }

        pet.setHunger(Math.min(MAX_STATUS, pet.getHunger() + amount));
    }

    public void stroke(Pet pet) {
        pet.setMood(Math.min(MAX_STATUS, pet.getMood() + STROKE_MOOD_AMOUNT));
    }
}
