package ubu.itig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class Consola  extends Activity{
	private EditText comando;
	private EditText result;
	private ImageButton run;
	private Session session;
	private JSch jsch;
	private ChannelExec channelExec;
	private InputStream in;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consola);
		inicializa();
		
	}
	
	public void inicializa(){
		comando = (EditText) findViewById(R.id.et_consola_comando);
		result = (EditText) findViewById(R.id.et_consola_resultado);
		result.setKeyListener(null);
		run = (ImageButton) findViewById(R.id.ib_consola_run);
	
		session= SingletonConexion.getConexion().getSesion();
		jsch = SingletonConexion.getConexion().getJsch();
		run.setOnClickListener(new listenerComando());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menucomandos, menu);
	    return true;
	}
	@Override
	   public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                case R.id.anadirVarios: 
                        añadirVarios();
                        return true;
                case R.id.reconectar:
                        reconectar();
                        return true;
                case R.id.anadirUltimo:
                        añadirUltimo();
                        return true;
                case R.id.borrar:
                        borrar();
                        return true;
                case R.id.ejecutarFavorito:
                        ejecutarFavorito();
                        return true;
                case R.id.cerrar:
                	
                	session.disconnect(); 
                	SingletonConexion.getConexion().setJsch(null);
                	SingletonConexion.getConexion().setSesion(null);
                        finish();
                        return true;
                default:
                        return false;
        }
}
	private void ejecutarFavorito() {
		// TODO Auto-generated method stub
		
	}

	private void borrar() {
		// TODO Auto-generated method stub
		
	}

	private void añadirUltimo() {
		// TODO Auto-generated method stub
		
	}

	private void reconectar() {
		// TODO Auto-generated method stub
		
	}

	private void añadirVarios() {
		// TODO Auto-generated method stub
		
	}
	
	private class listenerComando implements View.OnClickListener{

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			if(jsch!=null && session != null){
				
				try{
				
			
				channelExec = (ChannelExec) session
						.openChannel("exec");

				 in = channelExec.getInputStream();

				channelExec.setCommand(comando.getText().toString());
				channelExec.connect();
			
				
				result.setText(result.getText().toString() + session.getUserName()+"@"+session.getHost()+"  "+ comando.getText().toString() + '\n'+'\n');
				result.setTextColor(Color.GREEN); 
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			        String linea = null;
			  
			 
			        while ((linea = reader.readLine()) != null) {
			        	result.setText(result.getText().toString() + linea +'\n');
			        
			        }
			        result.setText(result.getText().toString() + '\n'+'\n');
			    
				}catch (JSchException e) {
					// TODO: handle exception
				}catch (IOException e) {
					// TODO: handle exception
				}
			}
		}
		
	}
}
