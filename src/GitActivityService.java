import java.io.IOException;

public class GitActivityService {
	public boolean isGitRepository(String path) {
		if (path == null || path.trim().equals("")) {
			return false;
		}
		ProcessBuilder processBuilder = new ProcessBuilder(
				"git",
				"-C",
				path,
				"rev-parse",
				"--is-inside-work-tree");

		processBuilder.redirectErrorStream(true);
		try {
			Process process = processBuilder.start();

			int exitCode = process.waitFor();

			return exitCode == 0;
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}

	}

	public String findLatestCommitId(String path) {
		return null;
	}
}
