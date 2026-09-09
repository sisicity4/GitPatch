import java.time.LocalDate;
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
  private final ActivityStreak activityStreak;
  private final ActivityStreakService activityStreakService;

  public Menu(
    Pet pet,
    PetService petService,
    GitActivityService gitActivityService,
    RepositoryService repositoryService,
    ActivityStreak activityStreak,
    ActivityStreakService activityStreakService
  ) {
    this(
      pet,
      petService,
      gitActivityService,
      repositoryService,
      activityStreak,
      activityStreakService,
      new Scanner(System.in)
    );
  }

  Menu(
    Pet pet,
    PetService petService,
    GitActivityService gitActivityService,
    RepositoryService repositoryService,
    ActivityStreak activityStreak,
    ActivityStreakService activityStreakService,
    Scanner scanner
  ) {
    this.scanner = scanner;
    this.pet = pet;
    this.petService = petService;
    this.gitActivityService = gitActivityService;
    this.repositoryService = repositoryService;
    this.activityStreak = activityStreak;
    this.activityStreakService = activityStreakService;
  }

  public void start() {
    boolean running = true;

    while (running) {
      showMenu();
      String input = normalizeNumberInput(scanner.nextLine());

      switch (input) {
        case "1" -> showPetStatus();
        case "2" -> careForPet();
        case "3" -> manageRepositories();
        case "4" -> checkGitActivity();
        case "0" -> {
          System.out.println("またね。ぱっちをよろしくね。");
          running = false;
        }
        default -> System.out.println("0〜4の番号を入力してください。");
      }
    }
  }

  private void showMenu() {
    System.out.println();
    System.out.println("=== Gitぱっち ===");
    System.out.println("1. ぱっちの様子を見る");
    System.out.println("2. お世話をする");
    System.out.println("3. リポジトリを管理する");
    System.out.println("4. Git活動を確認する");
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
    petService.feed(pet);
    System.out.println("ぱっちはごはんを食べた。満腹度が20回復した！");
    showPetStatus();
  }

  private void strokePet() {
    petService.stroke(pet);
    System.out.println("ぱっちをなでた。機嫌がよくなった！");
    showPetStatus();
  }

  private void showActivityStreak() {
    int current = activityStreak.getCurrentStreak();
    int longest = activityStreak.getLongestStreak();

    ActivityStreak.StreakName name = activityStreakService.determineStreakName(
      current
    );

    System.out.println("連続活動：" + current + "日");
    System.out.println("最長記録：" + longest + "日");
    System.out.println("称号：" + displayNameOf(name));
  }

  private void showActivityAchievement(
    int previousStreak,
    LocalDate previousActivityDate
  ) {
    System.out.println("今日の活動達成！");

    LocalDate currentActivityDate = activityStreak.getLastActivityDate();
    if (
      previousActivityDate != null &&
      currentActivityDate != null &&
      previousActivityDate.plusDays(1).isBefore(currentActivityDate)
    ) {
      System.out.println("今日はGitさわれなかったのかな? また1から頑張ろう！");
    }

    int currentStreak = activityStreak.getCurrentStreak();
    if (
      previousStreak < currentStreak &&
      (currentStreak == 3 || currentStreak == 7 || currentStreak == 14)
    ) {
      String displayName = displayNameOf(activityStreak.getStreakName());
      System.out.println(
        "すごい！" + currentStreak + "日連続でGit活動を達成した！"
      );
      System.out.println("称号「" + displayName + "」を獲得！");
    }
  }

  private String displayNameOf(ActivityStreak.StreakName name) {
    return switch (name) {
      case NONE -> "なし";
      case COMMIT_ROOKIE -> "コミットルーキー";
      case STREAK_KEEPER -> "ストリークキーパー";
      case GIT_DEVELOPER -> "Gitデベロッパー";
      case ACTIVE_CONTRIBUTOR -> "アクティブコントリビューター";
      case GIT_STAR -> "Gitスター";
    };
  }

  private void careForPet() {
    boolean caring = true;

    while (caring) {
      System.out.println();
      System.out.println("=== お世話 ===");
      System.out.println("1. ごはんをあげる");
      System.out.println("2. なでる");
      System.out.println("0. 戻る");
      System.out.print("番号を入力: ");

      String input = normalizeNumberInput(scanner.nextLine());
      switch (input) {
        case "1" -> feedPet();
        case "2" -> strokePet();
        case "0" -> caring = false;
        default -> System.out.println("0〜2の番号を入力してください。");
      }
    }
  }

  private void manageRepositories() {
    boolean managing = true;

    while (managing) {
      System.out.println();
      System.out.println("=== リポジトリ管理 ===");
      System.out.println("1. リポジトリを登録する");
      System.out.println("2. リポジトリ一覧を見る");
      System.out.println("3. リポジトリを編集する");
      System.out.println("4. リポジトリを削除する");
      System.out.println("0. 戻る");
      System.out.print("番号を入力: ");

      String input = normalizeNumberInput(scanner.nextLine());
      switch (input) {
        case "1" -> addRepository();
        case "2" -> showRepositories();
        case "3" -> editRepository();
        case "4" -> deleteRepository();
        case "0" -> managing = false;
        default -> System.out.println("0〜4の番号を入力してください。");
      }
    }
  }

  private void addRepository() {
    System.out.print("リポジトリ名を入力: ");
    String repoName = scanner.nextLine().trim();
    System.out.print("リポジトリのファイルパスを入力: ");
    String path = scanner.nextLine().trim();

    RepositoryStatus status = repositoryService.add(repoName, path);
    switch (status) {
      case SUCCESS -> System.out.println("リポジトリを登録しました。");
      case EMPTY_REPO_NAME -> System.out.println(
        "リポジトリ名を入力してください。"
      );
      case EMPTY_PATH -> System.out.println(
        "リポジトリのパスを入力してください。"
      );
      case DUPLICATE_PATH -> System.out.println(
        "このパスはすでに登録されています。"
      );
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
      System.out.println(i + 1 + ". " + repository.getRepoName());
      System.out.println("   " + repository.getPath());
    }
  }

  private void editRepository() {
    int index = selectRepositoryIndex();
    if (index < 0) {
      return;
    }

    RepositoryProfile repository = repositoryService.findAll().get(index);
    System.out.println("現在のリポジトリ名: " + repository.getRepoName());
    System.out.println("現在のパス: " + repository.getPath());

    System.out.print("新しいリポジトリ名を入力: ");
    String repoName = scanner.nextLine().trim();
    System.out.print("新しいリポジトリのファイルパスを入力: ");
    String path = scanner.nextLine().trim();

    RepositoryStatus status = repositoryService.update(index, repoName, path);
    switch (status) {
      case SUCCESS -> System.out.println("リポジトリを更新しました。");
      case EMPTY_REPO_NAME -> System.out.println(
        "リポジトリ名を入力してください。"
      );
      case EMPTY_PATH -> System.out.println(
        "リポジトリのパスを入力してください。"
      );
      case DUPLICATE_PATH -> System.out.println(
        "このパスはすでに登録されています。"
      );
      case INDEX_OUT_OF_RANGE -> System.out.println("編集できませんでした。");
    }
  }

  private void deleteRepository() {
    int index = selectRepositoryIndex();
    if (index < 0) {
      return;
    }

    RepositoryProfile repository = repositoryService.findAll().get(index);
    System.out.println("削除対象: " + repository.getRepoName());
    System.out.println("パス: " + repository.getPath());
    System.out.print("本当に削除しますか？ y / n: ");

    String confirmation = scanner.nextLine().trim();
    if (!confirmation.equalsIgnoreCase("y")) {
      System.out.println("リポジトリの削除を取り消しました。");
      return;
    }

    RepositoryStatus status = repositoryService.delete(index);
    switch (status) {
      case SUCCESS -> System.out.println("リポジトリを削除しました。");
      case INDEX_OUT_OF_RANGE -> System.out.println("削除できませんでした。");
      case EMPTY_REPO_NAME, EMPTY_PATH, DUPLICATE_PATH -> System.out.println(
        "削除できませんでした。"
      );
    }
  }

  private void checkGitActivity() {
    RepositoryProfile repository = selectRepository();
    if (repository == null) {
      showActivityStreak();
      return;
    }

    GitActivityService.CommitResult latestCommit =
      gitActivityService.findLatestCommit(repository.getPath());
    if (latestCommit.getStatus() != GitActivityService.GitStatus.SUCCESS) {
      showGitStatusMessage(latestCommit.getStatus());
      showActivityStreak();
      return;
    }

    String latestCommitId = latestCommit.getCommitId();
    if (!gitActivityService.isNewCommit(repository, latestCommitId)) {
      System.out.println("新しいGit活動はありません。");
      showActivityStreak();
      return;
    }

    int previousStreak = activityStreak.getCurrentStreak();
    LocalDate previousActivityDate = activityStreak.getLastActivityDate();
    petService.gainExperience(pet, 30);
    repository.setLastCheckedCommitId(latestCommitId);

    activityStreakService.updateStreak(activityStreak);
    System.out.println("新しいコミットを確認！経験値を30得た！");
    showActivityAchievement(previousStreak, previousActivityDate);
    showActivityStreak();
    showPetStatus();
  }

  private RepositoryProfile selectRepository() {
    int index = selectRepositoryIndex();
    if (index < 0) {
      return null;
    }
    return repositoryService.findAll().get(index);
  }

  private int selectRepositoryIndex() {
    List<RepositoryProfile> repositories = repositoryService.findAll();
    if (repositories.isEmpty()) {
      System.out.println("先にリポジトリを登録してください。");
      return -1;
    }

    showRepositories();
    System.out.print("対象のリポジトリ番号を入力（0で取消）: ");
    String input = normalizeNumberInput(scanner.nextLine());

    if (input.equals("0")) {
      System.out.println("操作を取り消しました。");
      return -1;
    }

    try {
      int index = Integer.parseInt(input) - 1;
      if (index < 0 || index >= repositories.size()) {
        System.out.println("一覧にある番号を入力してください。");
        return -1;
      }
      return index;
    } catch (NumberFormatException e) {
      System.out.println("数字を入力してください。");
      return -1;
    }
  }

  private void showGitStatusMessage(GitActivityService.GitStatus status) {
    switch (status) {
      case EMPTY_PATH -> System.out.println(
        "リポジトリのパスが空です。登録内容を確認してください。"
      );
      case PATH_NOT_FOUND -> System.out.println(
        "指定されたパスが見つかりません。登録内容を確認してください。"
      );
      case NOT_GIT_REPOSITORY -> System.out.println(
        "このフォルダはGitリポジトリではありません。"
      );
      case NO_COMMIT -> System.out.println(
        "まだコミットがありません。最初のコミット後に確認してください。"
      );
      case GIT_COMMAND_FAILED -> System.out.println(
        "Gitを実行できません。Gitの設定を確認してください。"
      );
      case INTERRUPTED -> System.out.println(
        "Git活動の確認が中断されました。もう一度試してください。"
      );
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
