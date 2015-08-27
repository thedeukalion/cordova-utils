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
import android.widget.EditText;
import android.widget.Button;
import android.view.LayoutInflater;
import android.widget.Toast;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NativeUtils extends CordovaPlugin
{

    private Activity activity = null;

    public static final String ACTION_SHOWDIALOG = "showDialog";
    public static final String ACTION_SHOWINPUT = "showInput";
    public static final String ACTION_STATUSBAR_SETCOLOR = "statusBarSetColor";

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

            ShowDialog(callbackContext, title, message, b, b.length > 2 ? true : false);

          }
          catch (JSONException e)
          {
            callbackContext.error(e.getMessage());
          }

          return true;
        }
        else if (action.equals(ACTION_SHOWINPUT))
        {
            try
            {
              String title = data.getString(0);
              String defaultText = data.getString(1);
              String okButton = data.getString(2);
              String cancelButton = data.getString(3);

              ShowInput(callbackContext, title, defaultText, okButton, cancelButton);

            }
            catch (JSONException e)
            {
              callbackContext.error(e.getMessage());
            }

            return true;
        }
        else if (action.equals(ACTION_STATUSBAR_SETCOLOR))
        {
          try
          {
            String color = data.getString(0);
            StatusBarSetColor(color);

          }
          catch (JSONException e) { }
        }

        return false;
    }

    public void ShowDialog(final CallbackContext cb, String title, String message, String[] buttons, boolean vertical)
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

    public void ShowInput(final CallbackContext cb, String title, String defaultText, String okButton, String cancelButton)
    {
        final Context context = activity;

        if (context == null)
            return;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        int resDialog = getResourceId("dialog_input", "layout");
        int resButton = getResourceId("dialog_button", "layout");

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout container =  (LinearLayout)inflater.inflate(resDialog, null);

        final TextView t = (TextView)container.getChildAt(0);
        t.setText(title);

        final EditText e = (EditText)container.getChildAt(1);
        e.setText(defaultText);

        LinearLayout b = (LinearLayout)container.getChildAt(2);

        float buttonWeight = 0.5f;

        View.OnClickListener clickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int id = Integer.parseInt(v.getTag().toString());

                if (id == 0)
                {
                  try
                  {
                      JSONObject obj = new JSONObject();
                      obj.put("Cancelled", false);
                      obj.optString("Input", e.getText().toString());

                      cb.success(obj);
                  }
                  catch (JSONException ex)
                  {
                    cb.error(ex.getMessage());
                  }

                }
                else
                {
                  try
                  {
                      JSONObject obj = new JSONObject();
                      obj.put("Cancelled", true);

                      cb.success(obj);
                  }
                  catch (JSONException ex)
                  {
                    cb.error(ex.getMessage());
                  }

                }

                dialog.setOnDismissListener(null);
                dialog.dismiss();
            }
        };


        LinearLayout.LayoutParams layoutParams = null;
        layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.weight = buttonWeight;

        Button buttonOK = (Button)inflater.inflate(resButton, null);
        buttonOK.setLayoutParams(layoutParams);
        buttonOK.setText(okButton);
        buttonOK.setTag(0);
        buttonOK.setOnClickListener(clickListener);
        b.addView(buttonOK);

        Button buttonCancel = (Button)inflater.inflate(resButton, null);
        buttonCancel.setLayoutParams(layoutParams);
        buttonCancel.setText(cancelButton);
        buttonCancel.setTag(1);
        buttonCancel.setOnClickListener(clickListener);
        b.addView(buttonCancel);

        dialog.setContentView(container);
        dialog.setOnDismissListener(new Dialog.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
              try
              {
                  JSONObject obj = new JSONObject();
                  obj.put("Cancelled", true);
                  cb.success(obj);
              }
              catch (JSONException ex)
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

    public void StatusBarSetColor(String color)
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            if (color != null && !color.isEmpty())
            {
                try
                {
                    int c = Color.parseColor(color);

                    final Window window = this.activity.getWindow();

                    window.clearFlags(0x04000000); // SDK 19: WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(0x80000000); // SDK 21: WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    window.getClass().getDeclaredMethod("setStatusBarColor", int.class).invoke(window, Color.parseColor(color));
                }
                catch (Exception ex) { }
            }
        }
    }

}
