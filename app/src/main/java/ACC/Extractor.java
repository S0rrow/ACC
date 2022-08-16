package ACC;

import java.io.*;
import java.util.*;

public class Extractor {

    HashMap<String, Integer> map = new HashMap<>();
    ArrayList<Integer> AST_types = new ArrayList<>();

    public int extract_vector(String log_text, String repo_name, String result_path) {

        File file = new File(log_text);
        File result_dir = new File(result_path);
        if (!result_dir.exists()) {
            result_dir.mkdir();
        }
        File vector_file = new File(result_dir.getAbsolutePath(), repo_name + "_gumtree_vector.csv");

        String line = null;
        boolean no_change = false;
        boolean add = false;
        int oper = 0;

        try {
            BufferedWriter vector_writer = new BufferedWriter(new FileWriter(vector_file, true));
            String write_line = "";

            BufferedReader log_reader = new BufferedReader(new FileReader(file));

            while ((line = log_reader.readLine()) != null && !no_change) {
                StringTokenizer st = new StringTokenizer(line);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (token.equals("[]")) {
                        no_change = true;
                    }
                    if (token.matches("insert-node|delete-node|update-node|insert-tree|delete-tree|move-tree")) {
                        if (AST_types.size() > 0 && oper != -1) {
                            for (int i = 0; i < AST_types.size(); i++) {
                                int val = 170 * oper + AST_types.get(i);
                                write_line += val + ",";
                            }
                        }
                        AST_types.clear();
                        oper = getNodeNum(token);
                    }
                    if (token.equals("---")) {
                        add = true;
                    }
                    if (token.equals("===")) {
                        add = false;
                    }
                    if (add == true) {
                        if (!Character.isAlphabetic(token.charAt(token.length() - 1)) && add) {
                            token = token.substring(0, token.length() - 1);
                        }

                        for (int i = 0; i < ChangeVector.expanded_nodes.length; i++) {
                            if (token.equals(ChangeVector.expanded_nodes[i])) {
                                AST_types.add(i + 1);
                            }
                        }
                    }
                }
                vector_writer.write(write_line + '\n');
            }
            vector_writer.close();
            log_reader.close();
        } catch (Exception e) {
            App.logger.error(App.ANSI_RED + e.getMessage() + App.ANSI_RESET);
            return -1;
        }
        return no_change ? 1 : 0;
    }

    public int getNodeNum(String str) {
        // on node types
        if (str.equals("delete-node")) {
            return 0;
        }
        if (str.equals("insert-node")) {
            return 1;
        }
        if (str.equals("update-node")) {
            return 2;
        }

        // on tree types
        if (str.equals("delete-tree")) {
            return 3;
        }
        if (str.equals("insert-tree")) {
            return 4;
        }
        if (str.equals("move-tree")) {
            return 5;
        }
        return -1;
    }
}
