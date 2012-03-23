package ubu.itig;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class ModuloSSHActivity extends TabActivity{
    /** Called when the activity is first created. */
	private TabHost tabHost;
	private Resources res;
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
    	 //pesta�a favoritos
        intent = new Intent().setClass(this, PestanaMainFav.class);
        spec = tabHost.newTabSpec("pestanamainfav").setIndicator("favoritos", res.getDrawable(R.drawable.pestanamainfav));
        spec.setContent(intent);
        tabHost.addTab(spec);
    	 
    }
    private void creaTabManual(){
    	TabHost.TabSpec spec;
        Intent intent;
  	 //pesta�a manual
      intent = new Intent().setClass(this, PestanaMainManual.class);
      spec = tabHost.newTabSpec("pestanamainmanual").setIndicator("manual", res.getDrawable(R.drawable.pestanamainmanual));
      spec.setContent(intent);
      tabHost.addTab(spec);
    }
    
}