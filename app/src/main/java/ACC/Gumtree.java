package ACC;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevWalk;

public class Gumtree {
    public boolean diff(String repo_git) {
        try {
            Repository repo = new FileRepository(repo_git);
            RevWalk walk = new RevWalk(repo);

        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + e.getMessage() + App.ANSI_RESET);
            return false;
        }

        return true;
    }
}
