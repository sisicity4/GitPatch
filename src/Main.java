import java.time.Clock;
import java.time.ZoneId;

public class Main {

  public static void main(String[] args) {
    Pet pet = new Pet();
    PetService petService = new PetService();
    GitActivityService gitActivityService = new GitActivityService();
    RepositoryService repositoryService = new RepositoryService();
    ActivityStreak activityStreak = new ActivityStreak();
    ActivityStreakService activityStreakService = new ActivityStreakService(
      Clock.system(ZoneId.of("Asia/Tokyo"))
    );

    Menu menu = new Menu(
      pet,
      petService,
      gitActivityService,
      repositoryService,
      activityStreak,
      activityStreakService
    );

    menu.start();
  }
}
