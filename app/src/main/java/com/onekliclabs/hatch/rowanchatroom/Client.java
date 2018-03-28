package com.onekliclabs.hatch.rowanchatroom;



import java.io.IOException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
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
    public boolean loggedin = false;

    public static boolean connected = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;

    public static XMPPTCPConnection connection;
    public static String loginUser;
    public static String passwordUser;
    public static Client instance = null;
    public static ChatRoomActivity chat;

    Gson gson;
    DashBoardActivity context;

    private static MultiUserChat multiUserChat;
    private boolean chat_created = false;
    private String serverAddress;

    //public org.jivesoftware.smack.chat.Chat Mychat;
    MMessageListener mMessageListener;


    public Client(ChatRoomActivity chat, DashBoardActivity context, String serverAddress, String loginUser,
                  String passwordUser)
    {
        this.serverAddress = serverAddress;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
        this.context = context;
        this.chat = chat;
        init();

    }

    public static Client getInstance(ChatRoomActivity mainActivity,DashBoardActivity context, String server,
                                     String user, String pass) {

        instance = new Client(mainActivity, context, server, user, pass);

        return instance;

    }

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

    public void init()
    {
        gson = new Gson();
        chat.setClient(this);
        mMessageListener = new MMessageListener(context);
        initialiseConnection();

    }

    private void initialiseConnection()
    {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName(serverAddress);
        config.setHost(serverAddress);
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementResumptiodDefault(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        XMPPConnectionListener connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);
    }

    public void disconnect() {
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
                connection.disconnect();
            }

        }).start();
    }


    public void connect(final String caller)
    {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected synchronized Boolean doInBackground(Void... arg0)
            {
                if (connection.isConnected())
                    return false;
                isconnecting = true;

                try {
                    connection.connect();
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
                return isconnecting = false;
            }
        };
        connectionThread.execute();
    }

    public void login()
    {
        try {
            connection.login(loginUser, passwordUser);

        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
    }

    public void sendMessage(String chatMessage)
    {
        String body = gson.toJson(chatMessage);

        if (!chat_created)
        {
            chat_created = true;
        }

        final Message message = new Message();
        message.setBody(body);
        message.setType(Message.Type.chat);
        message.setFrom(loginUser+ "@ec2-54-198-216-41.compute-1.amazonaws.com");

        try
        {
            if (connection.isAuthenticated())
            {

                multiUserChat.sendMessage(message);
                Log.d("Message sent", message.getBody());

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

    public class XMPPConnectionListener implements ConnectionListener
    {
        @Override
        public void connected(final XMPPConnection connection)
        {
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable()
                {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "ConnectionCLosed!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionCLosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "ConnectionClosedOn Error!!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;
            chat_created = false;
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
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful()
        {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        Toast.makeText(context, "REConnected!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1)
        {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;

            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxStanzas(2);
            multiUserChat = manager.getMultiUserChat(
                    "rowanchat@conference.ec2-54-198-216-41.compute-1.amazonaws.com");

            // continue to try and connect until connected or exit
            while(!multiUserChat.isJoined())
            {
                try {
                    multiUserChat.join(loginUser, passwordUser, history,
                            SmackConfiguration.getDefaultPacketReplyTimeout());
                } catch (SmackException.NoResponseException e) {
                    Log.e("Error",e.getMessage());
                } catch (XMPPException.XMPPErrorException e) {
                    Log.e("Error",e.getMessage());
                } catch (SmackException.NotConnectedException e) {
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
            }
            multiUserChat.addMessageListener(mMessageListener);
            chat_created = false;


            if (multiUserChat.isJoined())
            {
                Log.d("xmpp", "Connected!");

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "Connected!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private class MMessageListener implements MessageListener
    {
        public MMessageListener(Context context)
        {

        }

        @Override
        public void processMessage(Message message)
        {
            Log.i("MyXMPP_MESSAGE_LISTENER", "Xmpp message received: '"
                    + message);
            String name = message.getFrom();
            name = name.substring(name.indexOf("/")+1,name.length());
            processMessage(message.getBody(),name);
        }

        private void processMessage(final String chatMessage, final String name)
        {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    if(chatMessage != null)
                    {
                        if(name.equals(loginUser))
                            chat.postReceivedMessage(chatMessage, "user",name);
                        else
                            chat.postReceivedMessage(chatMessage, "other",name);

                        Log.d(" Message Received ", chatMessage);
                    }

                }
            });
        }
    }


}
