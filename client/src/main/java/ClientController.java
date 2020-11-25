import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import utils.FileUtils;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientController implements Initializable {

  @FXML
  TableView<FileInfo> clientFileList;

  @FXML
  ComboBox<String> boxOfDisk;

  @FXML
  TextField pathFieldClient;

  @FXML
  TextField newNameFile;

    private String clientDir = "Client_DIR";
    private final Network net = Network.getInstance();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("Тип");
        fileTypeColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getType().getTypeName()));
        fileTypeColumn.setPrefWidth(24);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Имя");
        fileNameColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getFileName()));
        fileNameColumn.setPrefWidth(240);

        TableColumn<FileInfo, Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(
                param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setCellFactory(column -> {
            return new TableCell<FileInfo, Long>() {
                @Override
                protected void updateItem(Long item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String text = String.format("%, d байт", item);
                        if (item == -1L) {
                            text = "DIR";
                        }
                        setText(text);
                    }
                }
            };
        });
        fileSizeColumn.setPrefWidth(120);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        TableColumn<FileInfo, String> fileDataColumn = new TableColumn<>("Дата изменения");
        fileDataColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getLastModified().format(dtf)));
        fileDataColumn.setPrefWidth(120);


        clientFileList.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDataColumn);
        clientFileList.getSortOrder().add(fileTypeColumn);

        boxOfDisk.getItems().clear();
        for (Path p: FileSystems.getDefault().getRootDirectories()
             ) {
            boxOfDisk.getItems().add(p.toString());
        }
        boxOfDisk.getSelectionModel().select(0);

        clientFileList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path newPath = Paths.get(pathFieldClient.getText())
                            .resolve(clientFileList.getSelectionModel().getSelectedItem().getFileName());
                    if (Files.isDirectory(newPath)) {
                        updateListClient(newPath);
                    }
                }
            }
        });

        updateListClient(Paths.get(clientDir));
    }

    public void updateListClient(Path path) { // был private
        try {
            pathFieldClient.setText(path.normalize().toAbsolutePath().toString());
        clientFileList.getItems().clear();
            clientFileList.getItems()
                    .addAll(Files.list(path).map(FileInfo :: new).collect(Collectors.toList()));
            System.out.println("Обновили список файлов у клиента");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Не удалось обновить список файлов", ButtonType.OK);
        }
    }

    public void btnPathUpAction(ActionEvent actionEvent) {
        Path upPath = Paths.get(pathFieldClient.getText()).getParent();
        if (upPath != null) {
            updateListClient(upPath);
        }
    }

    public void changeDiskAction(ActionEvent actionEvent) {
        ComboBox<String> element = (ComboBox<String>) actionEvent.getSource();
        updateListClient(Paths.get(element.getSelectionModel().getSelectedItem()));
    }

    public String getSelectedFileName() {
        if (!clientFileList.isFocused()) {
            return null;
        }
        return clientFileList.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath() {
        return pathFieldClient.getText();
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientFileList.getSelectionModel().getSelectedItem().getFileName();
        FileMessage message = new FileMessage(Paths.get(getCurrentPath(), fileName));
        net.writeMessage(message);
        net.writeMessage(new ListRequest());

    }

    public void btnRenameFileToClient(ActionEvent actionEvent) {
        if (newNameFile.getText() == null || newNameFile.getText().equals("")
            || getSelectedFileName() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Введите новое имя файла и выберите файл, который надо переименовать", ButtonType.OK);
            alert.showAndWait();

        } else {
            Path pathTo = Paths.get(getCurrentPath(), newNameFile.getText());
            Path pathFrom = Paths.get(getCurrentPath(), getSelectedFileName());
            try {
                Files.move(pathFrom, pathTo, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateListClient(Paths.get(getCurrentPath()));
        }
    }




            /*try {
            /*Socket socket = new Socket("localhost", 1313);
            in = new ObjectDecoderInputStream(socket.getInputStream());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());*/ /*

            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        AbstractMessage message = net.readMessage();
                        if (message instanceof FileMessage) {
                            FileMessage file = (FileMessage) message;
                            Files.write(Paths.get(getCurrentPath(), file.getFileName()), file.getData(), StandardOpenOption.CREATE);
                            updateListClient(Paths.get(getCurrentPath()));
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();


        } catch (Exception e) {
            e.printStackTrace();
        }*/

}
