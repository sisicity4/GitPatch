import java.util.HashSet;
import java.util.Set;

public class RepositoryProfile {

  private String repoName;
  private String path;
  private String lastCheckedCommitId;
  private final Set<String> rewardedCommitIds;

  public RepositoryProfile(String repoName, String path) {
    this.repoName = repoName;
    this.path = path;
    this.lastCheckedCommitId = null;
    this.rewardedCommitIds = new HashSet<>();
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
    if (lastCheckedCommitId != null) {
      rewardedCommitIds.add(lastCheckedCommitId);
    }
  }

  public boolean hasRewardedCommitId(String commitId) {
    return commitId != null && rewardedCommitIds.contains(commitId);
  }

  public void clearRewardedCommitIds() {
    rewardedCommitIds.clear();
    lastCheckedCommitId = null;
  }

  public void setRepoName(String repoName) {
    this.repoName = repoName;
  }

  // TODO 自動生成されたメソッド・スタブ
}
