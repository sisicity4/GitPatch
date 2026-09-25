import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GitService {

  private final Clock clock;

  public GitService(Clock clock) {
    this.clock = Objects.requireNonNull(clock, "clock");
  }

  public enum GitStatus {
    SUCCESS,
    EMPTY_PATH,
    PATH_NOT_FOUND,
    NOT_GIT_REPOSITORY,
    NO_COMMIT,
    GIT_COMMAND_FAILED,
    INTERRUPTED,
  }

  private static class CommandResult {

    private final GitStatus status;
    private final String output;

    private CommandResult(GitStatus status, String output) {
      this.status = status;
      this.output = output;
    }
  }

  public static class CommitResult {

    private final GitStatus status;
    private final String commitId;

    public CommitResult(GitStatus status, String commitId) {
      this.status = status;
      this.commitId = commitId;
    }

    public GitStatus getStatus() {
      return status;
    }

    public String getCommitId() {
      return commitId;
    }
  }

  public GitStatus getStatus(String path) {
    if (path == null || path.trim().equals("")) {
      return GitStatus.EMPTY_PATH;
    }

    try {
      if (!Files.isDirectory(Path.of(path))) {
        return GitStatus.PATH_NOT_FOUND;
      }
    } catch (InvalidPathException e) {
      return GitStatus.PATH_NOT_FOUND;
    }

    CommandResult repositoryCheck = runGitCommand(
      path,
      "rev-parse",
      "--is-inside-work-tree"
    );

    if (repositoryCheck.status != GitStatus.SUCCESS) {
      if (isNotGitRepositoryOutput(repositoryCheck.output)) {
        return GitStatus.NOT_GIT_REPOSITORY;
      }
      return repositoryCheck.status;
    }

    if (!"true".equals(repositoryCheck.output)) {
      return GitStatus.NOT_GIT_REPOSITORY;
    }

    CommandResult commitCheck = runGitCommand(path, "log", "-1", "--format=%H");

    if (commitCheck.status != GitStatus.SUCCESS) {
      if (isNoCommitOutput(commitCheck.output)) {
        return GitStatus.NO_COMMIT;
      }
      return commitCheck.status;
    }

    if (commitCheck.output == null || commitCheck.output.equals("")) {
      return GitStatus.NO_COMMIT;
    }

    return GitStatus.SUCCESS;
  }

  public boolean isGitRepository(String path) {
    GitStatus status = getStatus(path);
    return status == GitStatus.SUCCESS || status == GitStatus.NO_COMMIT;
  }

  public CommitResult findLatestCommit(String path) {
    GitStatus status = getStatus(path);
    if (status != GitStatus.SUCCESS) {
      return new CommitResult(status, null);
    }

    CommandResult result = runGitCommand(path, "log", "-1", "--format=%H");
    if (result.status != GitStatus.SUCCESS) {
      if (isNoCommitOutput(result.output)) {
        return new CommitResult(GitStatus.NO_COMMIT, null);
      }
      return new CommitResult(result.status, null);
    }

    if (result.output == null || result.output.equals("")) {
      return new CommitResult(GitStatus.NO_COMMIT, null);
    }

    return new CommitResult(GitStatus.SUCCESS, result.output);
  }

  public String findLatestCommitId(String path) {
    CommitResult result = findLatestCommit(path);
    return result.getStatus() == GitStatus.SUCCESS
      ? result.getCommitId()
      : null;
  }

  public boolean isNewCommit(
    RepositoryProfile repository,
    String latestCommitId
  ) {
    if (repository == null || latestCommitId == null) {
      return false;
    }
    return !repository.hasRewardedCommitId(latestCommitId);
  }

  public void updateStreak(ActivityStreak streak) {
    if (streak == null) {
      return;
    }
    updateStreakAtDate(streak, LocalDate.now(clock));
  }

  public ActivityStreak.StreakName determineStreakName(int currentStreak) {
    if (currentStreak >= 14) {
      return ActivityStreak.StreakName.GIT_STAR;
    }
    if (currentStreak >= 7) {
      return ActivityStreak.StreakName.ACTIVE_CONTRIBUTOR;
    }
    if (currentStreak >= 3) {
      return ActivityStreak.StreakName.GIT_DEVELOPER;
    }
    if (currentStreak >= 2) {
      return ActivityStreak.StreakName.STREAK_KEEPER;
    }
    if (currentStreak >= 1) {
      return ActivityStreak.StreakName.COMMIT_ROOKIE;
    }
    return ActivityStreak.StreakName.NONE;
  }

  private void updateStreakAtDate(
    ActivityStreak streak,
    LocalDate activityDate
  ) {
    LocalDate lastActivityDate = streak.getLastActivityDate();

    if (lastActivityDate == null) {
      streak.setCurrentStreak(1);
    } else if (activityDate.isBefore(lastActivityDate)) {
      return;
    } else if (lastActivityDate.equals(activityDate)) {
      return;
    } else if (lastActivityDate.plusDays(1).equals(activityDate)) {
      streak.setCurrentStreak(streak.getCurrentStreak() + 1);
    } else {
      streak.setCurrentStreak(1);
    }

    if (streak.getCurrentStreak() > streak.getLongestStreak()) {
      streak.setLongestStreak(streak.getCurrentStreak());
    }

    streak.setLastActivityDate(activityDate);
    streak.setStreakName(determineStreakName(streak.getCurrentStreak()));
  }

  private CommandResult runGitCommand(String path, String... arguments) {
    List<String> command = new ArrayList<>();
    command.add("git");
    command.add("-C");
    command.add(path);
    Collections.addAll(command, arguments);

    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.redirectErrorStream(true);

    try {
      Process process = processBuilder.start();
      String output;
      try (
        BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream())
        )
      ) {
        output = reader.readLine();
        output = output == null ? null : output.trim();
      }
      int exitCode = process.waitFor();

      if (exitCode != 0) {
        return new CommandResult(GitStatus.GIT_COMMAND_FAILED, output);
      }

      return new CommandResult(GitStatus.SUCCESS, output);
    } catch (IOException e) {
      return new CommandResult(GitStatus.GIT_COMMAND_FAILED, null);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return new CommandResult(GitStatus.INTERRUPTED, null);
    }
  }

  private boolean isNotGitRepositoryOutput(String output) {
    return (
      output != null && output.toLowerCase().contains("not a git repository")
    );
  }

  private boolean isNoCommitOutput(String output) {
    return output != null && output.contains("does not have any commits yet");
  }
}
