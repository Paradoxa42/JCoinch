import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class JCoincheClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        if (JCoincheClient.gameID == -1) {
            String[] parts = msg.split("|", 2);
            JCoincheClient.gameID = Integer.parseInt(parts[0]);
            System.out.println(parts[1].replace("|", ""));
            System.out.println("Game ID is " + JCoincheClient.gameID);
        }
        else {
            System.out.println(msg);
        }

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}