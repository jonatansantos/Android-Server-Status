package ubu.inf.terminal.modelo;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SingletonConexion {
	private static SingletonConexion MyConexion;
	private Session sesion;
	private JSch jsch;

	private SingletonConexion(){
		
	}
	public static SingletonConexion getConexion(){
		if(MyConexion==null){
			MyConexion = new SingletonConexion();
		}
		return MyConexion;
	}
	public Session getSesion() {
		return sesion;
	}
	public void setSesion(Session sesion) {
		this.sesion = sesion;
	}
	public JSch getJsch() {
		return jsch;
	}
	public void setJsch(JSch jsch) {
		this.jsch = jsch;
	}
	
}
