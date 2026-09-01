import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

			if (exitCode != 0) {
				return false;
			}

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()))) {
				String output = reader.readLine();
				return "true".equals(output == null ? null : output.trim());
			}
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public String findLatestCommitId(String path) {
		if (!isGitRepository(path)) {
			return null;
		}

		ProcessBuilder processBuilder = new ProcessBuilder(
				"git",
				"-C",
				path,
				"log",
				"-1",
				"--format=%H");

		processBuilder.redirectErrorStream(true);

		try {
			Process process = processBuilder.start();
			int exitCode = process.waitFor();

			if (exitCode != 0) {
				return null;
			}

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()))) {
				String output = reader.readLine();

				if (output == null) {
					return null;
				}

				output = output.trim();
				return output.equals("") ? null : output;
			}
		} catch (IOException e) {
			return null;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}
}
