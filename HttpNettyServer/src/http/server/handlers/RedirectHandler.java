package http.server.handlers;

import http.server.structure.RedirectReport;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.List;
import java.util.Map;

/**
 * Handles redirect requests
 *
 * @author rudzon
 */
public class RedirectHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        //System.out.println("RedirectHandler with URI " + req.getUri());    

        if (req.getUri().matches("/redirect[?]url=.*")) {
            QueryStringDecoder decoder = new QueryStringDecoder(req.getUri());
            Map<String, List<String>> parameters = decoder.parameters();

            String url = parameters.get("url").get(0);
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            sendRedirect(ctx, url);
            
            RedirectReport.incrementRedirectCounter(url);

        } else {
            ctx.fireChannelRead(req);
        }
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
