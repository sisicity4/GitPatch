import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final Pet pet;
    private final PetService petService;

    public Menu(Pet pet, PetService petService) {
        scanner = new Scanner(System.in);
        this.pet = pet;
        this.petService = petService;
    }

    public void start() {
        boolean running = true;

        while (running) {
            showMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> showPetStatus();
                case "2" -> feedPet();
                case "3" -> strokePet();
                case "0" -> {
                    System.out.println("またね。ぱっちをよろしくね。");
                    running = false;
                }
                default -> System.out.println("0〜3の番号を入力してください。");
            }
        }
    }

    private void showMenu() {
        System.out.println();
        System.out.println("=== Gitぱっち ===");
        System.out.println("1. ぱっちの様子を見る");
        System.out.println("2. ごはんをあげる");
        System.out.println("3. なでる");
        System.out.println("0. 終了する");
        System.out.print("番号を入力: ");
    }

    private void showPetStatus() {
        System.out.println();
        System.out.println(pet.getName() + " " + faceOf(pet.getExpression()));
        System.out.println("レベル: " + pet.getLevel());
        System.out.println("経験値: " + pet.getExp());
        System.out.println("満腹度: " + pet.getHunger());
        System.out.println("機嫌: " + pet.getMood());
        System.out.println(statusMessageOf(pet.getExpression()));
    }

    private void feedPet() {
        System.out.print("ごはんで回復する量を入力: ");
        String input = scanner.nextLine().trim();

        try {
            int amount = Integer.parseInt(input);
            if (amount <= 0) {
                System.out.println("1以上の数を入力してください。");
                return;
            }

            petService.feed(pet, amount);
            System.out.println("ぱっちはごはんを食べた。満腹度が上がった！");
            showPetStatus();
        } catch (NumberFormatException e) {
            System.out.println("数字を入力してください。");
        }
    }

    private void strokePet() {
        petService.stroke(pet);
        System.out.println("ぱっちをなでた。機嫌がよくなった！");
        showPetStatus();
    }

    private String faceOf(Pet.Expression expression) {
        return switch (expression) {
            case HUNGRY -> "(´；ω；`)";
            case SAD -> "(｡•́︿•̀｡)";
            case HAPPY -> "(＾▽＾)";
            case NORMAL -> "(・ω・)";
        };
    }

    private String statusMessageOf(Pet.Expression expression) {
        return switch (expression) {
            case HUNGRY -> "おなかがすいているみたい。ごはんをあげよう。";
            case SAD -> "少し元気がないみたい。なでてあげよう。";
            case HAPPY -> "とてもごきげん！";
            case NORMAL -> "今日ものんびり過ごしている。";
        };
    }
}
