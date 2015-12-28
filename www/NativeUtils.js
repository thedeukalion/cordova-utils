var exec = require("cordova/exec");
module.exports =
{

    showDialog: function(success, failure, config)
    {
      var
      title = config.title || "",
      message = config.message || "",
      buttons = config.buttons || [];

      exec(success || function() {},
           failure || function() {},
           'NativeUtils',
           'showDialog',
           [title, message, buttons]
      );
    },

    showInput: function(success, failure, config)
    {
      var
      title = config.title || "",
      defaultText = config.defaultText || "",
      buttonOk = config.buttonOK || "Ok",
      buttonCancel = config.buttonCancel || "Cancel";

      exec(success || function() {},
           failure || function() {},
           'NativeUtils',
           'showInput',
           [title, defaultText, buttonOk, buttonCancel]
      );
    },

    statusBarSetColor: function(color, success, failure)
    {
      exec(success || function() {},
           failure || function() {},
           'NativeUtils',
           'statusBarSetColor',
           [color]
      );
    },

    getDensity: function(success, failure)
    {
      exec(success || function() {},
           failure || function() {},
           'NativeUtils',
           'getDensity',
           []
      );
    }
};
