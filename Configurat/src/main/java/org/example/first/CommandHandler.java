package org.example.first;

import java.util.*;

public class CommandHandler {
    private FileSystem fileSystem;

    public CommandHandler(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public void executeCommand(String command) {
        String[] parts = command.split(" ");
        String cmd = parts[0];

        switch (cmd) {
            case "ls":
                fileSystem.listFiles();
                break;
            case "cd":
                if (parts.length > 1) {
                    fileSystem.changeDirectory(parts[1]);
                } else {
                    System.out.println("Укажите директорию.");
                }
                break;
            case "touch":
                if (parts.length > 1) {
                    fileSystem.createFile(parts[1]);
                } else {
                    System.out.println("Укажите имя файла.");
                }
                break;
            case "mkdir":
                if (parts.length > 1) {
                    fileSystem.createDirectory(parts[1]);
                } else {
                    System.out.println("Укажите имя директории.");
                }
                break;
            case "wc":
                if (parts.length > 1) {
                    String content = fileSystem.readFile(fileSystem.getCurrentPath() + "/" + parts[1]);
                    if (content != null) {
                        int lineCount = content.split("\n").length;
                        System.out.println("Количество строк: " + lineCount);
                    } else {
                        System.out.println("Файл не найден: " + parts[1]);
                    }
                } else {
                    System.out.println("Укажите файл.");
                }
                break;
            default:
                System.out.println("Неизвестная команда: " + cmd);
        }
    }
}
