package ubu.inf.prototipos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import android.app.Activity;
import android.os.Bundle;
import android.os.storage.OnObbStateChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;

public class Ssh extends Activity {

	private Button conectar;
	private Button desconectar;
	private ImageButton comando;
	private EditText user;
	private EditText pass;
	private EditText host;
	private EditText port;
	private EditText cmd;
	private EditText res;
	private JSch jsch;
	private Session session;
	private ChannelExec channelExec;
	private ViewSwitcher sw;
	//ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.vSwicher);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginssh);
		inicializa();
		
		
	}
	private void inicializa(){
		conectar = (Button) findViewById(R.id.btConnect);
		desconectar = (Button) findViewById(R.id.btDesConectar);
		comando = (ImageButton) findViewById(R.id.btrun);
		user = (EditText) findViewById(R.id.etUser);
		pass = (EditText) findViewById(R.id.etPass);
		host = (EditText) findViewById(R.id.etHost);
		port = (EditText) findViewById(R.id.etPort);
		cmd = (EditText) findViewById(R.id.etCmd);
		res = (EditText) findViewById(R.id.etResult);
		sw = (ViewSwitcher) findViewById(R.id.vSwicher);
		conectar.setOnClickListener(new listenerConectar());
		desconectar.setOnClickListener(new listenerDesconectar());
		comando.setOnClickListener(new listenerComando());
		
	}
	private class listenerComando implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(jsch!=null && session != null){
				try{
				res.setText("");
				channelExec = (ChannelExec) session
						.openChannel("exec");

				InputStream in = channelExec.getInputStream();

				channelExec.setCommand(cmd.getText().toString());
				channelExec.connect();
				 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			        String linea = null;
			  
			 
			        while ((linea = reader.readLine()) != null) {
			        	res.setText(res.getText().toString() + linea +'\n');
			        
			        }
				}catch (JSchException e) {
					// TODO: handle exception
				}catch (IOException e) {
					// TODO: handle exception
				}
			}
		}
		
	}
	private class listenerDesconectar implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(jsch!=null && session != null){
				channelExec.disconnect();
				session.disconnect();
				sw.showNext();
			}
		}
		
	}
	
	private class listenerConectar implements View.OnClickListener {
		

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (jsch == null && session == null) {
				try {
					int p = Integer.parseInt(port.getText().toString());
					jsch = new JSch();
				     session = jsch.getSession(
							user.getText().toString(), host.getText()
									.toString(), p);
					UserInfo ui = new SUserInfo(pass.getText().toString(), null);
					session.setUserInfo(ui);
					session.setPassword(pass.getText().toString());
					session.connect();
					sw.showNext();
					
					
					// System.out.println("------ FIN");
				} catch (Exception e) {
					// TODO: handle exception
				
				}
			}
		}

	}

}
