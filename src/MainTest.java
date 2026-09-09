import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Scanner;

public class MainTest {

  public static void main(String[] args) {
    System.out.println("=== 1. PetService ===");
    testPetService();

    System.out.println("\n=== 2. RepositoryService ===");
    testRepositoryService();

    System.out.println("\n=== 3. GitActivityService ===");
    testGitActivityService();

    System.out.println("\n=== 4. ActivityStreak ===");
    testActivityStreak();

    System.out.println("\n=== 5. Menu GitActivity ===");
    testMenuGitActivityAlwaysShowsStreak();

    System.out.println("\nすべてのテストが成功しました。");
  }

  private static void testRepositoryService() {
    RepositoryService repositoryService = new RepositoryService();

    checkRepositoryStatus(
      "1件目のリポジトリ登録",
      RepositoryStatus.SUCCESS,
      repositoryService.add("Gitぱっち本体", "/Users/example/GitPatch")
    );

    checkInt("登録後のリポジトリ数", 1, repositoryService.findAll().size());

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

    checkRepositoryStatus(
      "同じパスの重複登録",
      RepositoryStatus.DUPLICATE_PATH,
      repositoryService.add("同じパスの登録", "/Users/example/GitPatch")
    );
    checkRepositoryStatus(
      "空のリポジトリ名の登録",
      RepositoryStatus.EMPTY_REPO_NAME,
      repositoryService.add("", "/Users/example/empty-name")
    );
    checkRepositoryStatus(
      "空のパスの登録",
      RepositoryStatus.EMPTY_PATH,
      repositoryService.add("空のパス", "")
    );

    checkRepositoryStatus(
      "2件目のリポジトリ登録",
      RepositoryStatus.SUCCESS,
      repositoryService.add(
        "練習用リポジトリ",
        "/Users/example/PracticeRepository"
      )
    );
    checkInt(
      "2件目を登録した後のリポジトリ数",
      2,
      repositoryService.findAll().size()
    );

    checkRepositoryStatus(
      "1件目のリポジトリ更新",
      RepositoryStatus.SUCCESS,
      repositoryService.update(
        0,
        "Gitぱっち更新後",
        "/Users/example/GitPatchUpdated"
      )
    );

    RepositoryProfile updatedRepository = repositoryService.findAll().get(0);
    checkString(
      "更新後のリポジトリ名",
      "Gitぱっち更新後",
      updatedRepository.getRepoName()
    );
    checkString(
      "更新後のリポジトリのパス",
      "/Users/example/GitPatchUpdated",
      updatedRepository.getPath()
    );

    checkRepositoryStatus(
      "重複したパスへの更新",
      RepositoryStatus.DUPLICATE_PATH,
      repositoryService.update(
        0,
        "重複する名前",
        "/Users/example/PracticeRepository"
      )
    );

    RepositoryProfile repositoryAfterFailedUpdate = repositoryService
      .findAll()
      .get(0);
    checkString(
      "失敗した更新後も名前が変わらない",
      "Gitぱっち更新後",
      repositoryAfterFailedUpdate.getRepoName()
    );
    checkString(
      "失敗した更新後もパスが変わらない",
      "/Users/example/GitPatchUpdated",
      repositoryAfterFailedUpdate.getPath()
    );

    checkRepositoryStatus(
      "負の番号での更新",
      RepositoryStatus.INDEX_OUT_OF_RANGE,
      repositoryService.update(-1, "不正な番号", "/Users/example/Invalid")
    );
    checkRepositoryStatus(
      "一覧外の番号での更新",
      RepositoryStatus.INDEX_OUT_OF_RANGE,
      repositoryService.update(
        repositoryService.findAll().size(),
        "不正な番号",
        "/Users/example/Invalid"
      )
    );

    checkRepositoryStatus(
      "2件目のリポジトリ削除",
      RepositoryStatus.SUCCESS,
      repositoryService.delete(1)
    );
    checkInt(
      "1件削除した後のリポジトリ数",
      1,
      repositoryService.findAll().size()
    );
    checkRepositoryStatus(
      "1件目のリポジトリ削除",
      RepositoryStatus.SUCCESS,
      repositoryService.delete(0)
    );
    checkInt(
      "すべて削除した後のリポジトリ数",
      0,
      repositoryService.findAll().size()
    );
    checkRepositoryStatus(
      "空の一覧からの削除",
      RepositoryStatus.INDEX_OUT_OF_RANGE,
      repositoryService.delete(0)
    );
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
      throw new AssertionError(
        testName + "：期待値=" + expected + "、実際=" + actual
      );
    }

