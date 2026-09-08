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

  public void setPath(String path) {
    this.path = path;
  }

  public void setLastCheckedCommitId(String lastCheckedCommitId) {
    this.lastCheckedCommitId = lastCheckedCommitId;
  }

  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }

  // TODO 自動生成されたメソッド・スタブ
}
