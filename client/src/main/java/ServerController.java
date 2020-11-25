import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import utils.FileUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ServerController implements Initializable {

    @FXML
    TableView<FileInfo> serverFileList;

    @FXML
    TextField pathFieldServer;

    private String serverDir = "Server_DIR";
    private String clientDir = "Dir_of_User";
    private final Network net = Network.getInstance();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Connect");

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


        serverFileList.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDataColumn);
        serverFileList.getSortOrder().add(fileTypeColumn);

    }

    private void updateListServerV2(Path path) { //не так  - здесь надо через ListResponse
        try {
            pathFieldServer.setText(path.normalize().toAbsolutePath().toString());
            serverFileList.getItems().clear();
            serverFileList.getItems()
                    .addAll(Files.list(path).map(FileInfo :: new).collect(Collectors.toList()));
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Не удалось обновить список файлов", ButtonType.OK);
        }
    }
    public void updateListServer(ListResponse list) {
        System.out.println("Попытка обновления списка сервера");
        serverFileList.getItems().clear();
        Platform.runLater(() -> {
            serverFileList.getItems().addAll(list.getFilesData());
        });
        //serverFileList.getItems().addAll(list.getFilesData());// ни эта строка ни та что выше не работает - по сути это одно и тоже

    }

    public String getSelectedFileName() {
        if (!serverFileList.isFocused()) {
            return null;
        }
        return serverFileList.getSelectionModel().getSelectedItem().getFileName();
    }

    public String getCurrentPath() {
        return pathFieldServer.getText();
    }

    public void download(ActionEvent actionEvent) {
        String fileName = serverFileList.getSelectionModel().getSelectedItem().getFileName(); //было без .getFileName
        //String fileName = "3.txt";
        System.out.println(fileName);
        FileRequest request = new FileRequest(fileName);
        try {
            net.writeMessage(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void btnDeleteFileServer(ActionEvent actionEvent) {
        String fileName = serverFileList.getSelectionModel().getSelectedItem().getFileName();
        //String fileName = "3.txt";
        DeleteFile deleteFile = new DeleteFile(fileName);
        try {
            net.writeMessage(deleteFile);
            net.writeMessage(new ListRequest());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



            /*try {

            if (!Files.exists(Paths.get(clientDir))) {
                Files.createDirectory(Paths.get(clientDir));
            }
            List<String> clientFiles = FileUtils.getFiles(Paths.get(clientDir));
            net.writeMessage(new ListRequest());
            System.out.println("Запрос обновления списка сервера");
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        AbstractMessage message = net.readMessage();
                        System.out.println(message.getClass().toString());
                        if (message instanceof ListResponse) {
                            ListResponse list = (ListResponse) message;
                            serverFileList.getItems().clear();
                            Platform.runLater(() -> {
                                serverFileList.getItems().addAll(list.getFilesData());
                            });
                            serverFileList.getItems().addAll(list.getFilesData());// ни эта строка ни та что выше не работает - по сути это одно и тоже
                            System.out.println("я в потоке, должен вывести списко файлов на сервере");
                        }
                        /*if (message instanceof FileMessage) {
                            FileMessage file = (FileMessage) message;
                            Files.write(Paths.get(clientDir, file.getFileName()), file.getData(), StandardOpenOption.CREATE);
                            //serverFileList.getItems().clear();
                            //serverFileList.getItems().addAll(FileUtils.getFiles(Paths.get(clientDir)));
                            //out.writeObject(new ListRequest());
                            //out.flush();
                        }*/ /*
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            //client.getItems().addAll(clientFiles);

        } catch (Exception e) {
            e.printStackTrace();
        }*/


    //updateListServer(Paths.get(serverDir));

}
