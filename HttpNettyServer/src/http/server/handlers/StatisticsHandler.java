package http.server.handlers;

import http.server.structure.LastAccessRecord;
import http.server.structure.RequestReport;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.InetSocketAddress;

/**
 *  Accumulates statistics
 * @author rudzon
 */
public class StatisticsHandler extends ChannelTrafficShapingHandler {
    
    private static ChannelGroup allChannels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    
    private LastAccessRecord lastAccessRecord;
    
    public StatisticsHandler(long checkInterval, LastAccessRecord lastAccessRecord) {
        super(checkInterval);
        this.lastAccessRecord = lastAccessRecord;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        allChannels.add(ctx.channel());
        lastAccessRecord.setTimestamp(System.currentTimeMillis());
        lastAccessRecord.setSourceIP(((InetSocketAddress) ctx.channel()
                .remoteAddress()).getHostString());
                // do not use .getHostName(), cause reverse lookup       
        super.channelActive(ctx);
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            lastAccessRecord.setUri(req.getUri());
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
