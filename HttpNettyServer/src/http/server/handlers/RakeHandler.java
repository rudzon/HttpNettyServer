package http.server.handlers;

import http.server.structure.LastAccessRecord;
import http.server.structure.LastAccessReport;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * Handler to count received/sent bytes  and speed by hand
 *
 * @author rudzon
 */
public class RakeHandler extends ChannelDuplexHandler {

    private long receivedBytes;
    private long sentBytes;
    LastAccessRecord lastAccessRecord;

    public RakeHandler(LastAccessRecord lastAccessRecord) {
        this.lastAccessRecord = lastAccessRecord;
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
        long sessionDuration = System.currentTimeMillis() - lastAccessRecord.getTimestamp();
        long speed = ((receivedBytes + sentBytes)  * 1000) / 
                (sessionDuration == 0 ? 1 : sessionDuration);
        lastAccessRecord.setBytesReceived(receivedBytes);
        lastAccessRecord.setBytesSent(sentBytes);
        lastAccessRecord.setSpeed(speed);
        LastAccessReport.push(lastAccessRecord);
        super.channelInactive(ctx);
    }
}
