public class FileLoggerConfiguration {
    private final String pathToFile;
    private final LoggingLevel currentLoggingLevel;
    private final long maxFileSize;
    private final String newFileName;

    public FileLoggerConfiguration(String pathToFile, LoggingLevel currentLoggingLevel,
                                   long maxFileSize, String newFileName) {
        this.pathToFile = pathToFile;
        this.currentLoggingLevel = currentLoggingLevel;
        this.maxFileSize = maxFileSize;
        this.newFileName = newFileName;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public LoggingLevel getCurrentLoggingLevel() {
        return currentLoggingLevel;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public String getNewFileName() {
        return newFileName;
    }

    @Override
    public String toString() {
        return "FileLoggerConfiguration{" +
                " pathToFile=" + pathToFile +
                ", currentLoggingLevel=" + currentLoggingLevel +
                ", maxFileSize=" + maxFileSize +
                ", newFileName=" + newFileName +
                '}';
    }
}