package ubu.inf.terminal.modelo;

import java.util.ArrayList;

public class Script {

	private ArrayList<String> comandos;
	private String nombre;
	private int cantidad;
	private int idScript;
	public Script(String nombre, int cantidad,
			int idScript) {
		super();
		this.comandos = new ArrayList<String>();
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.idScript = idScript;
	}
	public ArrayList<String> getComandos() {
		return comandos;
	}
	public void addComando(String comando) {
		comandos.add(comando);
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCantidad() {
		return cantidad;
	}
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	public int getIdScript() {
		return idScript;
	}
	public void setIdScript(int idScript) {
		this.idScript = idScript;
	}
	
	
}
