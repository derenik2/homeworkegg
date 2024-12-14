package org.example.first;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.*;

public class FileSystem {
    private Map<String, List<String>> files = new HashMap<>();
    private String currentPath = "/";
    private String tarFilePath;

    public FileSystem(String tarFilePath) throws IOException {
        this.tarFilePath = tarFilePath;
        extractTar(tarFilePath);
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void changeDirectory(String path) {
        if (files.containsKey(path)) {
            currentPath = path;
        } else {
            System.out.println("Путь не найден: " + path);
        }
    }

    public void listFiles() {
        List<String> contents = files.get(currentPath);
        System.out.println(contents);
        if (contents != null) {
            contents.forEach(System.out::println);
        } else {
            System.out.println("Директория пуста.");
        }
    }

    public void createFile(String filename) {
        List<String> contents = files.computeIfAbsent(currentPath, k -> new ArrayList<>());
        if (!contents.contains(filename)) {
            contents.add(filename);
            saveToTar(currentPath + "/" + filename, false);
        } else {
            System.out.println("Файл уже существует: " + filename);
        }
    }

    public void createDirectory(String dirname) {
        String newDir = currentPath + "/" + dirname;
        if (!files.containsKey(newDir)) {
            files.put(newDir, new ArrayList<>());
            saveToTar(newDir, true);
        } else {
            System.out.println("Директория уже существует: " + dirname);
        }
    }

    private void extractTar(String tarFilePath) throws IOException {
        try (InputStream is = new FileInputStream(tarFilePath);
             TarArchiveInputStream tarInput = new TarArchiveInputStream(is)) {

            TarArchiveEntry entry;
            while ((entry = tarInput.getNextTarEntry()) != null) {
                // Очищаем имя файла от "./" в начале
                String entryName = entry.getName().replaceFirst("^\\./", "").trim();

                // Если имя пустое, пропускаем
                if (entryName.isEmpty()) {
                    continue;
                }

                System.out.println("Обрабатываем запись: " + entryName);

                String parentDir = entryName.split("/").length > 1
                        ? entryName.substring(0, entryName.substring(0,entryName.length()-1).lastIndexOf("/"))
                        : "/";
                if (entry.isDirectory()) {
                    files.putIfAbsent(entryName, new ArrayList<>());
                    if (!parentDir.equals(entryName)) {
                        files.putIfAbsent(parentDir, new ArrayList<>());
                        files.get(parentDir).add(entryName);
                    }

                    System.out.println("Добавлена директория: " + entryName);
                } else {
                    files.putIfAbsent(parentDir, new ArrayList<>());
                    files.get(parentDir).add(entryName);

                    System.out.println("Добавлен файл: " + entryName + " в директорию " + parentDir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readFile(String filepath) {
        try (InputStream is = new FileInputStream(tarFilePath);
             TarArchiveInputStream tarInput = new TarArchiveInputStream(is)) {

            TarArchiveEntry entry;
            while ((entry = tarInput.getNextTarEntry()) != null) {
                if (entry.getName().equals(filepath)) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy(tarInput, baos);
                    return baos.toString(); // Возвращаем содержимое файла как строку
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveToTar(String path, boolean isDirectory) {
        File tarFile = new File(tarFilePath);
        File tempFile = new File(tarFile.getAbsolutePath() + ".tmp");

        try (FileOutputStream fos = new FileOutputStream(tempFile);
             TarArchiveOutputStream tarOutput = new TarArchiveOutputStream(fos);
             FileInputStream fis = new FileInputStream(tarFile);
             TarArchiveInputStream tarInput = new TarArchiveInputStream(fis)) {
            TarArchiveEntry entry;
            while ((entry = tarInput.getNextTarEntry()) != null) {
                tarOutput.putArchiveEntry(entry);
                if (!entry.isDirectory()) {
                    IOUtils.copy(tarInput, tarOutput);
                }
                tarOutput.closeArchiveEntry();
            }
            TarArchiveEntry newEntry;
            if (isDirectory) {
                newEntry = new TarArchiveEntry(path + "/");
                newEntry.setMode(TarArchiveEntry.DEFAULT_DIR_MODE);
            } else {
                newEntry = new TarArchiveEntry(path);
                newEntry.setMode(TarArchiveEntry.DEFAULT_FILE_MODE);
                newEntry.setSize(0);
            }

            tarOutput.putArchiveEntry(newEntry);
            tarOutput.closeArchiveEntry();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tarFile.delete()) {
            tempFile.renameTo(tarFile);
        } else {
            System.out.println("Не удалось удалить оригинальный tar-файл.");
        }
    }
}
