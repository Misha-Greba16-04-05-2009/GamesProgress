import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final String GAMES_PATH = "D:/Games"; // Для Windows

    public static void main(String[] args) {

        // ========== ЗАДАЧА 1: УСТАНОВКА ==========
        System.out.println("=== Задача 1: Установка игры ===");
        installGame();

        // ========== ЗАДАЧА 2: СОХРАНЕНИЕ ==========
        System.out.println("\n=== Задача 2: Сохранение игры ===");
        saveGameProgress();

        // ========== ЗАДАЧА 3: ЗАГРУЗКА (со звездочкой) ==========
        System.out.println("\n=== Задача 3: Загрузка игры ===");
        loadGameProgress();
    }

    // ==================== ЗАДАЧА 1 ====================
    private static void installGame() {
        StringBuilder log = new StringBuilder();

        String[] dirs = {
                GAMES_PATH + "/src",
                GAMES_PATH + "/src/main",
                GAMES_PATH + "/src/test",
                GAMES_PATH + "/res",
                GAMES_PATH + "/res/drawables",
                GAMES_PATH + "/res/vectors",
                GAMES_PATH + "/res/icons",
                GAMES_PATH + "/savegames",
                GAMES_PATH + "/temp"
        };

        for (String dirPath : dirs) {
            File dir = new File(dirPath);
            if (dir.mkdir()) {
                log.append("Директория создана: ").append(dirPath).append("\n");
                System.out.println("✓ Создана директория: " + dirPath);
            } else {
                log.append("Не удалось создать директорию: ").append(dirPath).append("\n");
                System.out.println("✗ Не удалось создать директорию: " + dirPath);
            }
        }

        String[] files = {
                GAMES_PATH + "/src/main/Main.java",
                GAMES_PATH + "/src/main/Utils.java"
        };

        for (String filePath : files) {
            File file = new File(filePath);
            try {
                if (file.createNewFile()) {
                    log.append("Файл создан: ").append(filePath).append("\n");
                    System.out.println("✓ Создан файл: " + filePath);
                } else {
                    log.append("Не удалось создать файл: ").append(filePath).append("\n");
                    System.out.println("✗ Не удалось создать файл: " + filePath);
                }
            } catch (IOException e) {
                log.append("Ошибка при создании файла ").append(filePath).append(": ").append(e.getMessage()).append("\n");
                System.out.println("✗ Ошибка при создании файла: " + filePath);
            }
        }

        File tempFile = new File(GAMES_PATH + "/temp/temp.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(log.toString());
            System.out.println("✓ Лог записан в файл: " + tempFile.getPath());
        } catch (IOException e) {
            System.out.println("✗ Ошибка при записи лога: " + e.getMessage());
        }
    }

    // ==================== ЗАДАЧА 2 ====================
    private static void saveGameProgress() {
        GameProgress progress1 = new GameProgress(100, 5, 1, 10.5);
        GameProgress progress2 = new GameProgress(85, 7, 2, 25.3);
        GameProgress progress3 = new GameProgress(60, 10, 3, 50.7);

        String savePath1 = GAMES_PATH + "/savegames/save1.dat";
        String savePath2 = GAMES_PATH + "/savegames/save2.dat";
        String savePath3 = GAMES_PATH + "/savegames/save3.dat";

        saveGame(savePath1, progress1);
        saveGame(savePath2, progress2);
        saveGame(savePath3, progress3);

        List<String> filesToZip = new ArrayList<>();
        filesToZip.add(savePath1);
        filesToZip.add(savePath2);
        filesToZip.add(savePath3);

        String zipPath = GAMES_PATH + "/savegames/saves.zip";
        zipFiles(zipPath, filesToZip);

        for (String filePath : filesToZip) {
            File file = new File(filePath);
            if (file.delete()) {
                System.out.println("✓ Удален файл: " + filePath);
            } else {
                System.out.println("✗ Не удалось удалить файл: " + filePath);
            }
        }
    }

    private static void saveGame(String filePath, GameProgress progress) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(progress);
            System.out.println("✓ Сохранение создано: " + filePath);
        } catch (IOException e) {
            System.out.println("✗ Ошибка при сохранении " + filePath + ": " + e.getMessage());
        }
    }

    private static void zipFiles(String zipPath, List<String> filesToZip) {
        try (FileOutputStream fos = new FileOutputStream(zipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            byte[] buffer = new byte[1024];

            for (String filePath : filesToZip) {
                File file = new File(filePath);
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                    System.out.println("✓ Файл добавлен в архив: " + file.getName());
                } catch (IOException e) {
                    System.out.println("✗ Ошибка при добавлении файла " + filePath + ": " + e.getMessage());
                }
            }
            System.out.println("✓ Архив создан: " + zipPath);
        } catch (IOException e) {
            System.out.println("✗ Ошибка при создании архива: " + e.getMessage());
        }
    }

    // ==================== ЗАДАЧА 3 (со звездочкой) ====================
    private static void loadGameProgress() {
        String zipPath = GAMES_PATH + "/savegames/saves.zip";
        String extractPath = GAMES_PATH + "/savegames";

        openZip(zipPath, extractPath);

        String saveFile = extractPath + "/save2.dat";
        GameProgress loadedProgress = openProgress(saveFile);

        if (loadedProgress != null) {
            System.out.println("Загруженное сохранение: " + loadedProgress);
        }
    }

    private static void openZip(String zipPath, String extractPath) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            byte[] buffer = new byte[1024];

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                File newFile = new File(extractPath + "/" + fileName);

                new File(newFile.getParent()).mkdirs();

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    System.out.println("✓ Файл распакован: " + newFile.getPath());
                }
                zis.closeEntry();
            }
            System.out.println("✓ Распаковка архива завершена: " + zipPath);
        } catch (IOException e) {
            System.out.println("✗ Ошибка при распаковке архива: " + e.getMessage());
        }
    }

    private static GameProgress openProgress(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            GameProgress progress = (GameProgress) ois.readObject();
            System.out.println("✓ Файл загружен: " + filePath);
            return progress;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("✗ Ошибка при загрузке файла " + filePath + ": " + e.getMessage());
            return null;
        }
    }
}