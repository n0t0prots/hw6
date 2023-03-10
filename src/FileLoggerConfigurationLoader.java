import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLoggerConfigurationLoader implements Loader {
    private String pathToFile;
    private LoggingLevel currentLoggingLevel;
    private long maxFileSize;
    private String newFileName;

    @Override
    public FileLoggerConfiguration load(String configFileName) {
        try (FileReader fr = new FileReader((configFileName));
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("FILE")) {
                    pathToFile = line.split(":")[1];
                } else if (line.contains("LEVEL")) {
                    String level = line.split(":")[1];
                    currentLoggingLevel = LoggingLevel.valueOf(level);
                } else if (line.contains("MAX-SIZE")) {
                    String temp = line.split(":")[1];
                    maxFileSize = Long.parseLong(temp);
                } else if (line.contains("FORMAT")) {
                    DateTimeFormatter fileNameTimeFormat =
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss.SSS");
                    LocalDateTime now = LocalDateTime.now();
                    String formattedDate = fileNameTimeFormat.format(now);
                    newFileName = pathToFile + (line.split(":")[1]) + formattedDate + ".txt";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileLoggerConfiguration(pathToFile, currentLoggingLevel,
                maxFileSize, newFileName);
    }
}