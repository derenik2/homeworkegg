package org.example;

import org.example.second.GitDependencyVisualizer;

public class Main {
    public static void main(String[] args) {
        String[] args2 = new String[4];
        args2[0] = "/path/to/graphviz";
        args2[1]="/path/to/repo";
        args2[2]="/path/to/output.dot";
        args2[3]="branch_name";
        GitDependencyVisualizer.runner(args2);
    }
}