    System.out.println("OK: " + testName + "（" + actual + "）");
  }

  private static void checkString(
    String testName,
    String expected,
    String actual
  ) {
    if (!expected.equals(actual)) {
      throw new AssertionError(
        testName + "：期待値=" + expected + "、実際=" + actual
      );
    }

    System.out.println("OK: " + testName + "（" + actual + "）");
  }

  private static void checkRepositoryStatus(
    String testName,
    RepositoryStatus expected,
    RepositoryStatus actual
  ) {
    if (expected != actual) {
      throw new AssertionError(
        testName + "：期待値=" + expected + "、実際=" + actual
      );
    }

    System.out.println("OK: " + testName + "（" + actual + "）");
  }

  private static void checkGitStatus(
    String testName,
    GitActivityService.GitStatus expected,
    GitActivityService.GitStatus actual
  ) {
    if (expected != actual) {
      throw new AssertionError(
        testName + "：期待値=" + expected + "、実際=" + actual
      );
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

  private static void checkContains(
    String testName,
    String expected,
    String actual
  ) {
    if (!actual.contains(expected)) {
      throw new AssertionError(
        testName + "：期待する表示がありません。期待値=" + expected
      );
    }

    System.out.println("OK: " + testName);
  }

  private static void testGitActivityService() {
    GitActivityService gitActivityService = new GitActivityService();

    checkGitStatus(
      "GitPatch自身のGitStatus",
      GitActivityService.GitStatus.SUCCESS,
      gitActivityService.getStatus(".")
    );
    checkGitStatus(
      "空パスのGitStatus",
      GitActivityService.GitStatus.EMPTY_PATH,
      gitActivityService.getStatus("")
    );
    checkGitStatus(
      "存在しないパスのGitStatus",
      GitActivityService.GitStatus.PATH_NOT_FOUND,
      gitActivityService.getStatus("/tmp/path-that-does-not-exist")
    );
    checkGitStatus(
      "GitではないフォルダのGitStatus",
      GitActivityService.GitStatus.NOT_GIT_REPOSITORY,
      gitActivityService.getStatus("/tmp/gitpatch-not-a-repository")
    );

    RepositoryProfile repository = new RepositoryProfile(
      "テスト用リポジトリ",
      "/tmp/test-repository"
    );

    checkTrue(
      "初回のコミットは新しいコミットと判定する",
      gitActivityService.isNewCommit(repository, "commit-001")
    );

    repository.setLastCheckedCommitId("commit-001");

    checkTrue(
      "GitPatch自身をGitリポジトリとして判定",
      gitActivityService.isGitRepository(".")
    );

    checkFalse(
      "存在しないパスをGitリポジトリと判定しない",
      gitActivityService.isGitRepository("/tmp/path-that-does-not-exist")
    );

    checkFalse(
      "同じコミットIDは新しいコミットと判定しない",
      gitActivityService.isNewCommit(repository, "commit-001")
    );

    checkTrue(
      "異なるコミットIDは新しいコミットと判定する",
      gitActivityService.isNewCommit(repository, "commit-002")
    );

    String latestCommitId = gitActivityService.findLatestCommitId(".");

    if (latestCommitId == null || latestCommitId.trim().equals("")) {
      throw new AssertionError(
        "GitPatch自身のパスで最新コミットIDを取得できませんでした。"
      );
    }

    System.out.println(
      "OK: GitPatch自身のパスでコミットIDを取得（" + latestCommitId + "）"
    );
  }

  private static void testActivityStreak() {
    testFirstActivityStartsStreak();
    testSameDayDoesNotIncreaseStreak();
    testNextDayIncreasesStreak();
    testUpdateStreakResetsAfterGap();
    testUpdateStreakKeepsLongestRecord();
    testPastActivityDateIsIgnored();
    testStreakNameBoundaries();
    testUpdateStreakSetsStreakName();
  }

  private static void testMenuGitActivityAlwaysShowsStreak() {
    testMenuShowsStreakAfterNewCommit();
    testMenuShowsStreakWhenCommitIsUnchanged();
    testMenuShowsStreakAfterGitError();
    testMenuShowsStreakAfterRepositoryCancel();
    testMenuShowsStreakForSameDayDifferentCommit();
    testMenuShowsAchievementAndTitleAtThreeDays();
    testMenuShowsResetMessageAfterGap();
    testMenuDoesNotRepeatMilestoneMessageAfterBoundary();
  }

  private static void testMenuShowsStreakAfterNewCommit() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add("GitPatch", ".");

    String output = runMenu(
      "4\n1\n0\n",
      repositoryService,
      activityStreakServiceAt("2026-09-09")
    );

    checkContains(
      "新しいコミットの確認後にstreakを表示する",
      "連続活動：1日",
      output
    );
    checkContains("新しい活動で達成メッセージを表示する", "今日の活動達成！", output);
  }

  private static void testMenuShowsStreakWhenCommitIsUnchanged() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add("GitPatch", ".");
    String latestCommitId = new GitActivityService().findLatestCommitId(".");
    repositoryService.findAll().get(0).setLastCheckedCommitId(latestCommitId);

    String output = runMenu(
      "4\n1\n0\n",
      repositoryService,
      activityStreakServiceAt("2026-09-09")
    );

    checkContains(
      "同じコミットの確認後にstreakを表示する",
      "連続活動：0日",
      output
    );
  }

  private static void testMenuShowsStreakAfterGitError() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add(
      "存在しないリポジトリ",
      "/tmp/path-that-does-not-exist"
    );

    String output = runMenu(
      "4\n1\n0\n",
      repositoryService,
      activityStreakServiceAt("2026-09-09")
    );

    checkContains(
      "Gitエラーの案内後にstreakを表示する",
      "連続活動：0日",
      output
    );
  }

  private static void testMenuShowsStreakAfterRepositoryCancel() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add("GitPatch", ".");

    String output = runMenu(
      "4\n0\n0\n",
      repositoryService,
      activityStreakServiceAt("2026-09-09")
    );

    checkContains(
      "リポジトリ選択取消後にstreakを表示する",
      "連続活動：0日",
      output
    );
  }

  private static void testMenuShowsStreakForSameDayDifferentCommit() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add("テスト用リポジトリ", "/tmp/test-repository");

    String output = runMenu(
      "4\n1\n4\n1\n0\n",
      repositoryService,
      activityStreakServiceAt("2026-09-09"),
      new SequenceGitActivityService("commit-001", "commit-002")
    );

    checkContains(
      "同日の別コミットでXPが2回分になる",
      "経験値: 60",
      output
    );
    checkInt(
      "同日の別コミットでstreak表示が2回ある",
      2,
      countOccurrences(output, "連続活動：1日")
    );
  }

  private static void testMenuShowsAchievementAndTitleAtThreeDays() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add("テスト用リポジトリ", "/tmp/test-repository");

    ActivityStreak streak = new ActivityStreak();
    streak.setCurrentStreak(2);
    streak.setLongestStreak(2);
    streak.setLastActivityDate(LocalDate.parse("2026-09-08"));

    String output = runMenu(
      "4\n1\n0\n",
      repositoryService,
      streak,
      activityStreakServiceAt("2026-09-09"),
      new SequenceGitActivityService("commit-003")
    );

    checkContains("3日目の達成メッセージを表示する", "今日の活動達成！", output);
    checkContains(
      "3日目の節目メッセージを表示する",
      "すごい！3日連続でGit活動を達成した！",
      output
    );
    checkContains("3日目の称号表示", "称号：Git見習い", output);
    checkContains(
      "称号獲得メッセージを表示する",
      "称号「Git見習い」を獲得！",
      output
    );
  }

  private static void testMenuShowsResetMessageAfterGap() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add("テスト用リポジトリ", "/tmp/test-repository");

    ActivityStreak streak = new ActivityStreak();
    streak.setCurrentStreak(3);
    streak.setLongestStreak(3);
    streak.setLastActivityDate(LocalDate.parse("2026-09-07"));

    String output = runMenu(
      "4\n1\n0\n",
      repositoryService,
      streak,
      activityStreakServiceAt("2026-09-09"),
      new SequenceGitActivityService("commit-after-gap")
    );

    checkContains(
      "活動日の空白後にリセットメッセージを表示する",
      "今日はGitさわれなかったのかな? また1から頑張ろう！",
      output
    );
    checkContains("活動日の空白後は1日へ戻る", "連続活動：1日", output);
  }

  private static void testMenuDoesNotRepeatMilestoneMessageAfterBoundary() {
    RepositoryService repositoryService = new RepositoryService();
    repositoryService.add("テスト用リポジトリ", "/tmp/test-repository");

    ActivityStreak streak = new ActivityStreak();
    streak.setCurrentStreak(3);
    streak.setLongestStreak(3);
    streak.setLastActivityDate(LocalDate.parse("2026-09-08"));

    String output = runMenu(
      "4\n1\n0\n",
      repositoryService,
      streak,
      activityStreakServiceAt("2026-09-09"),
      new SequenceGitActivityService("commit-day-four")
    );

    checkContains("節目後の通常活動を表示する", "連続活動：4日", output);
    checkFalse(
      "節目後に称号獲得メッセージを繰り返さない",
      output.contains("称号「Git見習い」を獲得！")
    );
  }

  private static String runMenu(
    String input,
    RepositoryService repositoryService,
    ActivityStreakService activityStreakService
  ) {
    return runMenu(
      input,
      repositoryService,
      new ActivityStreak(),
      activityStreakService,
      new GitActivityService()
    );
  }

  private static String runMenu(
    String input,
    RepositoryService repositoryService,
    ActivityStreakService activityStreakService,
    GitActivityService gitActivityService
  ) {
    return runMenu(
      input,
      repositoryService,
      new ActivityStreak(),
      activityStreakService,
      gitActivityService
    );
  }

  private static String runMenu(
    String input,
    RepositoryService repositoryService,
    ActivityStreak activityStreak,
    ActivityStreakService activityStreakService,
    GitActivityService gitActivityService
  ) {
    Menu menu = new Menu(
      new Pet(),
      new PetService(),
      gitActivityService,
      repositoryService,
      activityStreak,
      activityStreakService,
      new Scanner(new StringReader(input))
    );

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    try {
      System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
      menu.start();
    } finally {
      System.setOut(originalOut);
    }

    return new String(output.toByteArray(), StandardCharsets.UTF_8);
  }

  private static int countOccurrences(String text, String target) {
    int count = 0;
    int start = 0;
    while ((start = text.indexOf(target, start)) >= 0) {
      count++;
      start += target.length();
    }
    return count;
  }

  private static ActivityStreakService activityStreakServiceAt(String date) {
    ZoneId zone = ZoneId.of("Asia/Tokyo");
    LocalDate localDate = LocalDate.parse(date);
    Clock clock = Clock.fixed(localDate.atStartOfDay(zone).toInstant(), zone);
    return new ActivityStreakService(clock);
  }

  private static void testFirstActivityStartsStreak() {
    ActivityStreak streak = new ActivityStreak();
    ActivityStreakService service = activityStreakServiceAt("2026-09-05");

    service.updateStreak(streak);

    checkInt("初回の活動日は連続1日になる", 1, streak.getCurrentStreak());
    checkTrue(
      "初期の称号はNONEになる",
      streak.getStreakName() == ActivityStreak.StreakName.NONE
    );
  }

  private static void testSameDayDoesNotIncreaseStreak() {
    ActivityStreak streak = new ActivityStreak();
    ActivityStreakService service = activityStreakServiceAt("2026-09-05");

    service.updateStreak(streak);
    service.updateStreak(streak);

    checkInt(
      "同じ日の活動では連続日数が増えない",
      1,
      streak.getCurrentStreak()
    );
  }

  private static void testNextDayIncreasesStreak() {
    ActivityStreak streak = new ActivityStreak();
    ActivityStreakService firstDay = activityStreakServiceAt("2026-09-05");
    ActivityStreakService nextDay = activityStreakServiceAt("2026-09-06");

    firstDay.updateStreak(streak);
    nextDay.updateStreak(streak);

    checkInt("翌日の活動で連続日数が増える", 2, streak.getCurrentStreak());
  }

  private static void testUpdateStreakResetsAfterGap() {
    ActivityStreak streak = new ActivityStreak();
    ActivityStreakService firstDay = activityStreakServiceAt("2026-09-05");
    ActivityStreakService afterGap = activityStreakServiceAt("2026-09-07");

    firstDay.updateStreak(streak);
    afterGap.updateStreak(streak);

    checkInt(
      "活動日が空くと連続ストリークが1に戻る",
      1,
      streak.getCurrentStreak()
    );
  }

  private static void testUpdateStreakKeepsLongestRecord() {
    ActivityStreak streak = new ActivityStreak();
    activityStreakServiceAt("2026-09-08").updateStreak(streak);
    activityStreakServiceAt("2026-09-09").updateStreak(streak);
    activityStreakServiceAt("2026-09-10").updateStreak(streak);
    activityStreakServiceAt("2026-09-13").updateStreak(streak);

    checkInt("最長記録を保持する", 3, streak.getLongestStreak());
  }

  private static void testPastActivityDateIsIgnored() {
    ActivityStreak streak = new ActivityStreak();

    activityStreakServiceAt("2026-09-09").updateStreak(streak);
    activityStreakServiceAt("2026-09-08").updateStreak(streak);

    checkInt(
      "過去日の活動で連続日数が変わらない",
      1,
      streak.getCurrentStreak()
    );
    checkInt(
      "過去日の活動で最長記録が変わらない",
      1,
      streak.getLongestStreak()
    );
    checkTrue(
      "過去日の活動で最後の活動日が戻らない",
      LocalDate.parse("2026-09-09").equals(streak.getLastActivityDate())
    );
  }

  private static void testStreakNameBoundaries() {
    checkStreakName(
      "0日目の称号",
      ActivityStreak.StreakName.NONE,
      0
    );
    checkStreakName(
      "2日目の称号",
      ActivityStreak.StreakName.NONE,
      2
    );
    checkStreakName(
      "3日目の称号",
      ActivityStreak.StreakName.GIT_APPRENTICE,
      3
    );
    checkStreakName(
      "6日目の称号",
      ActivityStreak.StreakName.GIT_APPRENTICE,
      6
    );
    checkStreakName("7日目の称号", ActivityStreak.StreakName.GIT_MAN, 7);
    checkStreakName(
      "13日目の称号",
      ActivityStreak.StreakName.GIT_MAN,
      13
    );
    checkStreakName("14日目の称号", ActivityStreak.StreakName.GIT_STAR, 14);
    checkStreakName("15日目の称号", ActivityStreak.StreakName.GIT_STAR, 15);
  }

  private static void checkStreakName(
    String testName,
    ActivityStreak.StreakName expected,
    int currentStreak
  ) {
    ActivityStreak.StreakName actual = activityStreakServiceAt(
      "2026-09-09"
    ).determineStreakName(currentStreak);
    if (expected != actual) {
      throw new AssertionError(
        testName + "：期待値=" + expected + "、実際=" + actual
      );
    }
    System.out.println("OK: " + testName + "（" + actual + "）");
  }

  private static void testUpdateStreakSetsStreakName() {
    ActivityStreak streak = new ActivityStreak();
    activityStreakServiceAt("2026-09-07").updateStreak(streak);
    activityStreakServiceAt("2026-09-08").updateStreak(streak);
    activityStreakServiceAt("2026-09-09").updateStreak(streak);

    checkTrue(
      "活動更新後に称号を保持する",
      streak.getStreakName() == ActivityStreak.StreakName.GIT_APPRENTICE
    );
  }

  private static class SequenceGitActivityService
    extends GitActivityService {

    private final String[] commitIds;
    private int nextCommitIndex;

    private SequenceGitActivityService(String... commitIds) {
      this.commitIds = commitIds;
    }

    @Override
    public GitStatus getStatus(String path) {
      return GitStatus.SUCCESS;
    }

    @Override
    public String findLatestCommitId(String path) {
      int index = Math.min(nextCommitIndex, commitIds.length - 1);
      nextCommitIndex++;
      return commitIds[index];
    }
  }
}
