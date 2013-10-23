HttpNettyServer
===============

Basic http server using Netty.

Build
--------
It's a netbeans project, so run 

    ant jar
in the project directory (where the `build.xml` is). 
Libraries are included with the project.

Usage
-----
As usual 

    java -jar /dist/HttpNettyServer.jar
will run app on port 8080, specify port as first argument like

    java -jar /dist/HttpNettyServer.jar 12345

Particular qualities of implementation
--------------------------------------
### Workflow
The request pass in the pipeline of worker eventloop looks like this:

`in RakeHandler` Accumulates read bytes.

`in HttpServerCodec` Decodes ByteBufs into HttpRequests .

`in StatisticsHandler` Fills record of the session(IP, URI, timestamp etc.), increments request counter, manages
the allChannels group which is current connection counter.

`in HelloHandler` Schedules a reply("Hello World") with a 10 sec delay, if the URI is `/hello`.

`in RedirectHandler` Sends redirect reply to a given URL ("/redirec?url=<url>"). Increments redirect
counter by url.

`in StatusHandler` Composes status reply from all kinds of reports (LastAccessReport, RedirectReport, RequestReport)

`in DefaultHandler` Replies with failure

`out StatisticsHandler` Should collect read/written bytes and speed with TrafficCounter, but doesn't.

`out HttpServerCodec` Encodes an HttpResponse into a ByteBuf.

`out RakeHandler` Accumulates written bytes, calculates speed as `speed = bytes*1000/sessionDuration`, also adds
filled record of session to queue.

### Shared resourses
LastAccessReport READ: `StatusHandler` READ/WRITE: `RakeHandler`

RedirectReport READ:`StatusHandler` READ/WRITE: `RedirectHandler`

RequestReport READ:`StatusHandler` READ/WRITE: `StatisticsHandler`

Screenshots
-----------
![alt tag](https://dl.dropboxusercontent.com/u/10930742/Job/hamsters/status.PNG)
![alt tag](https://dl.dropboxusercontent.com/u/10930742/Job/hamsters/ab.png)

