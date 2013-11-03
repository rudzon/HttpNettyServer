package http.server.handlers;

import http.server.structure.LastAccessRecord;
import http.server.structure.RequestReport;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.InetSocketAddress;

/**
 *  Accumulates statistics
 * @author rudzon
 */
public class StatisticsHandler extends ChannelTrafficShapingHandler {
    
    private static ChannelGroup allChannels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    private AttributeKey<LastAccessRecord> accessKey;
    
    public StatisticsHandler(long checkInterval,  AttributeKey<LastAccessRecord> accessKey) {
        super(checkInterval);
        this.accessKey = accessKey;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        allChannels.add(ctx.channel());
        LastAccessRecord lastAccessRecord = ctx.channel().attr(accessKey).get();
        lastAccessRecord.setTimestamp(System.currentTimeMillis());
        lastAccessRecord.setSourceIP(((InetSocketAddress) ctx.channel()
                .remoteAddress()).getHostString());
                // do not use .getHostName(), cause reverse lookup
        ctx.channel().attr(accessKey).set(lastAccessRecord);
        super.channelActive(ctx);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            LastAccessRecord lastAccessRecord = ctx.channel().attr(accessKey).get();
            HttpRequest req = (HttpRequest) msg;
            lastAccessRecord.setUri(req.getUri());
            ctx.channel().attr(accessKey).set(lastAccessRecord);
            RequestReport.incrementRequestCounter(lastAccessRecord.getSourceIP());
        }
        super.channelRead(ctx, msg);
    }
        
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    
    /**
     *
     * @return number of current active channels 
     */
    public static int getConnectionsCount(){
        return allChannels.size();
    }
}
