package http.server.handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * Task to send "Hello world" response 
 * @author rudzon
 */
public class HelloWorldRunnable implements Runnable {

    private ChannelHandlerContext ctx;
    private static final byte[] CONTENT = {'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd'};

    /**
     *
     * @param ctx context which is used to send response
     */
    public HelloWorldRunnable(ChannelHandlerContext ctx){
        this.ctx = ctx;
    }
    
    @Override
    public void run() {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, 
                Unpooled.wrappedBuffer(CONTENT));        
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
