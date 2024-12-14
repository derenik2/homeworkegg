package org.example.second;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitDependencyVisualizer {

    public static void runner(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: GitDependencyVisualizer <graphviz_path> <repo_path> <output_file> <branch_name>");
            return;
        }

        String graphvizPath = args[0];
        String repoPath = args[1];
        String outputFile = args[2];
        String branchName = args[3];

        try {
            // Получаем зависимости коммитов
            List<CommitNode> commitGraph = getCommitGraph(repoPath, branchName);

            // Генерируем граф в формате DOT
            String dotGraph = generateDotGraph(commitGraph);

            // Выводим граф в виде кода
            System.out.println(dotGraph);

            // Сохраняем результат в файл
            saveToFile(dotGraph, outputFile);
            renderGraph(graphvizPath, outputFile, "graph.png");


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<CommitNode> getCommitGraph(String repoPath, String branchName) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "git", "-C", repoPath, "log", "--pretty=format:%H %P", branchName);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        List<CommitNode> commitGraph = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            String commitHash = parts[0];
            List<String> parents = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));

            commitGraph.add(new CommitNode(commitHash, parents));
        }

        process.waitFor();
        return commitGraph;
    }

    private static String generateDotGraph(List<CommitNode> commitGraph) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph G {\n");

        for (CommitNode node : commitGraph) {
            for (String parent : node.parents) {
                sb.append("    \"").append(parent).append("\" -> \"").append(node.commitHash).append("\";\n");
            }
        }

        sb.append("}\n");
        return sb.toString();
    }
    private static void renderGraph(String graphvizPath, String dotFile, String outputImage) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                graphvizPath, "-Tpng", dotFile, "-o", outputImage);
        Process process = processBuilder.start();
        process.waitFor();
    }
    private static void saveToFile(String dotGraph, String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(dotGraph);
        }
    }
}

class CommitNode {
    String commitHash;
    List<String> parents;

    public CommitNode(String commitHash, List<String> parents) {
        this.commitHash = commitHash;
        this.parents = parents;
    }
}