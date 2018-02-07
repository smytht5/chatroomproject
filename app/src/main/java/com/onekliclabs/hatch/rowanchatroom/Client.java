package com.onekliclabs.hatch.rowanchatroom;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Owner on 8/23/15.
 */
public class Client
{
    private  OnMessageReceived messageListener;
    private boolean mRun;

    private OutputStreamWriter out;
    private BufferedReader in;

    // message(s) received from server
    private String serverMessage;

    public Client(OnMessageReceived listener)
    {
        messageListener = listener;
    }

    public void stopClient()
    {
        mRun = false;
    }


    public void sendMessage(String message)
    {
        if (out != null)
        {
            try
            {
                System.out.println("here 4:" + message);
                out.write(message + "\n");
                out.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
            System.out.println("out equals null");

    }

    public void addLike(int index)
    {
        if (out != null)
        {
            try
            {
                out.write(index + "\n");
                out.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void removeLike(int index)
    {
        if (out != null)
        {
            try
            {
                out.write(index + "\n");
                out.flush();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void run()
    {

        mRun = true;

        // create new socket
        Socket s = new Socket();

        try
        {
            //Connect to socket
            InetSocketAddress sa = new InetSocketAddress("10.0.0.111",8881);
            s.connect(sa, 2000);

            //Get location longitude and latitude
            //Double longitude = tracker.getLongitude();
            //Double latitude = tracker.getLatitude();

            try
            {
                //Send message to server
                out = new OutputStreamWriter(s.getOutputStream());


                //Get messages sent to server
                InputStreamReader isr = new InputStreamReader(s.getInputStream());
                in = new BufferedReader(isr);

                // in this while the client listens for the messages sent by the
                // server
                while (mRun)
                {
                    serverMessage = in.readLine();

                    if (serverMessage != null && messageListener != null)
                    {
                        messageListener.onMessageReceived(serverMessage);
                    }

                    serverMessage = null;
                }

                Log.e("RESPONSE FROM SERVER", "S: Received Message: '"
                        + serverMessage + "'");
            } catch(Exception e)
            {
                Log.e("TCP", "S: Error", e);
            } finally
            {
                s.close();
            }

        }catch(IOException ioe)
        {
            Log.e("Code Error", ioe.getMessage());

            messageListener.onErrorMessageReceived();
        }
    }


    interface OnMessageReceived
    {
        void onMessageReceived(String message);

        void onErrorMessageReceived();

    }
}
