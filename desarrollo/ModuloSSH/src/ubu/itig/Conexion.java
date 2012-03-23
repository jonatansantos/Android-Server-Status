package ubu.itig;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Conexion {
	private static Conexion MyConexion;
	private Session sesion;
	private JSch jsch;

	private Conexion(){
		
	}
	public static Conexion getConexion(){
		if(MyConexion==null){
			MyConexion = new Conexion();
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
