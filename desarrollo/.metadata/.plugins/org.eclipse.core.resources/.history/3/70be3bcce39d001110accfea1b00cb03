package ubu.inf.logica;

import ubu.inf.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MainSSH extends TabActivity{
  
	private TabHost tabHost;
	private Resources res;
	  /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tabHost = getTabHost();
        res = getResources(); 
        creaTabManual();
        creaTabFav();
       
        
    }
    private void creaTabFav(){
    	  TabHost.TabSpec spec;
          Intent intent;
    	 //pestaña favoritos
        intent = new Intent().setClass(this, PestanaMainFav.class);
        spec = tabHost.newTabSpec("pestanamainfav").setIndicator("favoritos", res.getDrawable(R.drawable.pestanamainfav));
        spec.setContent(intent);
        tabHost.addTab(spec);
    	 
    }
    private void creaTabManual(){
    	TabHost.TabSpec spec;
        Intent intent;
  	 //pestaña manual
      intent = new Intent().setClass(this, PestanaMainManual.class);
      spec = tabHost.newTabSpec("pestanamainmanual").setIndicator("manual", res.getDrawable(R.drawable.pestanamainmanual));
      spec.setContent(intent);
      tabHost.addTab(spec);
    }
    
}