package http.server;

import http.server.handlers.DefaultHandler;
import http.server.handlers.HelloHandler;
import http.server.handlers.RakeHandler;
import http.server.handlers.RedirectHandler;
import http.server.handlers.StatisticsHandler;
import http.server.handlers.StatusHandler;
import http.server.structure.LastAccessRecord;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;


/**
 * 
 * @author rudzon
 */
public class HttpServerChildInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        LastAccessRecord lastAccessRecord = new LastAccessRecord();
        
        pipeline.addLast("rake-handler", new RakeHandler(lastAccessRecord));
        pipeline.addLast("codec", new HttpServerCodec());
        // StatisticsHandler constructor zero param doesn't matter actually, 
        // cant get trafficCounter to work properly
        pipeline.addLast("statistics-handler", new StatisticsHandler(0, lastAccessRecord));
        //AbstractTrafficShapingHandler.DEFAULT_CHECK_INTERVAL));
        pipeline.addLast("hello-handler", new HelloHandler());
        pipeline.addLast("redirect-handler", new RedirectHandler());
        pipeline.addLast("status-handler", new StatusHandler());
        pipeline.addLast("default-handler", new DefaultHandler());
    }
}
