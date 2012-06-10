package ubu.inf.control.logica;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Clase que hereda de BroadcastReceiver, se encarga de iniciar un servicio concreto cuando ocurre un evento declarado en el manifest, en este caso cuando el teléfono termina de encenderse.
 * @author   David Herrero de la Peña
 * @author   Jonatan Santos Barrios
 * @version   1.0
 * @see BroadcastReceiver
 * @see ServicioAutoarranque
 * @uml.dependency   supplier="ubu.inf.control.logica.ServicioAutoarranque"
 */
public class AutoArranque extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent();
		
		i.setAction("ServicioAutoarranque");
		context.startService(i);
		
	}

}
