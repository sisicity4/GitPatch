public class Main {

	public static void main(String[] args) {

		Pet pet = new Pet();
		PetService petService = new PetService();
		GitActivityService gitActivityService = new GitActivityService();
		RepositoryService repositoryService = new RepositoryService();

		Menu menu = new Menu(
				pet,
				petService,
				gitActivityService,
				repositoryService);

		if (args.length > 0 && "--test".equals(args[0])) {
			System.out.println("=== 1. PetService ===");
			testPetService();

			System.out.println("\n=== 2. RepositoryService ===");
			testRepositoryService();

			System.out.println("\n=== 3. GitActivityService ===");
			testGitActivityService();

			System.out.println("\nすべてのテストが成功しました。");
			return;
		}
		menu.start();
	}

	private static void testRepositoryService() {
		RepositoryService repositoryService = new RepositoryService();

		checkRepositoryStatus(
				"1件目のリポジトリ登録",
				RepositoryStatus.SUCCESS,
				repositoryService.add(
						"Gitぱっち本体",
						"/Users/example/GitPatch"));

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

		checkRepositoryStatus(
				"同じパスの重複登録",
				RepositoryStatus.DUPLICATE_PATH,
				repositoryService.add("同じパスの登録", "/Users/example/GitPatch"));
		checkRepositoryStatus(
				"空のリポジトリ名の登録",
				RepositoryStatus.EMPTY_REPO_NAME,
				repositoryService.add("", "/Users/example/empty-name"));
		checkRepositoryStatus(
				"空のパスの登録",
				RepositoryStatus.EMPTY_PATH,
				repositoryService.add("空のパス", ""));

		checkRepositoryStatus(
				"2件目のリポジトリ登録",
				RepositoryStatus.SUCCESS,
				repositoryService.add("練習用リポジトリ", "/Users/example/PracticeRepository"));
		checkInt(
				"2件目を登録した後のリポジトリ数",
				2,
				repositoryService.findAll().size());

		checkRepositoryStatus(
				"1件目のリポジトリ更新",
				RepositoryStatus.SUCCESS,
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

		checkRepositoryStatus(
				"重複したパスへの更新",
				RepositoryStatus.DUPLICATE_PATH,
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

		checkRepositoryStatus(
				"負の番号での更新",
				RepositoryStatus.INDEX_OUT_OF_RANGE,
				repositoryService.update(-1, "不正な番号", "/Users/example/Invalid"));
		checkRepositoryStatus(
				"一覧外の番号での更新",
				RepositoryStatus.INDEX_OUT_OF_RANGE,
				repositoryService.update(
						repositoryService.findAll().size(),
						"不正な番号",
						"/Users/example/Invalid"));

		checkRepositoryStatus(
				"2件目のリポジトリ削除",
				RepositoryStatus.SUCCESS,
				repositoryService.delete(1));
		checkInt(
				"1件削除した後のリポジトリ数",
				1,
				repositoryService.findAll().size());
		checkRepositoryStatus(
				"1件目のリポジトリ削除",
				RepositoryStatus.SUCCESS,
				repositoryService.delete(0));
		checkInt(
				"すべて削除した後のリポジトリ数",
				0,
				repositoryService.findAll().size());
		checkRepositoryStatus(
				"空の一覧からの削除",
				RepositoryStatus.INDEX_OUT_OF_RANGE,
				repositoryService.delete(0));

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

		petService.feed(pet);
		checkInt("1回のごはんで満腹度が20増える", 70, pet.getHunger());

		petService.feed(pet);
		petService.feed(pet);
		petService.feed(pet);
		checkInt("ごはんを何回あげても満腹度は100が上限", 100, pet.getHunger());

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

	private static void checkRepositoryStatus(
			String testName,
			RepositoryStatus expected,
			RepositoryStatus actual) {
		if (expected != actual) {
			throw new AssertionError(
					testName + "：期待値=" + expected + "、実際=" + actual);
		}

		System.out.println("OK: " + testName + "（" + actual + "）");
	}

	private static void checkGitStatus(
			String testName,
			GitActivityService.GitStatus expected,
			GitActivityService.GitStatus actual) {
		if (expected != actual) {
			throw new AssertionError(
					testName + "：期待値=" + expected + "、実際=" + actual);
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

		checkGitStatus(
				"GitPatch自身のGitStatus",
				GitActivityService.GitStatus.SUCCESS,
				gitActivityService.getStatus("."));
		checkGitStatus(
				"空パスのGitStatus",
				GitActivityService.GitStatus.EMPTY_PATH,
				gitActivityService.getStatus(""));
		checkGitStatus(
				"存在しないパスのGitStatus",
				GitActivityService.GitStatus.PATH_NOT_FOUND,
				gitActivityService.getStatus("/tmp/path-that-does-not-exist"));
		checkGitStatus(
				"GitではないフォルダのGitStatus",
				GitActivityService.GitStatus.NOT_GIT_REPOSITORY,
				gitActivityService.getStatus("/tmp/gitpatch-not-a-repository"));

		RepositoryProfile repository = new RepositoryProfile(
				"テスト用リポジトリ",
				"/tmp/test-repository");

		checkTrue(
				"初回のコミットは新しいコミットと判定する",
				gitActivityService.isNewCommit(repository, "commit-001"));

		repository.setLastCheckedCommitId("commit-001");

		checkTrue(
				"GitPatch自身をGitリポジトリとして判定",
				gitActivityService.isGitRepository("."));

		checkFalse(
				"存在しないパスをGitリポジトリと判定しない",
				gitActivityService.isGitRepository("/tmp/path-that-does-not-exist"));

		checkFalse(
				"同じコミットIDは新しいコミットと判定しない",
				gitActivityService.isNewCommit(repository, "commit-001"));

		checkTrue(
				"異なるコミットIDは新しいコミットと判定する",
				gitActivityService.isNewCommit(repository, "commit-002"));

		String latestCommitId = gitActivityService.findLatestCommitId(".");

		if (latestCommitId == null || latestCommitId.trim().equals("")) {
			throw new AssertionError(
					"GitPatch自身のパスで最新コミットIDを取得できませんでした。");
		}

		System.out.println(
				"OK: GitPatch自身のパスでコミットIDを取得（"
						+ latestCommitId
						+ "）");

	}

}
