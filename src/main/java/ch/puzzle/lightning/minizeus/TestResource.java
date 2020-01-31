package ch.puzzle.lightning.minizeus;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;


@Path("test")
@ApplicationScoped
public class TestResource {

    private volatile SseBroadcaster sseBroadcaster;
    private OutboundSseEvent.Builder eventBuilder;

    @Context
    Sse sse;

    @PostConstruct
    public void initSse() {
        this.sseBroadcaster = sse.newBroadcaster();
        this.eventBuilder = sse.newEventBuilder();
        sseBroadcaster.onClose(sseEventSink -> System.out.println("subscription closed"));
    }

    @GET
    @Path("event")
    @Produces(MediaType.TEXT_PLAIN)
    public Response produceEvent() {
        OutboundSseEvent sseEvent = eventBuilder.name("data")
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data("success")
                .build();
        this.sseBroadcaster.broadcast(sseEvent);
        return Response.ok("ok").build();
    }

    @GET
    @Path("subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink sseEventSink) {
        System.out.println("new subscription");
        this.sseBroadcaster.register(sseEventSink);
    }
}
