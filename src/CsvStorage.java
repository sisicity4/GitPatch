import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CsvStorage {

  private static final int CURRENT_VERSION = 1;

  public static record AppState(
    Pet pet,
    List<RepositoryProfile> repositories,
    ActivityStreak activityStreak
  ) {}

  public AppState load(Path file) {
    if (!Files.exists(file)) {
      return emptyState();
    }

    try {
      Pet pet = new Pet();
      List<RepositoryProfile> repositories = new ArrayList<>();
      ActivityStreak activityStreak = new ActivityStreak();
      boolean versionFound = false;

      for (String record : readCsvRecords(file)) {
        if (record.isBlank()) {
          continue;
        }
        List<String> columns = parseCsvLine(record);
        validateRecord(columns);
        if (columns.get(0).equals("VERSION")) {
          if (
            versionFound || Integer.parseInt(columns.get(1)) != CURRENT_VERSION
          ) {
            throw new IllegalArgumentException(
              "CSVのバージョンが対応していません。"
            );
          }
          versionFound = true;
          continue;
        }
        applyRecord(columns, pet, repositories, activityStreak);
      }

      if (!versionFound) {
        throw new IllegalArgumentException("CSVにバージョン情報がありません。");
      }

      return new AppState(pet, repositories, activityStreak);
    } catch (IOException | RuntimeException e) {
      throw new IllegalArgumentException("CSVを読み込めませんでした。", e);
    }
  }

  public void save(Path file, AppState state) {
    try {
      ensureParentDirectory(file);
      Files.write(file, toCsvRecords(state), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("CSVを保存できませんでした。", e);
    }
  }

  private List<String> readCsvRecords(Path file) throws IOException {
    return Files.readAllLines(file, StandardCharsets.UTF_8);
  }

  private String escapeCsv(String value) {
    if (value == null) {
      return "";
    }
    String escaped = value.replace("\"", "\"\"");
    if (
      escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r")
    ) {
      return "\"" + escaped + "\"";
    }
    return escaped;
  }

  private List<String> parseCsvLine(String line) {
    List<String> columns = new ArrayList<>();
    StringBuilder value = new StringBuilder();
    boolean quoted = false;

    for (int index = 0; index < line.length(); index++) {
      char character = line.charAt(index);
      if (character == '"') {
        if (
          quoted && index + 1 < line.length() && line.charAt(index + 1) == '"'
        ) {
          value.append('"');
          index++;
        } else {
          quoted = !quoted;
        }
      } else if (character == ',' && !quoted) {
        columns.add(value.toString());
        value.setLength(0);
      } else {
        value.append(character);
      }
    }

    if (quoted) {
      throw new IllegalArgumentException("CSVの引用符が閉じられていません。");
    }
    columns.add(value.toString());
    return columns;
  }

  private void validateRecord(List<String> columns) {
    if (columns.isEmpty() || columns.get(0).isBlank()) {
      throw new IllegalArgumentException("CSVのレコード種別が空です。");
    }

    int expectedColumns = switch (columns.get(0)) {
      case "VERSION" -> 2;
      case "REWARDED_COMMIT" -> 3;
      case "REPOSITORY" -> 4;
      case "STREAK" -> 4;
      case "PET" -> 5;
      default -> throw new IllegalArgumentException("未知のCSVレコードです。");
    };
    if (columns.size() != expectedColumns) {
      throw new IllegalArgumentException("CSVの列数が不正です。");
    }
  }

  private void applyRecord(
    List<String> columns,
    Pet pet,
    List<RepositoryProfile> repositories,
    ActivityStreak activityStreak
  ) {
    switch (columns.get(0)) {
      case "PET" -> {
        pet.setLevel(Integer.parseInt(columns.get(1)));
        pet.setExp(Integer.parseInt(columns.get(2)));
        pet.setHunger(Integer.parseInt(columns.get(3)));
        pet.setMood(Integer.parseInt(columns.get(4)));
      }
      case "REPOSITORY" -> {
        RepositoryProfile repository = new RepositoryProfile(
          columns.get(1),
          columns.get(2)
        );
        if (!columns.get(3).isEmpty()) {
          repository.setLastCheckedCommitId(columns.get(3));
        }
        repositories.add(repository);
      }
      case "REWARDED_COMMIT" -> {
        RepositoryProfile repository = repositories
          .stream()
          .filter(item -> item.getPath().equals(columns.get(1)))
          .findFirst()
          .orElseThrow(() ->
            new IllegalArgumentException("対象リポジトリがありません。")
          );
        repository.addRewardedCommitId(columns.get(2));
      }
      case "STREAK" -> {
        activityStreak.setCurrentStreak(Integer.parseInt(columns.get(1)));
        activityStreak.setLongestStreak(Integer.parseInt(columns.get(2)));
        if (!columns.get(3).isEmpty()) {
          activityStreak.setLastActivityDate(LocalDate.parse(columns.get(3)));
        }
      }
      default -> throw new IllegalArgumentException("未知のCSVレコードです。");
    }
  }

  private List<String> toCsvRecords(AppState state) {
    List<String> records = new ArrayList<>();
    records.add(String.join(",", "VERSION", String.valueOf(CURRENT_VERSION)));
    Pet pet = state.pet();
    records.add(
      String.join(
        ",",
        "PET",
        String.valueOf(pet.getLevel()),
        String.valueOf(pet.getExp()),
        String.valueOf(pet.getHunger()),
        String.valueOf(pet.getMood())
      )
    );

    for (RepositoryProfile repository : state.repositories()) {
      records.add(
        String.join(
          ",",
          "REPOSITORY",
          escapeCsv(repository.getRepoName()),
          escapeCsv(repository.getPath()),
          escapeCsv(repository.getLastCheckedCommitId())
        )
      );
      for (String commitId : repository.getRewardedCommitIds()) {
        records.add(
          String.join(
            ",",
            "REWARDED_COMMIT",
            escapeCsv(repository.getPath()),
            escapeCsv(commitId)
          )
        );
      }
    }

    ActivityStreak streak = state.activityStreak();
    records.add(
      String.join(
        ",",
        "STREAK",
        String.valueOf(streak.getCurrentStreak()),
        String.valueOf(streak.getLongestStreak()),
        escapeCsv(
          streak.getLastActivityDate() == null
            ? ""
            : streak.getLastActivityDate().toString()
        )
      )
    );
    return records;
  }

  private Path ensureParentDirectory(Path file) throws IOException {
    Path parent = file.toAbsolutePath().getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    return file;
  }

  private AppState emptyState() {
    return new AppState(new Pet(), new ArrayList<>(), new ActivityStreak());
  }
}
