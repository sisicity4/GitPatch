public class RepositoryProfile {
	private String name;
	private int exp;
	private int level;
	private int mood;
	private int hunger;
	
	public void gainexp(int amount) {
		exp += amount;
		
		if (exp >= level * 100) {
			exp -= level * 100;
			level++;
		}
	}
}
