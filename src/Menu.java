import java.util.List;
import java.util.Scanner;

public class Menu {
	private String normalizeNumberInput(String input) {
		return input
				.replace('０', '0')
				.replace('１', '1')
				.replace('２', '2')
				.replace('３', '3')
				.replace('４', '4')
				.replace('５', '5')
				.replace('６', '6')
				.replace('７', '7')
				.replace('８', '8')
				.replace('９', '9')
				.trim();
	}

	private final Scanner scanner;
	private final Pet pet;
	private final PetService petService;
	private final GitActivityService gitActivityService;
	private final RepositoryService repositoryService;

	public Menu(
			Pet pet,
			PetService petService,
			GitActivityService gitActivityService,
			RepositoryService repositoryService) {
		scanner = new Scanner(System.in);
		this.pet = pet;
		this.petService = petService;
		this.gitActivityService = gitActivityService;
		this.repositoryService = repositoryService;
	}

	public void start() {
		boolean running = true;

		while (running) {
			showMenu();
			String input = normalizeNumberInput(scanner.nextLine());

			switch (input) {
			case "1" -> showPetStatus();
			case "2" -> feedPet();
			case "3" -> strokePet();
			case "4" -> checkGitActivity();
			case "5" -> addRepository();
			case "6" -> showRepositories();
			case "0" -> {
				System.out.println("またね。ぱっちをよろしくね。");
				running = false;
			}
			default -> System.out.println("0〜6の番号を入力してください。");
			}
		}
	}

	private void showMenu() {
		System.out.println();
		System.out.println("=== Gitぱっち ===");
		System.out.println("1. ぱっちの様子を見る");
		System.out.println("2. ごはんをあげる");
		System.out.println("3. なでる");
		System.out.println("4. Git活動を確認する");
		System.out.println("5. リポジトリを登録する");
		System.out.println("6. リポジトリ一覧を見る");
		System.out.println("0. 終了する");
		System.out.print("番号を入力: ");
	}

	private void showPetStatus() {
		System.out.println();
		System.out.println(pet.getName() + " " + faceOf(pet.getExpression()));
		System.out.println("レベル: " + pet.getLevel());
		System.out.println("経験値: " + pet.getExp());
		System.out.println("満腹度: " + pet.getHunger());
		System.out.println("機嫌: " + pet.getMood());
		System.out.println(statusMessageOf(pet.getExpression()));
	}

	private void feedPet() {
		System.out.print("ごはんで回復する量を入力: ");
		String input = normalizeNumberInput(scanner.nextLine());

		try {
			int amount = Integer.parseInt(input);
			if (amount <= 0) {
				System.out.println("1以上の数を入力してください。");
				return;
			}

			petService.feed(pet, amount);
			System.out.println("ぱっちはごはんを食べた。満腹度が上がった！");
			showPetStatus();
		} catch (NumberFormatException e) {
			System.out.println("数字を入力してください。");
		}
	}

	private void strokePet() {
		petService.stroke(pet);
		System.out.println("ぱっちをなでた。機嫌がよくなった！");
		showPetStatus();
	}

	private void addRepository() {
		System.out.print("リポジトリ名を入力: ");
		String repoName = scanner.nextLine().trim();
		System.out.print("リポジトリのファイルパスを入力: ");
		String path = scanner.nextLine().trim();

		RepositoryStatus status = repositoryService.add(repoName, path);
		switch (status) {
		case SUCCESS -> System.out.println("リポジトリを登録しました。");
		case EMPTY_REPO_NAME -> System.out.println("リポジトリ名を入力してください。");
		case EMPTY_PATH -> System.out.println("リポジトリのパスを入力してください。");
		case DUPLICATE_PATH -> System.out.println("このパスはすでに登録されています。");
		case INDEX_OUT_OF_RANGE -> System.out.println("登録できませんでした。");
		}
	}

	private void showRepositories() {
		List<RepositoryProfile> repositories = repositoryService.findAll();

		if (repositories.isEmpty()) {
			System.out.println("登録されているリポジトリはありません。");
			return;
		}

		System.out.println("=== 登録リポジトリ ===");
		for (int i = 0; i < repositories.size(); i++) {
			RepositoryProfile repository = repositories.get(i);
			System.out.println((i + 1) + ". " + repository.getRepoName());
			System.out.println("   " + repository.getPath());
		}
	}

	private void checkGitActivity() {
		RepositoryProfile repository = selectRepository();
		if (repository == null) {
			return;
		}

		GitActivityService.GitStatus status = gitActivityService.getStatus(repository.getPath());
		if (status != GitActivityService.GitStatus.SUCCESS) {
			showGitStatusMessage(status);
			return;
		}

		String latestCommitId = gitActivityService.findLatestCommitId(
				repository.getPath());
		if (!gitActivityService.isNewCommit(repository, latestCommitId)) {
			System.out.println("新しいGit活動はありません。");
			return;
		}

		petService.gainExperience(pet, 30);

		repository.setLastCheckedCommitId(latestCommitId);
		System.out.println("新しいコミットを確認！経験値を30得た！");
		showPetStatus();
	}

	private RepositoryProfile selectRepository() {
		List<RepositoryProfile> repositories = repositoryService.findAll();
		if (repositories.isEmpty()) {
			System.out.println("先にリポジトリを登録してください。");
			return null;
		}

		showRepositories();
		System.out.print("確認するリポジトリの番号を入力: ");
		String input = normalizeNumberInput(scanner.nextLine());


		try {
			int index = Integer.parseInt(input) - 1;
			if (index < 0 || index >= repositories.size()) {
				System.out.println("一覧にある番号を入力してください。");
				return null;
			}
			return repositories.get(index);
		} catch (NumberFormatException e) {
			System.out.println("数字を入力してください。");
			return null;
		}
	}

	private void showGitStatusMessage(GitActivityService.GitStatus status) {
		switch (status) {
		case EMPTY_PATH -> System.out.println("リポジトリのパスが空です。登録内容を確認してください。");
		case PATH_NOT_FOUND -> System.out.println("指定されたパスが見つかりません。登録内容を確認してください。");
		case NOT_GIT_REPOSITORY -> System.out.println("このフォルダはGitリポジトリではありません。");
		case NO_COMMIT -> System.out.println("まだコミットがありません。最初のコミット後に確認してください。");
		case GIT_COMMAND_FAILED -> System.out.println("Gitを実行できません。Gitの設定を確認してください。");
		case INTERRUPTED -> System.out.println("Git活動の確認が中断されました。もう一度試してください。");
		case SUCCESS -> System.out.println("Git活動を確認できました。");
		}
	}

	private String faceOf(Pet.Expression expression) {
		return switch (expression) {
		case HUNGRY -> "(´；ω；`)";
		case SAD -> "(｡•́︿•̀｡)";
		case HAPPY -> "(＾▽＾)";
		case NORMAL -> "(・ω・)";
		};
	}

	private String statusMessageOf(Pet.Expression expression) {
		return switch (expression) {
		case HUNGRY -> "おなかがすいているみたい。ごはんをあげよう。";
		case SAD -> "少し元気がないみたい。なでてあげよう。";
		case HAPPY -> "とてもごきげん！";
		case NORMAL -> "今日ものんびり過ごしている。";
		};
	}
}
