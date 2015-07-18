package com.nativeutils;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;

import java.util.Hashtable;
import java.util.UUID;

public class NativeUtils extends CordovaPlugin
{

    private Activity activity = null;
    private Hashtable<String, CallbackContext> callbackDialogs = new Hashtable<>();

    public static final String ACTION_SHOWDIALOG = "showDialog";

    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext)
    {

        if (this.activity == null)
            this.activity = this.cordova.getActivity();

        if (action.equals(ACTION_SHOWDIALOG))
        {
          try
          {
            String title = data.getString(0);
            String message = data.getString(1);
            String positiveButton = data.getString(2);
            String negativeButton = data.getString(3);

            String id = UUID.randomUUID().toString();
            callbackContext.put(id, callbackContext);
            showDialog(id, title, message, positiveButton, negativeButton);

          }
          catch (JSONException e)
          {
            callbackContext.error("authToken/url required as parameters: " + e.getMessage());
          }

          return true;
        }

        return false;
    }

    private void showDialog(final String id, String title, String message, String positiveButton, String negativeButton)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);

      builder
      .setMessage(message)
      .setTitle(title)
      .setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
      {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {
            if (callbackDialogs.contains(id))
            {
              CallbackContext callback = callbackDialogs.get(id);
              callbackContext.remove(id);

              try
              {

                JSONObject response = new JSONObject();
                response.put("Positive", true);
                response.put("Dismissed", false);

                PluginResult result = new PluginResult(PluginResult.Status.OK, response);
                result.setKeepCallback(false);
                callback.sendPluginResult(result);
              }
              catch (Exception ex)
              {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage());
                result.setKeepCallback(false);
                callback.sendPluginResult(result);
              }
            }

          }
      })
      .setNegativeButton(negativeButton, new DialogInterface.OnClickListener()
      {
          @Override
          public void onClick(DialogInterface dialog, int which)
          {

            if (callbackDialogs.contains(id))
            {
              CallbackContext callback = callbackDialogs.get(id);
              callbackContext.remove(id);

              try
              {

                JSONObject response = new JSONObject();
                response.put("Positive", false);
                response.put("Dismissed", false);

                PluginResult result = new PluginResult(PluginResult.Status.OK, response);
                result.setKeepCallback(false);
                callback.sendPluginResult(result);
              }
              catch (Exception ex)
              {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage());
                result.setKeepCallback(false);
                callback.sendPluginResult(result);
              }
            }
          }
      });

      AlertDialog dialog = builder.create();
      dialog.show();
    }
}
