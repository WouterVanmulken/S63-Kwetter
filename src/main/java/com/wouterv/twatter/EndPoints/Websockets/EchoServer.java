package com.wouterv.twatter.EndPoints.Websockets;

/**
 * Created by Wouter Vanmulken on 6-5-2017.
 */
import com.wouterv.twatter.Models.Tweet;
import com.wouterv.twatter.Service.TweetService;
import java.io.IOException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @ServerEndpoint gives the relative name for the end point
 * This will be accessed via ws://localhost:8080/EchoChamber/echo
 * Where "localhost" is the address of the host,
 * "EchoChamber" is the name of the package
 * and "echo" is the address to access this class from the server
 */
@Stateless
@ServerEndpoint("/echo")
public class EchoServer {
    @Inject
    TweetService service;
    @Context
    UriInfo uriInfo;
    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("aklsdjfalskjdf");

        String[] split = message.split(",");
        Tweet tweet = null;
        try {
            tweet = service.create(split[1], Integer.parseInt(split[0]));
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Message from " + session.getId() + ": " + message);
        try {
            session.getBasicRemote().sendText(tweet.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
    }
}