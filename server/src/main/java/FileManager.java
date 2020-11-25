import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileManager extends ChannelInboundHandlerAdapter {

    private String dir = "Server_DIR";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FileRequest) {
            FileRequest request = (FileRequest) msg;
            ctx.writeAndFlush(new FileMessage(Paths.get(dir, request.getFileName())));
        }
        if (msg instanceof ListRequest) {
            ListResponse newList = new ListResponse(Paths.get(dir));
            System.out.println(newList.getFilesData().toString()); // что-то не так здесь, пытаюсь поймать ошибку
            ctx.writeAndFlush(newList); // здесь всё зависает
            System.out.println("Отрпавляю listResponse на клиента"); //!!!! вот это сообщение не печатается уже!!!
        }
        if (msg instanceof FileMessage) {
            FileMessage file = (FileMessage) msg;
            Files.write(Paths.get(dir, file.getFileName()), file.getData(), StandardOpenOption.CREATE);
        }
        if (msg instanceof DeleteFile) {
            DeleteFile file = (DeleteFile) msg;
            Files.delete(Paths.get(dir, file.getFileName()));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
