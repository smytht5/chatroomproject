package com.onekliclabs.hatch.rowanchatroom;



import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager.AutoReceiptMode;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;


public class Client
{
    public boolean loggedin = false;                    // returns true if logged in

    public static boolean connected = false;
    public static boolean isconnecting = false;         // return true if this client is in the act of connecting
    public static boolean isToasted = true;
    public static XMPPTCPConnection connection;         // client connection
    protected static String loginUser;                  // user name
    protected static String passwordUser;               // user password
    public static ChatRoomActivity chat;                // activity to upload messages to

    Gson gson;
    LoginActivity context;                              // base activity

    private static MultiUserChat multiUserChat;         // group chat room
    private String serverAddress;                       // address of the server



    public Client(LoginActivity context, String serverAddress, String loginUser,
                  String passwordUser)
    {
        this.serverAddress = serverAddress;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
        this.context = context;
        gson = new Gson();
        mMessageListener = new MMessageListener();
        dMessageListener = new DMessageListener();
        initialiseConnection();
    }

    public org.jivesoftware.smack.chat.Chat directChat; // direct message creator
    private MMessageListener mMessageListener;          // listens for incoming messages from group chat
    private DMessageListener dMessageListener;          // listens for incoming messages from direct message

    // load connection manager
    static
    {
        try
        {
            Class.forName("org.jivesoftware.smack.ReconnectionManager");
        } catch (ClassNotFoundException ex)
        {
            // problem loading reconnection manager
        }
    }

    /**
     * Configure XMPP connection data including: server address, port number,
     * and security settings.
     */
    private void initialiseConnection()
    {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setDebuggerEnabled(false);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        try {
            TLSUtils.acceptAllCertificates(config);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        config.setServiceName(serverAddress);
        config.setHost(serverAddress);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);

        // declare XMPP connection
        connection = new XMPPTCPConnection(config.build());
        ReconnectionManager rmanager = ReconnectionManager.getInstanceFor(connection);
        rmanager.enableAutomaticReconnection();
        rmanager.setEnabledPerDefault(true);
    }



    /**
     * Use XMPP connection to Log into server
     */
    public void login()
    {
        if (!isLoggedIn())
        {
            try
            {
                connection.login(loginUser, passwordUser);

            } catch (XMPPException | SmackException | IOException e)
            {
                Log.e("Log in Error 1", e.getMessage());
            } catch (Exception e)
            {
                Log.e("Log in Error 2", e.getMessage());
            }

            // add connection listener
            XMPPConnectionListener connectionListener = new XMPPConnectionListener();
            connection.addConnectionListener(connectionListener);
        }
        return;
    }

    public boolean isLoggedIn()
    {
        return connection.isAuthenticated();
    }


    public void addAccount()
    {

        AccountManager accountManager = AccountManager.getInstance(connection);

        try {
            accountManager.createAccount(loginUser, passwordUser);
            accountManager.sensitiveOperationOverInsecureConnection(false);
        } catch (SmackException.NoResponseException e) {
            Log.e("Account Creation 1", e.getMessage());

        } catch (XMPPException.XMPPErrorException e) {
            Log.e("Account Creation 2", e.getMessage());

        } catch (NotConnectedException e) {
            Log.e("Account Creation 3", e.getMessage());

        }
    }

