import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitActivityService {
	public enum GitStatus {
		SUCCESS, EMPTY_PATH, PATH_NOT_FOUND, NOT_GIT_REPOSITORY, NO_COMMIT, GIT_COMMAND_FAILED, INTERRUPTED
	}

	private static class CommandResult {
		private final GitStatus status;
		private final String output;

		private CommandResult(GitStatus status, String output) {
			this.status = status;
			this.output = output;
		}
	}

	public GitStatus getStatus(String path) {
		if (path == null || path.trim().equals("")) {
			return GitStatus.EMPTY_PATH;
		}

		try {
			if (!Files.isDirectory(Path.of(path))) {
				return GitStatus.PATH_NOT_FOUND;
			}
		} catch (InvalidPathException e) {
			return GitStatus.PATH_NOT_FOUND;
		}

		CommandResult repositoryCheck = runGitCommand(
				path,
				"rev-parse",
				"--is-inside-work-tree");

		if (repositoryCheck.status != GitStatus.SUCCESS) {
			return repositoryCheck.status;
		}

		if (!"true".equals(repositoryCheck.output)) {
			return GitStatus.NOT_GIT_REPOSITORY;
		}

		CommandResult commitCheck = runGitCommand(
				path,
				"log",
				"-1",
				"--format=%H");

		if (commitCheck.status == GitStatus.NOT_GIT_REPOSITORY) {
			return GitStatus.NO_COMMIT;
		}

		if (commitCheck.status != GitStatus.SUCCESS) {
			return commitCheck.status;
		}

		if (commitCheck.output == null || commitCheck.output.equals("")) {
			return GitStatus.NO_COMMIT;
		}

		return GitStatus.SUCCESS;
	}

	public boolean isGitRepository(String path) {
		GitStatus status = getStatus(path);
		return status == GitStatus.SUCCESS || status == GitStatus.NO_COMMIT;
	}

	public String findLatestCommitId(String path) {
		if (getStatus(path) != GitStatus.SUCCESS) {
			return null;
		}

		CommandResult result = runGitCommand(
				path,
				"log",
				"-1",
				"--format=%H");

		return result.status == GitStatus.SUCCESS ? result.output : null;
	}

	public boolean isNewCommit(
			RepositoryProfile repository,
			String latestCommitId) {
		if (repository == null || latestCommitId == null) {
			return false;
		}
		return !latestCommitId.equals(repository.getLastCheckedCommitId());
	}

	private CommandResult runGitCommand(String path, String... arguments) {
		List<String> command = new ArrayList<>();
		command.add("git");
		command.add("-C");
		command.add(path);
		Collections.addAll(command, arguments);

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);

		try {
			Process process = processBuilder.start();
			int exitCode = process.waitFor();

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()))) {
				String output = reader.readLine();
				output = output == null ? null : output.trim();

				if (exitCode != 0) {
					return new CommandResult(GitStatus.NOT_GIT_REPOSITORY, output);
				}

				return new CommandResult(GitStatus.SUCCESS, output);
			}
		} catch (IOException e) {
			return new CommandResult(GitStatus.GIT_COMMAND_FAILED, null);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new CommandResult(GitStatus.INTERRUPTED, null);
		}
	}
}
