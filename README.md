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
