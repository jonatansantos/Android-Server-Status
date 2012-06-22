package ubu.inf.control.accesodatos;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;

/**
 * Servicio que se encarga de borrar los datos guardado en la base de datos, es llamado por 
 * el lector sms cuando se recibe el mensaje para borrar los datos.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * 
 * @version 1.0
 * @see Service
 * 
 */
public class ServicioBorrado extends Service{

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		FachadaSSH.getInstance(this).borraTabla();
		FachadaEmail.getInstance(this).borraTabla();
		this.stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
