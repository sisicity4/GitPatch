import java.util.ArrayList;
import java.util.List;

public class RepositoryService {
	private final List<RepositoryProfile> repositories = new ArrayList<>();

	public RepositoryStatus add(String repoName, String path) {
		if (repoName == null || repoName.trim().equals("")) {
			return RepositoryStatus.EMPTY_REPO_NAME;
		}

		if (path == null || path.trim().equals("")) {
			return RepositoryStatus.EMPTY_PATH;
		}

		for (RepositoryProfile existingRepository : repositories) {
			if (existingRepository.getPath().equals(path)) {
				return RepositoryStatus.DUPLICATE_PATH;
			}
		}

		RepositoryProfile repository = new RepositoryProfile(repoName, path);
		repositories.add(repository);
		return RepositoryStatus.SUCCESS;
	}

	public List<RepositoryProfile> findAll() {
		return List.copyOf(repositories);
	}

	public RepositoryStatus update(int index, String repoName, String path) {
		if (index < 0 || index >= repositories.size()) {
			return RepositoryStatus.INDEX_OUT_OF_RANGE;
		}

		if (repoName == null || repoName.trim().equals("")) {
			return RepositoryStatus.EMPTY_REPO_NAME;
		}

		if (path == null || path.trim().equals("")) {
			return RepositoryStatus.EMPTY_PATH;
		}

		for (int i = 0; i < repositories.size(); i++) {
			if (i != index && repositories.get(i).getPath().equals(path)) {
				return RepositoryStatus.DUPLICATE_PATH;
			}
		}

		RepositoryProfile repository = repositories.get(index);
		repository.setRepoName(repoName);
		repository.setPath(path);
		return RepositoryStatus.SUCCESS;
	}

	public RepositoryStatus delete(int index) {
		if (index < 0 || index >= repositories.size()) {
			return RepositoryStatus.INDEX_OUT_OF_RANGE;
		}

		repositories.remove(index);
		return RepositoryStatus.SUCCESS;
	}
}
