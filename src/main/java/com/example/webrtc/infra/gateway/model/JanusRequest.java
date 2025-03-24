package com.example.webrtc.infra.gateway.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


/** janus doc 참고.....
 * The server root (/janus by default, but configurable), which you only POST to in order to create a Janus session;
 * The session endpoint (e.g., /janus/12345678, using the identifier retrieved with a previous create), which you either send a GET to (long poll for events and messages from plugins) or a POST (to create plugin handles or manipulate the session);
 * The plugin handle endpoint (e.g., /janus/12345678/98765432, appending the handle identifier to the session one) which you only send POST messages to (messages/negotiations for a plugin, handle manipulation), as all events related to this handle would be received in the session endpoint GET (the janus.js library would redirect the incoming messages to the right handle internally).
 * Messages and requests you can send to and receive from any of the above mentioned endpoints are described in the following chapters. In general, all messages share at least two fields:
 *
 * janus: the request/event (e.g., "create", "attach", "message", etc.);
 * transaction: a random string that the client can use to match incoming messages from the server (since, as explained in the Plugins documentation, all messages are asynchronous).
 * Different messages will of course add different information to this base syntax. Error message, instead, usually have these fields:
 *
 * janus: this would be "error";
 * transaction: this would be the transaction identifier of the request that failed;
 * error: a JSON object containing two fields:
 * code: a numeric error code, as defined in apierror.h;
 * reason: a verbose string describing the cause of the failure.
 */
@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JanusRequest<T> {
    private final String janus;
    private final String transaction;
    private final String plugin;

    private T body;
}
