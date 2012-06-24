package ubu.inf.terminal.vista;

import ubu.inf.terminal.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Actividad principal que contiene todas las pesta�as para el funcionamiento de la aplicaci�n.
 * @author       David Herrero de la Pe�a
 * @author       Jonatan Santos Barrios
 * @see TabActivity
 * @uml.dependency   supplier="ubu.inf.terminal.vista.PestanaMainManual"
 * @uml.dependency   supplier="ubu.inf.terminal.vista.PestanaMainFav"
 */
public class MainSSH extends TabActivity{
  
	/**
	 * TabHost que contiene las pesta�as.
	 */
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
    /**
     * Crea la pesta�a de servidores favoritos.
     */
    private void creaTabFav(){
    	  TabHost.TabSpec spec;
          Intent intent;
    	 //pesta�a favoritos
        intent = new Intent().setClass(this, PestanaMainFav.class);
        spec = tabHost.newTabSpec("pestanamainfav").setIndicator("favoritos", res.getDrawable(R.drawable.pestanamainfav));
        spec.setContent(intent);
        tabHost.addTab(spec);
    	 
    }
    /**
     * Crea la pesta�a de conexi�n manual.
     */
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