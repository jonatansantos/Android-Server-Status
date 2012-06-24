package ubu.inf.control.vista;

import ubu.inf.control.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Clase que extiende de PreferenceActivity y sirve para establecer las
 * prefrencias de la aplicaci�n.
 * 
 * @author David Herrero de la Pe�a
 * @author Jonatan Santos Barrio
 * @version 1.0
 * @see PreferenceActivity
 * 
 */
public class Preferencias extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencias);
	}

}
