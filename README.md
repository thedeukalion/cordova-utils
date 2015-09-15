Native tools for Android/Cordova

###ShowDialog/ShowAlert:
```
if (window.plugins && window.plugins.nativeUtils)
{
  window.plugins.nativeUtils.showDialog(function(index)
  {
    if (index == 0)
    {
      console.log("You pressed Yes");
    }
    else if (index == 1)
    {
      console.log("You pressed No");
    }
  },
  function(error)
  {
  },
  {
    title: "Press yes or no",
    message: "Would you like to continue?",
    buttons: ["Yes", "No"]
  });
}
```

###ShowInput
```
if (window.plugins && window.plugins.nativeUtils)
{
  window.plugins.nativeUtils.showInput(function(result)
  {
    if (result.buttonIndex == 1)
    {
      console.log("You cancelled");
    }
    else
    {
      console.log("Your name is: " + result.input1);
    }
  },
  function(error)
  {
  },
  {
    title: "Enter your name",
    defaultText: "Your name here",
    buttonOk: "Continue",
    buttonCancel: "Cancel"
  });
}
```

###StatusBarSetColor
```
if (window.plugins && window.plugins.nativeUtils)
{
  // RED STATUS BAR
  window.plugins.nativeUtils.statusBarSetColor("#ff0000");

  // GREEN STATUS BAR
  window.plugins.nativeUtils.statusBarSetColor("#0000ff");

  // BLUE STATUS BAR
  window.plugins.nativeUtils.statusBarSetColor("#00ff00");

}
```

###Listeners

Native Tools for Android/IOS on Cordova (Especially IOS)

###LifeCycleCallback
```
if (window.plugins && window.plugins.nativeUtils)
{
  // RED STATUS BAR
  window.plugins.nativeUtils.setLifecycleCallback(function(state)
  {
    if (state == "Pause")
    {
      console.log("App has entered background");
    }
    else if (state == "Resume")
    {
      console.log("App has entered foreground");
    }
    else if (state == "Destroy")
    {
      console.log("App is being destroyed");
    }

  });

}
```
