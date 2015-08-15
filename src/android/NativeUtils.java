package com.nativeutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.view.LayoutInflater;
import android.widget.Toast;

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

    public int getResourceId(String name, String type)
    {
      return this.activity.getResources().getIdentifier(name, type, activity.getPackageName());
    }

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
            JSONArray buttons = data.getJSONArray(2);

            String[] b = new String[buttons.length()];

            for (int i=0; i < buttons.length(); i++)
            {
              b[i] = buttons.getString(i);
            }

            String id = UUID.randomUUID().toString();
            ShowDialog(callbackContext, id, title, message, b, b.length > 2 ? true : false);

          }
          catch (JSONException e)
          {
            callbackContext.error(e.getMessage());
          }

          return true;
        }

        return false;
    }

    public void ShowDialog(final CallbackContext cb, final String id, String title, String message, String[] buttons, boolean vertical)
    {
        final Context context = activity;

        if (context == null)
            return;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        int resDialog = getResourceId("dialog", "layout");
        int resButton = getResourceId("dialog_button", "layout");

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout container =  (LinearLayout)inflater.inflate(resDialog, null);

        TextView t = (TextView)container.getChildAt(0);
        t.setText(title);

        TextView m = (TextView)container.getChildAt(1);
        m.setText(message);

        LinearLayout b = (LinearLayout)container.getChildAt(3);

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
                int index = Integer.parseInt(v.getTag().toString());

                try
                {
                  cb.success(index);
                }
                catch (Exception ex)
                {
                  cb.error(ex.getMessage());
                }

                dialog.setOnDismissListener(null);
                dialog.dismiss();
            }
        };

        for (int i=0; i < buttons.length; i++)
        {

            Button button = (Button)inflater.inflate(resButton, null);

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
            button.setTag(i);
            button.setOnClickListener(clickListener);

            b.addView(button);
        }

        dialog.setContentView(container);
        dialog.setOnDismissListener(new Dialog.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
              try
              {
                cb.success(-1);
              }
              catch (Exception ex)
              {
                cb.error(ex.getMessage());
              }
            }
        });

        t.measure(0, 0);
        b.measure(0, 0);
        container.measure(0, 0);

        dialog.show();
    }
}
