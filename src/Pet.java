
public class Pet {
    private String name;
    private int level;
    private int exp;
    private int hunger;
    private int mood;
    private String lastCheckedCommitId;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	public int getHunger() {
		return hunger;
	}
	public void setHunger(int hunger) {
		this.hunger = hunger;
	}
	public int getMood() {
		return mood;
	}
	public void setMood(int mood) {
		this.mood = mood;
	}
	public String getLastCheckedCommitId() {
		return lastCheckedCommitId;
	}
	public void setLastCheckedCommitId(String lastCheckedCommitId) {
		this.lastCheckedCommitId = lastCheckedCommitId;
	}
	@Override
	public String toString() {
		return lastCheckedCommitId;

	}
	public Pet(String name) {
    this.name = name;
    this.level = 1;
    this.exp = 0;
    this.hunger = 50;
    this.mood = 50;
	}
}
