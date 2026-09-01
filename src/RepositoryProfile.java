public class RepositoryProfile {
	private String repoName;
	private String path;
	private String lastCheckedCommitId;

	public RepositoryProfile(String repoName, String path) {
		this.repoName = repoName;
		this.path = path;
		this.lastCheckedCommitId = null;
	}

	public String getRepoName() {
		return repoName;
	}

	public String getPath() {
		return path;
	}

	public String getLastCheckedCommitId() {
		return lastCheckedCommitId;
	}

	void setName(String repoName) {
		this.repoName = repoName;
	}

	void setPath(String path) {
		this.path = path;
	}

	void setLastCheckedCommitId(String lastCheckedCommitId) {
		this.lastCheckedCommitId = lastCheckedCommitId;
	}

	public void setRepoName(String repoName2) {
		// TODO 自動生成されたメソッド・スタブ

	}
	//	1. repoNameが空でないか確認する
	//	2. pathが空でないか確認する
	//	3. 同じpathが既に登録済みでないか確認する
	//	4. new RepositoryProfile(repoName, path) を作る
	//	5. repositories.add(...) する
	//	6. trueを返す

}
