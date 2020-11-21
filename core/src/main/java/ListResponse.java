import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListResponse extends AbstractMessage {

    // file.txt
    private final List<FileInfo> filesData; //был List<String>

    public ListResponse(Path dir) throws IOException {
        filesData = Files.list(dir).map(FileInfo :: new).collect(Collectors.toList());

                /*.map(p -> p.getFileName().toString())
                .collect(Collectors.toList());*/

        /*try {
            pathFieldServer.setText(path.normalize().toAbsolutePath().toString());
            serverFileList.getItems().clear();
            serverFileList.getItems()
                    .addAll(Files.list(path).map(FileInfo :: new).collect(Collectors.toList()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Не удалось обновить список файлов", ButtonType.OK);
        }*/
    }

    public List<FileInfo> getFilesData() { //был List<String>
        return filesData;
    }
}
