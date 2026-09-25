public class Pet {

  private static final String NAME = "ぱっち";
  private static final int HUNGRY_THRESHOLD = 20;
  private static final int SAD_MOOD_THRESHOLD = 20;
  private static final int HAPPY_LEVEL_THRESHOLD = 5;
  private static final int MAX_STATUS = 100;
  private static final int FEED_HUNGER_AMOUNT = 20;
  private static final int STROKE_MOOD_AMOUNT = 10;

  public enum Expression {
    HUNGRY,
    SAD,
    HAPPY,
    NORMAL,
  }

  private int level;
  private int exp;
  private int hunger;
  private int mood;

  public Pet() {
    level = 1;
    exp = 0;
    hunger = 50;
    mood = 50;
  }

  public String getName() {
    return NAME;
  }

  public int getLevel() {
    return level;
  }

  public int getExp() {
    return exp;
  }

  public int getHunger() {
    return hunger;
  }

  public int getMood() {
    return mood;
  }

  public Expression getExpression() {
    if (hunger <= HUNGRY_THRESHOLD) {
      return Expression.HUNGRY;
    }
    if (mood <= SAD_MOOD_THRESHOLD) {
      return Expression.SAD;
    }
    if (level >= HAPPY_LEVEL_THRESHOLD) {
      return Expression.HAPPY;
    }
    return Expression.NORMAL;
  }

  void setLevel(int level) {
    this.level = Math.max(1, level);
  }

  void setExp(int exp) {
    this.exp = Math.max(0, exp);
  }

  void setHunger(int hunger) {
    this.hunger = Math.clamp(hunger, 0, 100);
  }

  void setMood(int mood) {
    this.mood = Math.clamp(mood, 0, 100);
  }

  public void gainExperience(int amount) {
    if (amount <= 0) {
      return;
    }

    setExp(getExp() + amount);

    while (getExp() >= getLevel() * 100) {
      int requiredExp = getLevel() * 100;
      setExp(getExp() - requiredExp);
      setLevel(getLevel() + 1);
    }
  }

  public void feed() {
    setHunger(Math.min(MAX_STATUS, getHunger() + FEED_HUNGER_AMOUNT));
  }

  public void stroke() {
    setMood(Math.min(MAX_STATUS, getMood() + STROKE_MOOD_AMOUNT));
  }
}
