import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {
    public enum typeFile {
        FILE ("F"),
        DIRECTORY ("D");

        private String typeName;

        typeFile(String type) {
            this.typeName = type;
        }

        public String getTypeName() {
            return typeName;
        }
    }
    private String fileName;
    private typeFile type;
    private long size;
    private LocalDateTime lastModified;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public typeFile getType() {
        return type;
    }

    public void setType(typeFile type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public FileInfo(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? typeFile.DIRECTORY : typeFile.FILE;
            if (Files.isDirectory(path)) {
                this.size = -1L;
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(3));
        } catch (IOException e) {
            throw new RuntimeException("Unable to create info_file from this path");
        }

    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", type=" + type +
                '}';
    }
}
