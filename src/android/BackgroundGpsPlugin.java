package com.tenforwardconsulting.cordova.bgloc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import org.apache.cordova.*;
import org.json.JSONArray;

public class BackgroundGpsPlugin extends CordovaPlugin
{

    public static final String ACTION_START = "start";
    public static final String ACTION_STOP = "stop";
    public static final String ACTION_CONFIGURE = "configure";

    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext)
    {

        if (this.activity == null)
            this.activity = this.cordova.getActivity();

        if (action.equals(ACTION_CONFIGURE))
        {
            this.callback = callbackContext;
            return true;
        }
        else if (action.equals(ACTION_START))
        {
            this.isWatching = true;
            return startService();
        }
        else if (action.equals(ACTION_STOP))
        {
            this.isWatching = false;
            return stopService();
        }

        return true;
    }

    // TRACKER

    public void AddCoordinate(final LocationUpdateService.Coord c)
    {
        if (callback == null)
            return;

        try
        {
            PluginResult result = new PluginResult(PluginResult.Status.OK, c.toJson());
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }
        catch (Exception ex)
        {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage());
            result.setKeepCallback(true);
            callback.sendPluginResult(result);
        }
    }

    private LocationUpdateService bgService;
    private boolean isBound = false;
    private Messenger mService = null;
    private final Messenger mMessenger = new Messenger(new IncomingHandler());
    private Activity activity = null;
    private CallbackContext callback = null;
    private boolean isWatching = false;

    class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case LocationUpdateService.LOCATION_UPDATE:
                    AddCoordinate(((LocationUpdateService.Coord)msg.obj));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {

            mService = new Messenger(service);

            try
            {
                Message msg = Message.obtain(null, LocationUpdateService.REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            }
            catch (RemoteException e)
            {
            }

        }

        public void onServiceDisconnected(ComponentName className)
        {
            bgService = null;
        }
    };

    public boolean startService()
    {
        if (!isBound)
        {
            activity.bindService(new Intent(activity, LocationUpdateService.class), connection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }

        return isBound;
    }

    public boolean stopService()
    {
        if (isBound)
        {
            if (mService != null)
            {
                try
                {
                    Message msg = Message.obtain(null, LocationUpdateService.UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                }
                catch (RemoteException e)
                {
                }
            }
            // Detach our existing connection.
            activity.unbindService(connection);
            isBound = false;
        }


        return !isBound;
    }

    public void onDestroy()
    {
        stopService();
    }
}
