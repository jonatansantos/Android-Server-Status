package ubu.inf.control.modelo;

import java.util.ArrayList;
/**
 * Clase para guardar todos los datos relativos al servicio Email.
 * @author  David Herrero de la Peña
 * @author  Jonatan Santos Barrios
 * @version  1.0
 */
public class SingletonEmail {
	/**
	 * @uml.property  name="myConexion"
	 * @uml.associationEnd  
	 */
	private static SingletonEmail MyConexion;
	/**
	 * array con todos los servidores a los que tiene que ocnsultar.
	 * @uml.property  name="hosts"
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
	/**
	 * @return
	 * @uml.property  name="hosts"
	 */
	public ArrayList<Servidor> getHosts() {
		return hosts;
	}
	/**
	 * @param hosts
	 * @uml.property  name="hosts"
	 */
	public void setHosts(ArrayList<Servidor> hosts) {
		this.hosts = hosts;
	}
	
	
}
