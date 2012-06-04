package ubu.inf.control.accesodatos;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServicioBorrado extends Service{

	
	
	@Override
	public void onCreate() {
		super.onCreate();
		FachadaServidores.getInstance(this).borraTabla();
		FachadaEmail.getInstance(this).borraTabla();
		this.stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
