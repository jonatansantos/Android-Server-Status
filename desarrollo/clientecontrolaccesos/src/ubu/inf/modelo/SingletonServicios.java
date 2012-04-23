package ubu.inf.modelo;

import java.util.ArrayList;

public class SingletonServicios {
	private static SingletonServicios MyConexion;
	private ArrayList<String> hosts;
	

	private SingletonServicios(){
		hosts=new ArrayList<String>();
	}
	public static SingletonServicios getConexion(){
		if(MyConexion==null){
			MyConexion = new SingletonServicios();
		}
		return MyConexion;
	}
	public ArrayList<String> getHosts() {
		return hosts;
	}
	public void setHosts(ArrayList<String> hosts) {
		this.hosts = hosts;
	}
	
	
}
