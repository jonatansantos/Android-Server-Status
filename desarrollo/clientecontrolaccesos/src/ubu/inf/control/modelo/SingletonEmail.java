package ubu.inf.control.modelo;

import java.util.ArrayList;
/**Clase para guardar todos los datos relativos al servicio Email.
 * 
 * @author David Herrero de la Peña
 * @author Jonatan Santos Barrios
 * @version 1.0
 * 
 *
 */
public class SingletonEmail {
	private static SingletonEmail MyConexion;
	/**
	 * array con todos los servidores a los que tiene que ocnsultar.
	 */
	private ArrayList<Servidor> hosts;
	

	
	private SingletonEmail(){
		hosts=new ArrayList<Servidor>();
	}
	public static SingletonEmail getConexion(){
		if(MyConexion==null){
			MyConexion = new SingletonEmail();
		}
		return MyConexion;
	}
	public ArrayList<Servidor> getHosts() {
		return hosts;
	}
	public void setHosts(ArrayList<Servidor> hosts) {
		this.hosts = hosts;
	}
	
	
}