    /**
     * Disconnect from server.
     */
    public void disconnectFromServer()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                connection.disconnect();
            }

        }).start();
    }

    /**
     * Disconnect from Group Chat Room
     */
    public void disconnectFromMultiChat() {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    multiUserChat.leave();
                    Log.d("MultiChat", "room left");
                } catch (NotConnectedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    /**
     * Check for XMPP connection
     * @return true if client is connected to server; false otherwise
     */
    public boolean isConnected()
    {
        return connection.isConnected();
    }

    /**
     * Check for MultiUserChat room has been joined
     * @return true if client is connected to server; false otherwise
     */
    public boolean isJoined(){return multiUserChat.isJoined();}


    /**
     * Connects this client to server using AsyncTask
     * @param caller Class calling this Method
     */
    public void connect(final String caller)
    {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0)
            {
                // if already connected, return
                if (connection.isConnected())
                    return false;

                // set client is connecting
                isconnecting = true;

                // use XMPP connection and connect to server
                try {
                    connection.connect();

                    // listen for packet response from server
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener()
                    {

                        @Override
                        public void onReceiptReceived(final String fromid,
                                                      final String toid, final String msgid,
                                                      final Stanza packet)
                        {
                            // do nothing with responded packet
                        }
                    });
                    connected = true;

                } catch (IOException e)
                {
                    Log.e("(" + caller + ") ",  e.getMessage());
                } catch (SmackException e)
                {
                    Log.e("(" + caller + ") ", e.getMessage());
                } catch (XMPPException e)
                {
                    Log.e("(" + caller + ") ",  e.getMessage());;
                }
                // connecting is complete
                return isconnecting = false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean)
            {
                super.onPostExecute(aBoolean);
                addAccount();
                login();
            }
        };
        connectionThread.execute();
    }


    /**
     * Use XMPP connection to connect to a created group chat room
     * @param activity associated with posting messages
     * @param room name of MultiUserChat room to be connected to
     */
    public void joinMultiChat(ChatRoomActivity activity, String room)
    {
        chat = activity;
        chat.setClient(this);

        // manager for group chat
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        // retrieves and manages message history of group chat
        DiscussionHistory history = new DiscussionHistory();
        // only allow 2 messages to be loaded from history when group is joined
        history.setMaxStanzas(2);
        multiUserChat = manager.getMultiUserChat(room);

        // continue to try and connect until connected or exit

        try {
            // joins group chat room
            // -- To do -- change loginUser to change user name
            multiUserChat.join(loginUser, passwordUser, history,
                    SmackConfiguration.getDefaultPacketReplyTimeout());
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException |
                SmackException.NotConnectedException e) {
            Log.e("Error",e.getMessage());
        }
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }).start();

        multiUserChat.addMessageListener(mMessageListener);
    }

    /**
     * Use XMPP connection to connect to a created group chat room
     * @param activity associated with posting messages
     * @param room name of MultiUserChat room to be connected to
     */
    public void joinMultiChat(ChatRoomActivity activity, String room, String newNickName)
    {
        chat = activity;
        chat.setClient(this);

        // manager for group chat
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        // retrieves and manages message history of group chat
        DiscussionHistory history = new DiscussionHistory();
        // only allow 2 messages to be loaded from history when group is joined
        history.setMaxStanzas(2);
        multiUserChat = manager.getMultiUserChat(room);

        // continue to try and connect until connected or exit

        try {
            // joins group chat room
            // -- To do -- change loginUser to change user name
            multiUserChat.join(newNickName, passwordUser, history,
                    SmackConfiguration.getDefaultPacketReplyTimeout());
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException |
                SmackException.NotConnectedException e) {
            Log.e("Error",e.getMessage());
        }
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }).start();

        multiUserChat.addMessageListener(mMessageListener);
    }



    /**
     * Constructs and sends a message to the group chat room
     * @param chatMessage the message user wants to send to group
     */
    public void sendMultiChatMessage(String chatMessage)
    {
        // convert string to json
        String body = gson.toJson(chatMessage);

        // construct message to send to group chat
        final Message message = new Message();
        message.setBody(body);
        message.setType(Message.Type.chat);
        message.setFrom(loginUser + "@"
                + context.getString(R.string.server));

        try
        {
            if (connection.isAuthenticated())
            {
                multiUserChat.sendMessage(message);
                Log.d("xmpp.SendMessage()", "msg Sent: " + message.getBody());

            } else {

                login();
            }
        } catch (NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.SendMessage()",
                    "msg Not sent!" + e.getMessage());
        }

    }

    /**
     * Constructs and sends a direct message to specified user
     * @param chatMessage the message user wants to send to group
     * @param to reciever user that message is to be delivered to
     */
    public void sendDirectMessage(String chatMessage, String to)
    {
        // convert string to json
        String body = gson.toJson(chatMessage);

        // declare user to connect to
        directChat = ChatManager.getInstanceFor(connection).createChat(
                to + "@"
                        + context.getString(R.string.server),
                dMessageListener);

        // construct message
        final Message message = new Message(to);
        message.setBody(body);
        message.setType(Message.Type.chat);

        try
        {
            if (connection.isAuthenticated())
            {
                directChat.sendMessage(message);
                Log.d("xmpp.SendMessage()", "msg Sent: " + message.getBody());

            } else {

                login();
            }
        } catch (NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.SendMessage()",
                    "msg Not sent!" + e.getMessage());
        }

    }

    /**
     * This is the XMPP connection listener class, this class listens and reports;
     * connection to the server disconnection from the server,
     * and reconnection to the server
     */
    public class XMPPConnectionListener implements ConnectionListener
    {
        @Override
        public void connected(final XMPPConnection connection)
        {
            connected = true;
        }

        @Override
        public void connectionClosed() {
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            loggedin = false;

        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;
            loggedin = false;

        }

        @Override
        public void reconnectingIn(int arg0)
        {
            Log.d("xmpp", "Reconnectingin " + arg0);
            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0)
        {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(context, "ReconnectionFailed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful()
        {
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1)
        {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;
        }
    }

    /**
     * This is the MultiChat Message listener class, this class listens for incoming
     * messages and processes those messages
     */
    private class MMessageListener implements MessageListener
    {

        @Override
        public void processMessage(Message message)
        {
            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);
            String name = message.getFrom();
            name = name.substring(name.indexOf("/")+1,name.length());
            processMessage(message.getBody(),name);
        }

        /**
         * Takes the received message and post it to the chat room window
         * @param chatMessage message to add to view
         * @param name user this messages was sent by
         */
        private void processMessage(final String chatMessage, final String name)
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    try{
                        if(chatMessage != null)
                        {
                            if(name.equals(loginUser))
                                chat.postReceivedMessage(chatMessage, "user",name);
                            else
                                chat.postReceivedMessage(chatMessage, "other",name);

                            Log.d(" Message Received ", chatMessage);
                        }
                    }catch (Exception e)
                    {
                        Log.e("Exception", e.getMessage());
                    }

                }
            });
        }
    }

    /**
     * This is the Direct Message listener class, this class listens for incoming
     * direct messages and processes those messages
     */
    private class DMessageListener implements ChatMessageListener {

        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat,
                                   final Message message) {
            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);

            if (message.getType() == Message.Type.chat
                    && message.getBody() != null) {
                final String chatMessage = gson.fromJson(
                        message.getBody(), String.class);

                processMessage(chatMessage);
            }
        }

        private void processMessage(final String chatMessage) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    // -- To do -- add location to post received direct message
                }
            });
        }
    }
    //returns the original username
    public String getOriginalUserName(){
        return loginUser;
    }
}
