package com.nativeutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.LayoutInflater;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            JSONArray buttons = data.getString(2);

            String[] b = new String[buttons.length()];

            for (int i=0; i < buttons.length(); i++)
            {
              b[i] = buttons.getString(i);
            }

            String id = UUID.randomUUID().toString();
            callbackDialogs.put(id, callbackContext);
            showDialog(id, title, message, buttons);

          }
          catch (JSONException e)
          {
            callbackContext.error(e.getMessage());
          }

          return true;
        }

        return false;
    }

    public void ShowDialog(final UUID id, String title, String message, String[] buttons)
    {
      ShowDialog(id, title, message, buttons, buttons.length > 2 ? true : false);
    }

    public void ShowDialog(final UUID id, String title, String message, String[] buttons, boolean vertical)
    {
        final Context context = activity;

        if (context == null)
            return;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View container =  inflater.inflate(com.nativeutils.R.layout.dialog, null);

        TextView t = (TextView)container.findViewById(com.nativeutils.R.id.title);
        t.setText(title);

        TextView m = (TextView)container.findViewById(com.nativeutils.R.id.message);
        m.setText(message);

        LinearLayout b = (LinearLayout)container.findViewById(com.nativeutils.R.id.buttons);

        if (vertical)
        {
            b.setOrientation(LinearLayout.VERTICAL);
        }
        else
        {
            b.setOrientation(LinearLayout.HORIZONTAL);
        }

        float buttonWeight = (1f / (float)buttons.length);

        View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int index = v.getId();

                DialogResult(id, index);

                dialog.setOnDismissListener(null);
                dialog.dismiss();
            }
        };

        for (int i=0; i < buttons.length; i++)
        {

            Button button = (Button)inflater.inflate(com.nativeutils.R.layout.dialog_button, null);

            LinearLayout.LayoutParams layoutParams = null;

            if (vertical)
            {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            else
            {
                layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.weight = buttonWeight;
            }

            button.setLayoutParams(layoutParams);
            button.setText(buttons[i]);
            button.setId(i);
            button.setOnClickListener(clickListener);

            b.addView(button);
        }

        dialog.setContentView(container);
        dialog.setOnDismissListener(new Dialog.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
              DialogResult(id, -1);
            }
        });

        t.measure(0, 0);
        b.measure(0, 0);
        container.measure(0, 0);

        dialog.show();
    }

    private void DialogResult(final UUID id, int index)
    {
        if (callbackDialogs.contains(id))
        {
          CallbackContext callback = callbackDialogs.get(id);
          callbackDialogs.remove(id);

          try
          {

            JSONObject response = new JSONObject();
            response.put("Index", index);

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

    /*
    private void showDialog(final String id, String title, String message, String positiveButton, String negativeButton)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);

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
              callbackDialogs.remove(id);

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
              callbackDialogs.remove(id);

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
    */
}
