public class Main {

  public static void main(String[] args) {
    Pet pet = new Pet();
    PetService petService = new PetService();
    GitActivityService gitActivityService = new GitActivityService();
    RepositoryService repositoryService = new RepositoryService();

    Menu menu = new Menu(
      pet,
      petService,
      gitActivityService,
      repositoryService,
      ActivityStreak,
      ActivityStreakService,

    );

    menu.start();
  }
}
