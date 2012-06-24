package ubu.inf.terminal.vista;

import ubu.inf.terminal.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Actividad principal que contiene todas las pestañas para el funcionamiento de la aplicación.
 * @author       David Herrero de la Peña
 * @author       Jonatan Santos Barrios
 * @see TabActivity
 * @uml.dependency   supplier="ubu.inf.terminal.vista.PestanaMainManual"
 * @uml.dependency   supplier="ubu.inf.terminal.vista.PestanaMainFav"
 */
public class MainSSH extends TabActivity{
  
	/**
	 * TabHost que contiene las pestañas.
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
     * Crea la pestaña de servidores favoritos.
     */
    private void creaTabFav(){
    	  TabHost.TabSpec spec;
          Intent intent;
    	 //pestaña favoritos
        intent = new Intent().setClass(this, PestanaMainFav.class);
        spec = tabHost.newTabSpec("pestanamainfav").setIndicator("favoritos", res.getDrawable(R.drawable.pestanamainfav));
        spec.setContent(intent);
        tabHost.addTab(spec);
    	 
    }
    /**
     * Crea la pestaña de conexión manual.
     */
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