import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.ZoneId;

public class Main {

  private static Path getDataFile() {
    return Paths.get("data", "data.csv");
  }

  private static CsvStorage.AppState loadState(CsvStorage storage, Path file) {
    try {
      return storage.load(file);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      return new CsvStorage.AppState(
        new Pet(),
        new java.util.ArrayList<>(),
        new ActivityStreak()
      );
    }
  }

  private static void saveState(
    CsvStorage storage,
    Path file,
    CsvStorage.AppState state
  ) {
    storage.save(file, state);
  }

  public static void main(String[] args) {
    CsvStorage storage = new CsvStorage();
    Path dataFile = getDataFile();
    CsvStorage.AppState state = loadState(storage, dataFile);
    Pet pet = state.pet();
    GitService gitService = new GitService(
      Clock.system(ZoneId.of("Asia/Tokyo"))
    );
    RepositoryService repositoryService = new RepositoryService();
    for (RepositoryProfile repository : state.repositories()) {
      repositoryService.addLoadedRepository(repository);
    }
    ActivityStreak activityStreak = state.activityStreak();
    Menu menu = new Menu(pet, gitService, repositoryService, activityStreak);

    menu.start();
    saveState(
      storage,
      dataFile,
      new CsvStorage.AppState(pet, repositoryService.findAll(), activityStreak)
    );
  }
}
