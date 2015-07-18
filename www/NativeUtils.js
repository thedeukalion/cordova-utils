var exec = require("cordova/exec");
module.exports =
{

    showDialog: function(success, failure, config)
    {
      var
      title = config.title || "",
      message = config.message || "",
      buttonPositive = config.buttonPositive || "Yes",
      buttonNegative = config.buttonNegative || "No";

      exec(success || function() {},
           failure || function() {},
           'NativeUtils',
           'showDialog',
           [title, message, buttonPositive, buttonNegative]
      );
    };
};
