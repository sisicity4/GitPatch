public class Main {

	public static void main(String[] args) {
		if (args.length > 0 && "--test".equals(args[0])) {
			testPetService();
			testRepositoryService();
			testGitActivityService();

			System.out.println("すべてのテストが成功しました。");
			return;
		}

		Pet pet = new Pet();
		PetService petService = new PetService();
		Menu menu = new Menu(pet, petService);
		menu.start();
	}

	private static void testRepositoryService() {
		RepositoryService repositoryService = new RepositoryService();

		boolean added = repositoryService.add(
				"Gitぱっち本体",
				"/Users/example/GitPatch");

		if (!added) {
			throw new AssertionError("リポジトリを登録できませんでした。");
		}

		checkInt(
				"登録後のリポジトリ数",
				1,
				repositoryService.findAll().size());

		RepositoryProfile repository = repositoryService.findAll().get(0);
		checkString(
				"登録したリポジトリ名",
				"Gitぱっち本体",
				repository.getRepoName());
		checkString(
				"登録したリポジトリのパス",
				"/Users/example/GitPatch",
				repository.getPath());

		checkFalse(
				"同じパスの重複登録",
				repositoryService.add("同じパスの登録", "/Users/example/GitPatch"));
		checkFalse(
				"空のリポジトリ名の登録",
				repositoryService.add("", "/Users/example/empty-name"));
		checkFalse(
				"空のパスの登録",
				repositoryService.add("空のパス", ""));

		checkTrue(
				"2件目のリポジトリ登録",
				repositoryService.add("練習用リポジトリ", "/Users/example/PracticeRepository"));
		checkInt(
				"2件目を登録した後のリポジトリ数",
				2,
				repositoryService.findAll().size());

		checkTrue(
				"1件目のリポジトリ更新",
				repositoryService.update(
						0,
						"Gitぱっち更新後",
						"/Users/example/GitPatchUpdated"));

		RepositoryProfile updatedRepository = repositoryService.findAll().get(0);
		checkString(
				"更新後のリポジトリ名",
				"Gitぱっち更新後",
				updatedRepository.getRepoName());
		checkString(
				"更新後のリポジトリのパス",
				"/Users/example/GitPatchUpdated",
				updatedRepository.getPath());

		checkFalse(
				"重複したパスへの更新",
				repositoryService.update(
						0,
						"重複する名前",
						"/Users/example/PracticeRepository"));

		RepositoryProfile repositoryAfterFailedUpdate = repositoryService.findAll().get(0);
		checkString(
				"失敗した更新後も名前が変わらない",
				"Gitぱっち更新後",
				repositoryAfterFailedUpdate.getRepoName());
		checkString(
				"失敗した更新後もパスが変わらない",
				"/Users/example/GitPatchUpdated",
				repositoryAfterFailedUpdate.getPath());

		checkFalse(
				"負の番号での更新",
				repositoryService.update(-1, "不正な番号", "/Users/example/Invalid"));
		checkFalse(
				"一覧外の番号での更新",
				repositoryService.update(
						repositoryService.findAll().size(),
						"不正な番号",
						"/Users/example/Invalid"));

		checkTrue("2件目のリポジトリ削除", repositoryService.delete(1));
		checkInt(
				"1件削除した後のリポジトリ数",
				1,
				repositoryService.findAll().size());
		checkTrue("1件目のリポジトリ削除", repositoryService.delete(0));
		checkInt(
				"すべて削除した後のリポジトリ数",
				0,
				repositoryService.findAll().size());
		checkFalse("空の一覧からの削除", repositoryService.delete(0));
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

	private static void checkString(String testName, String expected, String actual) {
		if (!expected.equals(actual)) {
			throw new AssertionError(testName + "：期待値=" + expected + "、実際=" + actual);
		}

		System.out.println("OK: " + testName + "（" + actual + "）");
	}

	private static void checkTrue(String testName, boolean actual) {
		if (!actual) {
			throw new AssertionError(testName + "：成功するはずでした。");
		}

		System.out.println("OK: " + testName);
	}

	private static void checkFalse(String testName, boolean actual) {
		if (actual) {
			throw new AssertionError(testName + "：失敗するはずでした。");
		}

		System.out.println("OK: " + testName);
	}

	private static void testGitActivityService() {
		GitActivityService gitActivityService = new GitActivityService();

		checkTrue(
				"GitPatch自身をGitリポジトリとして判定",
				gitActivityService.isGitRepository("."));

		checkFalse(
				"存在しないパスをGitリポジトリと判定しない",
				gitActivityService.isGitRepository("/tmp/path-that-does-not-exist"));
	}

}
