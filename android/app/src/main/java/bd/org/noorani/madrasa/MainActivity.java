package bd.org.noorani.madrasa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

/** Browser-backed app: uses the browser's secure sign-in session instead of embedding OAuth in a WebView. */
public class MainActivity extends Activity {
    private static final String SITE = "https://dhekuncha-noorani-madrasa.freshspice37.chatgpt.site";
    private TextView status;
    private final int green = Color.rgb(36,87,64);
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28),dp(36),dp(28),dp(36));
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(244,246,242),Color.rgb(225,235,215),Color.rgb(243,237,219)}));
        if(Build.VERSION.SDK_INT >= 30) root.setOnApplyWindowInsetsListener((view,insets)->{
            android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
            view.setPadding(dp(28)+bars.left,dp(24)+bars.top,dp(28)+bars.right,dp(24)+bars.bottom);
            return insets;
        });
        ImageView icon = new ImageView(this); icon.setImageResource(bd.org.noorani.madrasa.R.drawable.ic_madrasa);
        LinearLayout.LayoutParams iconParams=new LinearLayout.LayoutParams(dp(90),dp(90)); iconParams.bottomMargin=dp(24); root.addView(icon,iconParams);
        TextView title=text("নূরানী মাদ্রাসা",28,true); root.addView(title);
        TextView full=text("ঢেকুঞ্চা দিয়ল চারমাথা বাজার\nনূরানী হাফেজিয়া মাদ্রাসা",17,false); gap(full,14);root.addView(full);
        TextView slogan=text("ইলমের আলোয় আলোকিত হোক আগামী",14,false);gap(slogan,15);root.addView(slogan);
        Button open=new Button(this);open.setText("ওয়েবসাইট খুলুন  ↗");open.setTextColor(Color.WHITE);open.setTextSize(16);open.setAllCaps(false);
        GradientDrawable bg=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(103,151,115),green,Color.rgb(133,145,84)});bg.setCornerRadius(dp(30));bg.setStroke(dp(1),Color.rgb(169,193,143));open.setBackground(bg);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp(56));params.topMargin=dp(28);root.addView(open,params);open.setOnClickListener(v->openSite());
        status=text("ইন্টারনেট সংযোগ প্রয়োজন। প্রাইভেট সংস্করণে প্রথমবার আপনার অনুমোদিত ChatGPT অ্যাকাউন্ট দিয়ে লগইন করুন।",13,false);gap(status,22);root.addView(status);
        TextView info=text("ফলাফল · রুটিন · নোটিশ · ভর্তি",13,true);gap(info,25);root.addView(info);
        setContentView(root);
        if(savedInstanceState==null && online()) root.post(this::openSite);
    }
    private boolean online(){
        ConnectivityManager manager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if(manager==null)return false;
        NetworkCapabilities cap=manager.getNetworkCapabilities(manager.getActiveNetwork());
        return cap!=null&&cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
    private void openSite(){
        if(!online()){status.setText("ইন্টারনেট সংযোগ পাওয়া যায়নি। সংযোগ চালু করে আবার ‘ওয়েবসাইট খুলুন’ চাপুন।");return;}
        try{
            CustomTabsIntent tab=new CustomTabsIntent.Builder()
              .setDefaultColorSchemeParams(new CustomTabColorSchemeParams.Builder().setToolbarColor(green).setNavigationBarColor(Color.rgb(244,246,242)).build())
              .setShowTitle(true).setUrlBarHidingEnabled(true).build();
            tab.launchUrl(this,Uri.parse(SITE));
        }catch(ActivityNotFoundException unavailable){
            try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(SITE)));}
            catch(ActivityNotFoundException missing){new AlertDialog.Builder(this).setTitle("ব্রাউজার প্রয়োজন").setMessage("ফোনে Chrome বা অন্য একটি HTTPS সমর্থিত ব্রাউজার চালু করে আবার চেষ্টা করুন।").setPositiveButton("ঠিক আছে",null).show();}
        }
    }
    private TextView text(String value,int size,boolean bold){TextView t=new TextView(this);t.setText(value);t.setTextSize(size);t.setTextColor(green);t.setGravity(Gravity.CENTER);t.setLineSpacing(dp(5),1);t.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));return t;}
    private void gap(View view,int top){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);p.topMargin=dp(top);view.setLayoutParams(p);}
    private int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
}
