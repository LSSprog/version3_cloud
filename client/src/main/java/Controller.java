import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import utils.FileUtils;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable { //Этот контроллер старый переписал отдельно на другие ServerController иClientController

    private String clientDir = "Client_DIR";
    public ListView<String> client;
    public ListView<FileInfo> server; //,было ListView<String>
    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;


    public void upload(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        String fileName = client.getSelectionModel().getSelectedItem();
        FileMessage message = new FileMessage(Paths.get(clientDir, fileName));
        out.writeObject(message);
        out.flush();
        out.writeObject(new ListRequest());
        out.flush();
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = server.getSelectionModel().getSelectedItem().getFileName(); //было без .getFileName
        FileRequest request = new FileRequest(fileName);
        out.writeObject(request);
        out.flush();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 1313);
            in = new ObjectDecoderInputStream(socket.getInputStream());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());

            if (!Files.exists(Paths.get(clientDir))) {
                Files.createDirectory(Paths.get(clientDir));
            }
            List<String> clientFiles = FileUtils.getFiles(Paths.get(clientDir));
            out.writeObject(new ListRequest());
            out.flush();
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Object message = in.readObject();
                        if (message instanceof ListResponse) {
                            ListResponse list = (ListResponse) message;
                            server.getItems().clear();
                            Platform.runLater(() -> {
                                server.getItems().addAll(list.getFilesData());
                            });
                        }
                        if (message instanceof FileMessage) {
                            FileMessage file = (FileMessage) message;
                            Files.write(Paths.get(clientDir, file.getFileName()), file.getData(), StandardOpenOption.CREATE);
                            client.getItems().clear();
                            client.getItems().addAll(FileUtils.getFiles(Paths.get(clientDir)));
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            client.getItems().addAll(clientFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnExitOnClose(ActionEvent actionEvent) {
        Platform.exit();
    }
}

   /* @FXML
    ListView serverP;
    @FXML
    VBox serverPanel, clientPanel;


    ServerController serP = (ServerController) serverPanel.getProperties().get("controller");
    ClientController cliP = (ClientController) clientPanel.getProperties().get("controller");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 1313);
            in = new ObjectDecoderInputStream(socket.getInputStream());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());

            //if (!Files.exists(Paths.get(clientDir))) {
              //  Files.createDirectory(Paths.get(clientDir));
            //}
            //List<String> clientFiles = FileUtils.getFiles(Paths.get(clientDir));
            cliP.updateListClient(Paths.get(cliP.getCurrentPath()));
            out.writeObject(new ListRequest());
            out.flush();
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Object message = in.readObject();
                        if (message instanceof ListResponse) {
                            ListResponse list = (ListResponse) message;
                            serverP.getItems().clear();
                            //server.getItems().clear();
                            Platform.runLater(() -> {
                                serverP.getItems().addAll(list.getFilesData());
                            });
                        }
                        if (message instanceof FileMessage) {
                            FileMessage file = (FileMessage) message;
                            Files.write(Paths.get(clientDir, file.getFileName()), file.getData(), StandardOpenOption.CREATE);
                            client.getItems().clear();
                            client.getItems().addAll(FileUtils.getFiles(Paths.get(clientDir)));
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            //client.getItems().addAll(clientFiles);
            cliP.updateListClient(Paths.get(cliP.getCurrentPath()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

