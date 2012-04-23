package ubu.inf.logica;

import ubu.inf.R;
import ubu.inf.R.layout;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Clase principal que contiene las dos pesta�as principales de la aplicaci�n.
 * 
 * @author David Herrero
 * @author Jonatan Santos
 * 
 *@version 1.0
 *
 */
public class Main extends TabActivity {
	/**
	 * TabHost que se usar�.
	 */
	private TabHost tabHost;
	/**
	 * Recursos de la aplicaci�n.
	 */
	private Resources res;
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //obtenemos la referencia al tabhost
        tabHost = getTabHost();
        //obtenemos la referencia a los recursos
        res = getResources(); 
       
        creaTabFav();
        creaTabManual();
    }
    /**
     * Funci�n que crea la pesta�a de favoritos, se encarga de crear el intent y de a�adirlo al tabhost.
     * @see PestanaMainFav
     */
    private void creaTabFav(){
  	  TabHost.TabSpec spec;
        Intent intent;
  	 //pesta�a favoritos
     //creamos el intent espec�fico para esa activity
      intent = new Intent().setClass(this, PestanaMainFav.class);
      //creamos las especificaciones, nombre e icono
      spec = tabHost.newTabSpec("pestanamainfav").setIndicator("favoritos", res.getDrawable(R.drawable.pestanamainfav));
      //a�adimos las especificaciones al intent
      spec.setContent(intent);
      //finalemte a�adirmos las especificaciones al tabhost para crear la pesta�a
      tabHost.addTab(spec);
  	 
  }
  private void creaTabManual(){
  	TabHost.TabSpec spec;
      Intent intent;
	 //pesta�a manual
      //creamos el intent espec�fico para esa activity
    intent = new Intent().setClass(this, PestanaMainNotificaciones.class);
    //creamos las especificaciones, nombre e icono
    spec = tabHost.newTabSpec("pestanamainnot").setIndicator("notificaciones", res.getDrawable(R.drawable.pestanamainnot));
    //a�adimos las especificaciones al intent
    spec.setContent(intent);
    //finalemte a�adirmos las especificaciones al tabhost para crear la pesta�a
    tabHost.addTab(spec);
  }
}