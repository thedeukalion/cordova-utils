Native tools for Android/Cordova

###ShowDialog/ShowAlert:
```
if (window.plugins && window.plugins.nativeUtils)
{
  window.plugins.nativeUtils.showDialog(function(index)
  {
    console.log("Button Index: " + index);
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
