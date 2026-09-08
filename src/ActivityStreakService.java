import java.time.LocalDate;

public class ActivityStreakService {

  public void updateStreak(ActivityStreak streak, LocalDate activityDate) {
    // TODO 自動生成されたメソッド・スタブ
    if (streak == null || activityDate == null) {
      return;
    }
    LocalDate lastActivityDate = streak.getLastActivityDate();

    if (lastActivityDate == null) {
      streak.setCurrentStreak(1);
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
  }
}
