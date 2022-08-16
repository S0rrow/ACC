package ACC;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.treewalk.*;

import java.io.*;
import java.util.*;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.*;

public class GitFunctions {
    public String name;

    public boolean clone(String url, String path) {
        App.logger.info("> cloning " + url + " to " + path);
        try {
            name = get_repo_name_from_url(url);
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(new java.io.File(path))
                    .setProgressMonitor(new TextProgressMonitor())
                    .call();
            return true;
        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + "[error] > Exception : " + e.getMessage());
            return false;
        }
    }

    public boolean crawl_commit_id(String repo_name, String path) {
        ProcessBuilder pb = new ProcessBuilder("git", "log", "--pretty=format:\"%H\"");
        try {
            pb.directory(new java.io.File(path));
            Process p = pb.start();
            if (!saveResult(p, path)) {
                return false;
            }
            p.waitFor();
            return true;
        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + "[error] > Exception : " + e.getMessage());
            return false;
        }
    }

    public boolean get_all_commits(ArrayList<String> repo_list, ArrayList<String> repo_name, String path) {
        try {
            for (int i = 0; i < repo_list.size(); i++) {
                Repository repo = new FileRepository(name);
                Collection<Ref> allRefs = repo.getRefDatabase().getRefs();

            }
            return true;
        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + "[error] > Exception : " + e.getMessage());
            return false;
        }
    }

    public String[] get_changed_file(String repo_git, String repo_name, String new_cid, String old_cid) {
        String[] result = new String[4];
        try {
            Repository repo = new FileRepository(repo_git);
            ObjectId oldHead = repo.resolve(old_cid + "^{tree}");
            ObjectId newHead = repo.resolve(new_cid + "^{tree}");
            if (oldHead == null || newHead == null) {
                App.logger.error(App.ANSI_RED + "[error] > oldHead or newHead is null");
                repo.close();
                return null;
            }
            ObjectReader reader = repo.newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldHead);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, newHead);
            Git git = new Git(repo);
            List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
            for (DiffEntry entry : diffs) {
                String str_new = entry.getNewPath();
                String str_old = entry.getOldPath();
                if (str_new.endsWith(".java") && str_old.endsWith(".java")) {
                    result[0] = new_cid;
                    result[1] = old_cid;
                    result[2] = str_new;
                    result[3] = str_old;
                }
            }
            git.close();
            repo.close();
        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + "[error] > Exception : " + e.getMessage());
            return null;
        }
        return result;
    }

    private boolean printResult(Process process) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            return true;
        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + "[error] > Exception : " + e.getMessage());
            return false;
        }
    }

    private boolean saveResult(Process process, String path) {
        try {
            File file = new File(path, "commitID.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String line = "";
            while ((line = reader.readLine()) != null) {
                line = line.substring(1, line.length() - 1);
                writer.write(line + "\n");
            }
            writer.close();
            reader.close();
            return true;
        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + "[error] > Exception : " + e.getMessage());
            return false;
        }
    }

    private String get_repo_name_from_url(String url) {
        String[] url_split = url.split("/");
        for (String split : url_split) {
            if (split.contains(".git")) {
                return split.replace(".git", "");
            }
        }
        return url_split[url_split.length - 1];
    }
}
