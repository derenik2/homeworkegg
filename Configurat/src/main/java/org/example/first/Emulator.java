package org.example.first;

import java.io.*;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Emulator {
    private String hostname;
    private FileSystem fileSystem;
    private CommandHandler commandHandler;

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public String getHostname() {
        return hostname;
    }

    public Emulator(String configPath) throws Exception {
        // Чтение конфигурации
        JSONObject config = new JSONObject(new JSONTokener(new FileInputStream(configPath)));
        this.hostname = config.getString("hostname");
        String virtualFsPath = config.getString("virtualFsPath");
        String startupScript = config.getString("startupScript");

        // Инициализация виртуальной файловой системы
        this.fileSystem = new FileSystem(virtualFsPath);
        this.commandHandler = new CommandHandler(fileSystem);

        // Выполнение стартового скрипта
        runStartupScript(startupScript);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print(hostname + ":" + fileSystem.getCurrentPath() + "$ ");
            input = scanner.nextLine();
            if (input.trim().equals("exit")) {
                System.out.println("Выход...");
                break;
            }
            commandHandler.executeCommand(input);
        }
    }

    private void runStartupScript(String scriptPath) throws IOException {
        if(scriptPath == null || scriptPath.equals("")) {
            return;
        }
        BufferedReader reader = new BufferedReader(new FileReader(scriptPath));
        String line;
        while ((line = reader.readLine()) != null) {
            commandHandler.executeCommand(line);
        }
        reader.close();
    }

    public static void main(String[] args) {
        try {
            Emulator emulator = new Emulator("C:\\Users\\nazar\\OneDrive\\Desktop\\study\\Configurat\\src\\main\\resources\\config.json");
            emulator.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
