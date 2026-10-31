package bd.org.noorani.madrasa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.Base64;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.webkit.WebViewAssetLoader;
import java.io.OutputStream;

/** Embedded offline app. It never navigates to the hosted website to display its pages. */
public class MainActivity extends Activity {
 private static final String HOME="https://appassets.androidplatform.net/assets/www/index.html";
 private static final int PICK_FILE=10, SAVE_FILE=11;
 private WebView web;
 private ValueCallback<Uri[]> fileCallback;
 private byte[] pendingFile;
 @SuppressLint("SetJavaScriptEnabled")
 @Override public void onCreate(Bundle state){
  super.onCreate(state);
  FrameLayout root=new FrameLayout(this);root.setBackgroundColor(Color.rgb(244,246,242));
  web=new WebView(this);web.setBackgroundColor(Color.rgb(244,246,242));
  WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);settings.setAllowFileAccess(false);settings.setAllowContentAccess(true);settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);settings.setSupportMultipleWindows(false);
  WebViewAssetLoader loader=new WebViewAssetLoader.Builder().addPathHandler("/assets/",new WebViewAssetLoader.AssetsPathHandler(this)).build();
  web.setWebViewClient(new WebViewClient(){
   @Override public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest request){return loader.shouldInterceptRequest(request.getUrl());}
   @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
    Uri u=request.getUrl();if("https".equals(u.getScheme())&&"appassets.androidplatform.net".equals(u.getHost())&&u.getPath()!=null&&u.getPath().startsWith("/assets/www/"))return false;
    if(!request.isForMainFrame())return true;
    String scheme=u.getScheme();if("https".equals(scheme)||"http".equals(scheme)||"tel".equals(scheme)||"mailto".equals(scheme)){
     try{startActivity(new Intent(Intent.ACTION_VIEW,u));}catch(ActivityNotFoundException e){Toast.makeText(MainActivity.this,"এই লিংক খোলার উপযুক্ত অ্যাপ পাওয়া যায়নি",Toast.LENGTH_LONG).show();}
    }
    return true;
   }
  });
  web.setWebChromeClient(new WebChromeClient(){
   @Override public boolean onShowFileChooser(WebView view,ValueCallback<Uri[]> callback,FileChooserParams params){
    if(fileCallback!=null)fileCallback.onReceiveValue(null);fileCallback=callback;
    try{Intent i=params.createIntent();i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK_FILE);}
    catch(ActivityNotFoundException e){fileCallback.onReceiveValue(null);fileCallback=null;Toast.makeText(MainActivity.this,"ফাইল বাছাই করার অ্যাপ পাওয়া যায়নি",Toast.LENGTH_LONG).show();}
    return true;
   }
   @Override public boolean onJsConfirm(WebView view,String url,String message,JsResult result){
    new AlertDialog.Builder(MainActivity.this).setMessage(message).setPositiveButton("হ্যাঁ",(d,w)->result.confirm()).setNegativeButton("বাতিল",(d,w)->result.cancel()).setOnCancelListener(d->result.cancel()).show();return true;
   }
  });
  web.addJavascriptInterface(new NativeFiles(),"MadrasaNative");
  root.addView(web,new FrameLayout.LayoutParams(-1,-1));
  if(Build.VERSION.SDK_INT>=30)root.setOnApplyWindowInsetsListener((view,insets)->{android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars());view.setPadding(bars.left,bars.top,bars.right,bars.bottom);return insets;});
  setContentView(root);web.loadUrl(HOME);
 }
 public final class NativeFiles {
  @JavascriptInterface public void saveFile(String name,String mime,String encoded){
   if(encoded==null||encoded.length()>115000000){runOnUiThread(()->Toast.makeText(MainActivity.this,"ফাইলটি অনেক বড়",Toast.LENGTH_LONG).show());return;}
   final byte[] bytes;try{bytes=Base64.decode(encoded,Base64.DEFAULT);}catch(IllegalArgumentException e){return;}
   runOnUiThread(()->{if(pendingFile!=null){Toast.makeText(MainActivity.this,"আগের ফাইল সংরক্ষণ শেষ করুন",Toast.LENGTH_SHORT).show();return;}pendingFile=bytes;
    Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);intent.addCategory(Intent.CATEGORY_OPENABLE);intent.setType(mime!=null&&mime.matches("[a-zA-Z0-9.+-]+/[a-zA-Z0-9.+-]+")?mime:"application/octet-stream");intent.putExtra(Intent.EXTRA_TITLE,name==null?"Madrasa-file":name.replaceAll("[/\\\\]","_"));
    try{startActivityForResult(intent,SAVE_FILE);}catch(ActivityNotFoundException e){pendingFile=null;Toast.makeText(MainActivity.this,"ফাইল সংরক্ষণের অ্যাপ পাওয়া যায়নি",Toast.LENGTH_LONG).show();}
   });
  }
  @JavascriptInterface public void printPage(){runOnUiThread(()->{PrintManager pm=(PrintManager)getSystemService(PRINT_SERVICE);if(pm!=null)pm.print("Noorani Madrasa",web.createPrintDocumentAdapter("Madrasa"),new PrintAttributes.Builder().build());});}
 }
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);
  if(request==PICK_FILE&&fileCallback!=null){fileCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(result,data));fileCallback=null;}
  if(request==SAVE_FILE){byte[] bytes=pendingFile;pendingFile=null;if(result==RESULT_OK&&data!=null&&data.getData()!=null&&bytes!=null){Uri uri=data.getData();new Thread(()->{try(OutputStream out=getContentResolver().openOutputStream(uri)){if(out==null)throw new java.io.IOException();out.write(bytes);runOnUiThread(()->Toast.makeText(this,"ফাইল সংরক্ষিত হয়েছে",Toast.LENGTH_SHORT).show());}catch(Exception e){runOnUiThread(()->Toast.makeText(this,"ফাইল সংরক্ষণ হয়নি। আবার চেষ্টা করুন।",Toast.LENGTH_LONG).show());}}).start();}}
 }
 @Override public void onBackPressed(){web.evaluateJavascript("location.hash",hash->{if(hash!=null&&!hash.equals("\"\"")&&!hash.equals("\"#home\"")){web.evaluateJavascript("location.hash='home'",null);}else{new AlertDialog.Builder(this).setMessage("অ্যাপ বন্ধ করবেন?").setPositiveButton("হ্যাঁ",(d,w)->finish()).setNegativeButton("না",null).show();}});}
 @Override protected void onDestroy(){if(fileCallback!=null)fileCallback.onReceiveValue(null);if(web!=null){web.removeJavascriptInterface("MadrasaNative");web.destroy();}super.onDestroy();}
}
