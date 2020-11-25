import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListResponse extends AbstractMessage {

    private final List<FileInfo> filesData; //был List<String>

    public ListResponse(Path dir) throws IOException {
        filesData = Files.list(dir).map(FileInfo :: new).collect(Collectors.toList());
                /*.map(p -> p.getFileName().toString()) // другой вариант для String
                .collect(Collectors.toList());*/
    }

    public List<FileInfo> getFilesData() { //был List<String>
        return filesData;
    }

}
