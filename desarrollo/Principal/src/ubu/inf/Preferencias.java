package ubu.inf;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Clase que extiende de PreferenceActivity y sirve para establecer las prefrencias de la aplicación.
 * @author   David Herrero de la Peña
 * @author   Jonatan Santos Barrio
 * @version   1.0
 * @see PreferenceActivity
 * @uml.dependency   supplier="ubu.inf.Login"
 */
public class Preferencias extends PreferenceActivity {

	/**
	 * onCreate.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencias);
	}

	/**
	 * @uml.property  name="falsaActividad"
	 * @uml.associationEnd  inverse="preferencias:ubu.inf.FalsaActividad"
	 * @uml.association  name="<call>"
	 */
	private FalsaActividad falsaActividad;

	/**
	 * Getter of the property <tt>falsaActividad</tt>
	 * @return  Returns the falsaActividad.
	 * @uml.property  name="falsaActividad"
	 */
	public FalsaActividad getFalsaActividad() {
		return falsaActividad;
	}

	/**
	 * Setter of the property <tt>falsaActividad</tt>
	 * @param falsaActividad  The falsaActividad to set.
	 * @uml.property  name="falsaActividad"
	 */
	public void setFalsaActividad(FalsaActividad falsaActividad) {
		this.falsaActividad = falsaActividad;
	}

}
