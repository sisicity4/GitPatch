public class Main {

	public static void main(String[] args) {
		if (args.length > 0 && "--test".equals(args[0])) {
			testPetService();
			System.out.println("PetServiceのテストがすべて成功しました。");
			return;
		}

		Pet pet = new Pet();
		PetService petService = new PetService();
		Menu menu = new Menu(pet, petService);
		menu.start();
	}

	private static void testPetService() {
		Pet pet = new Pet();
		PetService petService = new PetService();

		petService.gainExperience(pet, 80);
		checkInt("80経験値を得た後のレベル", 1, pet.getLevel());
		checkInt("80経験値を得た後の経験値", 80, pet.getExp());

		petService.gainExperience(pet, 30);
		checkInt("110経験値になった後のレベル", 2, pet.getLevel());
		checkInt("レベルアップ後に残る経験値", 10, pet.getExp());

		petService.feed(pet, 200);
		checkInt("満腹度の上限", 100, pet.getHunger());

		petService.gainExperience(pet, -10);
		checkInt("負の経験値を渡した後の経験値", 10, pet.getExp());
	}

	private static void checkInt(String testName, int expected, int actual) {
		if (expected != actual) {
			throw new AssertionError(testName + "：期待値=" + expected + "、実際=" + actual);
		}

		System.out.println("OK: " + testName + "（" + actual + "）");
	}
}
