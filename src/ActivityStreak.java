import java.time.LocalDate;

public class ActivityStreak {

  private int currentStreak;
  private int longestStreak;
  private LocalDate lastActivityDate;
  private StreakName streakName;

  public ActivityStreak() {
    setCurrentStreak(0);
    setLongestStreak(0);
    setLastActivityDate(null);
    setStreakName(StreakName.NONE);
  }

  public enum StreakName {
    NONE,
    COMMIT_ROOKIE,
    STREAK_KEEPER,
    GIT_DEVELOPER,
    ACTIVE_CONTRIBUTOR,
    GIT_STAR,
  }

  public int getCurrentStreak() {
    return currentStreak;
  }

  public void setCurrentStreak(int currentStreak) {
    this.currentStreak = currentStreak;
  }

  public int getLongestStreak() {
    return longestStreak;
  }

  public void setLongestStreak(int longestStreak) {
    this.longestStreak = longestStreak;
  }

  public LocalDate getLastActivityDate() {
    return lastActivityDate;
  }

  public void setLastActivityDate(LocalDate lastActivityDate) {
    this.lastActivityDate = lastActivityDate;
  }

  public StreakName getStreakName() {
    return streakName;
  }

  public void setStreakName(StreakName streakName) {
    this.streakName = streakName == null ? StreakName.NONE : streakName;
  }
}
