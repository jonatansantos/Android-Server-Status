package ubu.inf.modelo;

import android.app.PendingIntent;

public class ServIntent {

	private Servidor serv;
	private PendingIntent pen;
	public ServIntent(Servidor serv, PendingIntent p) {
		super();
		this.serv = serv;
		this.pen = p;
	}
	public Servidor getServ() {
		return serv;
	}
	public void setServ(Servidor serv) {
		this.serv = serv;
	}
	public PendingIntent getPen() {
		return pen;
	}
	public void setPen(PendingIntent pen) {
		this.pen = pen;
	};
	
	
}
