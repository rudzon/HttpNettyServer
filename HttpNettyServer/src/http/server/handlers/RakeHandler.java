package http.server.handlers;

import http.server.structure.LastAccessRecord;
import http.server.structure.LastAccessReport;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

/**
 * Handler to count received/sent bytes  and speed by hand
 *
 * @author rudzon
 */
public class RakeHandler extends ChannelDuplexHandler {

    private long receivedBytes;
    private long sentBytes;
    private AttributeKey<LastAccessRecord> accessKey;

    public RakeHandler(AttributeKey<LastAccessRecord> accessKey) {
        this.accessKey = accessKey;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        receivedBytes += ((ByteBuf) msg).writerIndex();
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {
        sentBytes += ((ByteBuf) msg).writerIndex();
        super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        super.flush(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LastAccessRecord lastAccessRecord = ctx.channel().attr(accessKey).get();
        long sessionDuration = System.currentTimeMillis() - lastAccessRecord.getTimestamp();
        long speed = ((receivedBytes + sentBytes)  * 1000) / 
                (sessionDuration == 0 ? 1 : sessionDuration);
        lastAccessRecord.setBytesReceived(receivedBytes);
        lastAccessRecord.setBytesSent(sentBytes);
        lastAccessRecord.setSpeed(speed);
        ctx.channel().attr(accessKey).set(lastAccessRecord);
        LastAccessReport.push(lastAccessRecord);
        super.channelInactive(ctx);
    }
}
