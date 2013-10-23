package http.server.handlers;

import http.server.structure.LastAccessRecord;
import http.server.structure.LastAccessReport;
import http.server.structure.RedirectReport;
import http.server.structure.RequestRecord;
import http.server.structure.RequestReport;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;
import io.netty.util.CharsetUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Composes and response with server status information
 * @author rudzon
 */
public class StatusHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
        if (req.getUri().equals("/status")) {
            FullHttpResponse response =
                    new DefaultFullHttpResponse(HTTP_1_1, OK,
                    Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(
                    buildStatusReport().toString(), CharsetUtil.UTF_8)));

            response.headers().set(CONTENT_TYPE, "text/html");
            response.headers().set(CONTENT_LENGTH,
                    response.content().readableBytes());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.fireChannelRead(req);
        }
    }
    
    private StringBuilder buildStatusReport() {
        SimpleDateFormat dateFormatGmt =
                        new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", new Locale("en"));
        StringBuilder buf = new StringBuilder("<!DOCTYPE html>\n<html>\n<body>");
        buf.append("<meta charset=\"utf-8\"> ");
        buf.append("Total queries: ");
        buf.append(RequestReport.getRequestCount());
        buf.append("<br>");
        buf.append("Unique queries: ");
        buf.append(RequestReport.getHitCount());
        buf.append("<br>");
        buf.append("Currently open connectios: ");
        buf.append(StatisticsHandler.getConnectionsCount());
        buf.append("<br>");

        List<Entry<String, RequestRecord>> reqestInfo = RequestReport.getRequestInfo();
        if (!reqestInfo.isEmpty()) {
            buf.append("<table border=\"1\">");
            buf.append("<caption>Unique queries</caption>");
            buf.append("<tr>");
            buf.append("<th>IP</th>");
            buf.append("<th>Queries</th>");
            buf.append("<th>Last active</th>");
            buf.append("</tr>");

            for (Entry<String, RequestRecord> entry : reqestInfo) {
                buf.append("<tr>");
                buf.append("<th>");
                buf.append(entry.getKey());
                buf.append("</th>");
                buf.append("<th>");
                buf.append(entry.getValue().getRequestCount());
                buf.append("</th>");
                buf.append("<th>");
                Date date = new Date(entry.getValue().getLastRequestTime());
                buf.append(dateFormatGmt.format(date));
                buf.append("</th>");
                buf.append("</tr>");
            }
            buf.append("</table>");
        }


        List<Entry<String, AtomicLong>> redirectInfo = RedirectReport.getRedirectInfo();
        if (!redirectInfo.isEmpty()) {
            buf.append("<table border=\"1\">");
            buf.append("<caption>Redirect queries</caption>");
            buf.append("<tr>");
            buf.append("<th>URL</th>");
            buf.append("<th>Queries</th>");
            buf.append("</tr>");

            for (Entry<String, AtomicLong> entry : redirectInfo) {
                buf.append("<tr>");
                buf.append("<th>");
                buf.append("<a href=\"");
                buf.append(entry.getKey());
                buf.append("\">");
                buf.append(entry.getKey());
                buf.append("</a></th>");
                buf.append("</th>");
                buf.append("<th>");
                buf.append(entry.getValue().toString());
                buf.append("</tr>");

            }
            buf.append("</table>");
        }
        
        List<LastAccessRecord> lastAccessInfo = LastAccessReport.getLastAccessInfo();
        
            buf.append("<table border=\"1\">");
            buf.append("<caption>Last processed connections</caption>");
            buf.append("<tr>");
            buf.append("<th>IP</th>");
            buf.append("<th>URI</th>");
            buf.append("<th>Timestamp</th>");
            buf.append("<th>Bytes sent</th>");
            buf.append("<th>Bytes received</th>");
            buf.append("<th>Speed (bytes/sec)</th>");
            buf.append("</tr>");

            for (LastAccessRecord record : lastAccessInfo) {
                buf.append("<tr>");
                buf.append("<th>");
                buf.append(record.getSourceIP());
                buf.append("</th>");
                buf.append("<th>");
                buf.append(record.getUri());
                buf.append("</th>");
                
                buf.append("<th>");
                buf.append(dateFormatGmt.format(new Date(record.getTimestamp())));
                buf.append("</th>");
                buf.append("<th>");
                buf.append(record.getBytesSent());
                buf.append("</th>");
                buf.append("<th>");
                buf.append(record.getBytesReceived());
                buf.append("</th>");
                buf.append("<th>");
                buf.append(record.getSpeed());
                buf.append("</th>");
                buf.append("</tr>");

            }
            buf.append("</table>");
        
        
        buf.append("</body>\n</html>");
        // there gonna be another way to compose request
        return buf;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
