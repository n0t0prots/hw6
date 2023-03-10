import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger implements Logger {
    private final String pathToFile;
    private final LoggingLevel currentLoggingLevel;
    private final long maxFileSize;
    private String newFileName;
    private final String logEntriesFormat;
    private long lastModifiedFileLength;

    public FileLogger(FileLoggerConfiguration fileLoggerConfiguration) {
        DateTimeFormatter fileTimeLogFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String formattedLogTime = fileTimeLogFormat.format(now);
        this.pathToFile = fileLoggerConfiguration.getPathToFile();
        this.currentLoggingLevel = fileLoggerConfiguration.getCurrentLoggingLevel();
        this.maxFileSize = fileLoggerConfiguration.getMaxFileSize();
        this.newFileName = fileLoggerConfiguration.getNewFileName();
        this.logEntriesFormat = String.format("%s %s Message:", formattedLogTime,
                currentLoggingLevel);
    }

    //Main methods
    @Override
    public void debug(String debugMessage) {
        Path path = getNewFileName();
        if (currentLoggingLevel.equals(LoggingLevel.DEBUG)) {
            writeLogToFile(path, logEntriesFormat + debugMessage + '\n');
        }
    }

    @Override
    public void info(String infoMessage) {
        Path path = getNewFileName();
        if (currentLoggingLevel.equals(LoggingLevel.INFO)
                || currentLoggingLevel.equals(LoggingLevel.DEBUG)) {
            writeLogToFile(path, logEntriesFormat + infoMessage + '\n');
        }
    }

    //Auxiliary methods
    private void writeLogToFile(Path path, String content) {
        try {
            if (lastModifiedFileLength < maxFileSize) {
                Files.write(path, content.getBytes(StandardCharsets.UTF_8),         /*create & write to file*/
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                String exceptionMessage = String.format("Max:%d Current:%d Path:%s",
                        maxFileSize,
                        lastModifiedFileLength, path.toAbsolutePath());
                throw new FileMaxSizeReachedException(exceptionMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FileMaxSizeReachedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            try {
                Files.write(path, content.getBytes(StandardCharsets.UTF_8),         /*create & write to file*/
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);  /*after exception has been thrown*/
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private Path getNewFileName() {
        final File dir = (new File(pathToFile));
        final boolean isDirectoryCreated = dir.mkdir();   /*creation of new directory at first start*/
        File[] files = dir.listFiles();
        if (files != null) {
            if (isDirectoryCreated && files.length == 0) {
                File file = new File(newFileName);
                try {
                    file.createNewFile();             /*creation of new file at directory at first start*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!isDirectoryCreated && files.length > 0) {
                File lastModifiedFile = getLastModifiedFile(files);
                if (getLastModifiedFileSize(lastModifiedFile) < maxFileSize) {     /* file name when directory*/
                    newFileName = pathToFile + lastModifiedFile.getName();         /* exists and is not empty*/
                } else if (getLastModifiedFileSize(lastModifiedFile) > maxFileSize) {
                    DateTimeFormatter fileNameTimeFormat =
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH.mm.ss.SSS");
                    LocalDateTime now = LocalDateTime.now();
                    String formattedDate = fileNameTimeFormat.format(now);
                    newFileName = String.format("src/logsFolder/Log_%s.txt",            /* file name when*/
                            formattedDate);                                        /*file size limit exceeded*/
                }
            }
        }
        return Paths.get(newFileName);
    }

    private File getLastModifiedFile(File[] files) {
        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    private long getLastModifiedFileSize(File lastModified) {
        return lastModifiedFileLength = lastModified.length();
    }
}
