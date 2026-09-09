import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public class ActivityStreakService {

  private final Clock clock;

  public ActivityStreakService(Clock clock) {
    this.clock = Objects.requireNonNull(clock, "clock");
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
      return ActivityStreak.StreakName.GIT_MAN;
    }
    if (currentStreak >= 3) {
      return ActivityStreak.StreakName.GIT_APPRENTICE;
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
}
