package http.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import java.util.concurrent.TimeUnit;

/**
 * Handles "/hello" request
 * @author rudzon
 */
public class HelloHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (req.getUri().equals("/hello")) {
            ctx.executor().schedule(new HelloWorldRunnable(ctx), 10, TimeUnit.SECONDS);
        } else {
            ctx.fireChannelRead(req);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}