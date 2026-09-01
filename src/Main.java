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
		
		if (args.length > 0 && "--test".equals(args[0])) {
		    testRepositoryService();
		    System.out.println("repositoryServiceのテストが成功しました。");
		    return;
		}

	}

	private static void testRepositoryService() {
	    RepositoryService repositoryService = new RepositoryService();

	    boolean added = repositoryService.add(
	            "Gitぱっち本体",
	            "/Users/example/GitPatch"
	    );

	    if (!added) {
	        throw new AssertionError("リポジトリを登録できませんでした。");
	    }

	    checkInt(
	            "登録後のリポジトリ数",
	            1,
	            repositoryService.findAll().size()
	    );
	    
	    private static void checkString(String testName, String expected, String actual) {
	        if (!expected.equals(actual)) {
	            throw new AssertionError(
	                    testName + "：期待値=" + expected + "、実際=" + actual
	            );
	        }

	        System.out.println("OK: " + testName + "（" + actual + "）");
	    }


	    RepositoryProfile repository = repositoryService.findAll().get(0);

	    checkString(
	            "登録したリポジトリ名",
	            "Gitぱっち本体",
	            repository.getRepoName()
	    );

	    checkString(
	            "登録したリポジトリのパス",
	            "/Users/example/GitPatch",
	            repository.getPath()
	    );

	    boolean duplicateAdded = repositoryService.add(
	            "同じパスの登録",
	            "/Users/example/GitPatch"
	    );

	    if (duplicateAdded) {
	        throw new AssertionError("同じパスを重複登録できてしまいました。");
	    }

	    boolean emptyNameAdded = repositoryService.add(
	            "",
	            "/Users/example/empty-name"
	    );

	    if (emptyNameAdded) {
	        throw new AssertionError("空のリポジトリ名を登録できてしまいました。");
	    }

	    boolean emptyPathAdded = repositoryService.add(
	            "空のパス",
	            ""
	    );

	    if (emptyPathAdded) {
	        throw new AssertionError("空のパスを登録できてしまいました。");
	    }

	    System.out.println("OK: RepositoryServiceの登録・重複・空入力チェック");
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
