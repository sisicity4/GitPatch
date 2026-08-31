import java.util.ArrayList;
import java.util.List;

public class RepositoryService {
    private final List<RepositoryProfile> repositories = new ArrayList<>();

    public boolean add(String name, String path) {
    	return false;
    }
    public List<RepositoryProfile> findAll(){
        return List.copyOf(repositories);
    }
    public boolean update(int index, String repoName, String path) {
        if (index < 0 || index >= repositories.size()) {
            return false;
        }

        if (repoName == null || repoName.isBlank()) {
            return false;
        }

        if (path == null || path.isBlank()) {
            return false;
        }

        for (int i = 0; i < repositories.size(); i++) {
            if (i != index && repositories.get(i).getPath().equals(path)) {
                return false;
            }
        }

        RepositoryProfile repository = repositories.get(index);
        repository.setRepoName(repoName);
        repository.setPath(path);

        return true;
    }

    public boolean delete(int index) {
        if (index < 0 || index >= repositories.size()) {
            return false;
        }

        repositories.remove(index);
        return true;
    }

    

}
