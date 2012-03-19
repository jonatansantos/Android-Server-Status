package com.example.findemor.push;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class mainActivity extends Activity {
	
	private static PendingIntent pendingIntent;
	
	Button m_btnAlarma = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        m_btnAlarma = ((Button)findViewById(R.id.btnAlarm));
        m_btnAlarma.setOnClickListener(
				new View.OnClickListener() {
					public void onClick(View view) {
						CambiarEstadoAlarma();
					}});
    }

    
    /**
     * Desactiva o activa la alarma, estableciendola al estado contrario del actual
     */
    private void CambiarEstadoAlarma()
    {
    	if (pendingIntent == null)
    	{
    		//La alarma est‡ desactivada, la activamos
    		ActivarAlarma();
    	}else
    	{
    		//La alarma est‡ activada, la desactivamos
    		DesactivarAlarma();
    	}
    }
    
    /**
     * Desactiva la alarma
     */
    private void DesactivarAlarma()
    {
    	 AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    	 alarmManager.cancel(pendingIntent);
    	            
    	 m_btnAlarma.setText(getString(R.string.btnAlarmOFF));
    	 pendingIntent = null;
    	 

		 Toast.makeText(mainActivity.this, "Alarma detenida", Toast.LENGTH_LONG).show();
		   
    }
    
    /**
     * Activa la alarma
     */
    private void ActivarAlarma()
    {
    	
    	int comprobacionIntervaloSegundos = 30;
    	
		   Intent myIntent = new Intent(mainActivity.this, alarmChecker.class);
		   pendingIntent = PendingIntent.getService(mainActivity.this, 0, myIntent, 0);

		   AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

		   Calendar calendar = Calendar.getInstance();
		   calendar.setTimeInMillis(System.currentTimeMillis());
		   calendar.add(Calendar.SECOND, 10);
		   alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), comprobacionIntervaloSegundos * 1000, pendingIntent);

		   m_btnAlarma.setText(getString(R.string.btnAlarmON));
		   
		   Toast.makeText(mainActivity.this, "Alarma iniciada", Toast.LENGTH_LONG).show();
		   
		
    }
    
}