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
    }
};
