import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Network {
    private Socket socket;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    private static Network instance;

    private Network() {
        try {
            socket = new Socket("localhost", 1313);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Network getInstance() {
        if (instance == null) {
            return new Network();
        }
        return instance;
    }

    public AbstractMessage readMessage() throws IOException, ClassNotFoundException {
        //Object msg = in.readObject();
        System.out.println("читаю сообщение"); // + msg.getClass());
        return (AbstractMessage) in.readObject(); // ВОТ ЗДЕСЬ ВСЁ ЗАВИСАЕТ!!!
    }

    public void writeMessage (AbstractMessage msg) throws IOException {
        System.out.println("отправляю сообщение" + msg.getClass());
        out.writeObject(msg);
        out.flush();
    }

}
