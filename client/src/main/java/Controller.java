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
    private final Network net = Network.getInstance();

    public void btnExitOnClose(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    VBox serverPanel, clientPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ServerController serP = (ServerController) serverPanel.getProperties().get("ctrl");
        ClientController cliP = (ClientController) clientPanel.getProperties().get("ctrl2");

        try {

            if (!Files.exists(Paths.get(clientDir))) {
               Files.createDirectory(Paths.get(clientDir));
            }
            cliP.updateListClient(Paths.get(cliP.getCurrentPath()));
            net.writeMessage(new ListRequest());
            System.out.println("Запрос обновления списка сервера v2");
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        AbstractMessage message = net.readMessage(); //in.readObject();
                        System.out.println("Пришло сообщение от сервера");
                        if (message instanceof ListResponse) {
                            ListResponse list = (ListResponse) message;
                            System.out.println("Пришёл список от сервера на обновление списка");
                            Platform.runLater(() -> {
                                serP.updateListServer(list); //server.getItems().addAll(list.getFilesData());
                               // Обновить список сервера
                            });
                            serP.updateListServer(list);
                        }
                        if (message instanceof FileMessage) {
                            FileMessage file = (FileMessage) message;
                            Files.write(Paths.get(cliP.getCurrentPath(), file.getFileName()), file.getData(), StandardOpenOption.CREATE);
                            //Files.write(Paths.get(clientDir, file.getFileName()), file.getData(), StandardOpenOption.CREATE);
                            cliP.updateListClient(Paths.get(cliP.getCurrentPath()));
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setDaemon(true);
            t.start();

            cliP.updateListClient(Paths.get(cliP.getCurrentPath()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




        /*@Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            /*Socket socket = new Socket("localhost", 1313);
            in = new ObjectDecoderInputStream(socket.getInputStream());
            out = new ObjectEncoderOutputStream(socket.getOutputStream());*/

            /*if (!Files.exists(Paths.get(clientDir))) {
                Files.createDirectory(Paths.get(clientDir));
            }*/
    //List<String> clientFiles = FileUtils.getFiles(Paths.get(clientDir));
            /*out.writeObject(new ListRequest());
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
            client.getItems().addAll(clientFiles);*/
        /*} catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}

