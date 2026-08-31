public class RepositoryProfile {
	private String repoName;
    private String path;
    private String lastCheckedCommitId;
	

    public RepositoryProfile(String repoName, String path) {
        this.repoName = repoName;
        this.path = path;
        this.lastCheckedCommitId = null;
    }

    public String getName() {
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
}
