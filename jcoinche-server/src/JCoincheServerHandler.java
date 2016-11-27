import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.InetAddress;

public class JCoincheServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        channels.add(ctx.channel());
                        if (JCoincheServer.listGame.isEmpty())
                        {
                            System.out.println("New Game");
                            Game newe = new Game();
                            newe.channels.add(ctx.channel());
                            JCoincheServer.listGame.add(newe);
                            new Thread(newe).start();
                            ctx.writeAndFlush(JCoincheServer.id + "|Weleucom please type your name\n\r");
                        }
                        else if (JCoincheServer.listGame.get(JCoincheServer.id).Network.stop_waiting) {
                            System.out.println("Adding New Game");
                            Game newe = new Game();
                            newe.channels.add(ctx.channel());
                            JCoincheServer.listGame.add(newe);
                            new Thread(newe).start();
                            ctx.writeAndFlush(JCoincheServer.id + "|Weleucom please type your name\n\r");
                            JCoincheServer.id += 1;
                        }
                        else {
                            System.out.println("Adding New Player " + JCoincheServer.id);
                            JCoincheServer.listGame.get(JCoincheServer.id).channels.add(ctx.channel());
                            System.out.println("Added");
                            ctx.writeAndFlush(JCoincheServer.id + "|Weleucom please type your name\n\r");
                        }
                    }
                });
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        String[] parts = msg.split("|", 2);
        parts[1] = parts[1].replace("|", "");
        System.out.println(parts[0]);
        // Close the connection if the client has sent 'bye'.
        if ("|bye".equals(parts[1].toLowerCase())) {
            ctx.close();
        }
        else {
            JCoincheServer.listGame.get(Integer.parseInt(parts[0])).Network.message = parts[1].replace("|", "");
            JCoincheServer.listGame.get(Integer.parseInt(parts[0])).pChannels.put(parts[1].replace("|", ""), ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}