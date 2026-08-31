
public class PetService {

    private static final int STROKE_MOOD_AMOUNT = 10;


	public void gainExperience(Pet pet,int amount) {

     if (amount <= 0) {
             return;
         }

    int newExp = pet.getExp() + amount;
   	pet.setExp(newExp);

	while (pet.getExp() >= pet.getLevel() * 100) {
	    int requiredExp = pet.getLevel() * 100;
		pet.setExp(pet.getExp() - requiredExp);
        pet.setLevel(pet.getLevel() + 1);
	}
	}
	public void feed(Pet pet, int amount){

	}

}
