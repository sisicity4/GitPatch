import java.time.LocalDate;

public class ActivityStreak {

  private int currentStreak;
  private int longestStreak;
  private LocalDate lastActivityDate;
  private String streakName;

  public ActivityStreak() {
    setCurrentStreak(0);
    setLongestStreak(0);
    setLastActivityDate(null);
  }

  public enum StreakName {
    NONE,
    GIT_APPRENTICE,
    GIT_MAN,
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

  public String getStreakName() {
    return streakName;
  }

  public void setStreakName(String streakName) {
    this.streakName = streakName;
  }
}
