package chatUDP;

public abstract class Mensaje {
	Mensaje sig;
	public static Mensaje decodificar(String s) {
		if(s.compareTo("/nt") == 0) return new MensajeRechazoNick();
		if(s.compareTo("/d") == 0) return new MensajeDesconexion();
		if(s.startsWith("/n:")) return new MensajeNick(s.substring(3));
		if(s.startsWith("/p")) return new MensajePrivado(s.substring(3));
		return new MensajePublico(s);
	}
	abstract public String toString();
}

class MensajePublico extends Mensaje{
	String txt;
	public MensajePublico(String s) { txt = s; }
	public String toString() { return txt; }
}

class MensajeRechazoNick extends Mensaje{
	public String toString() { return "/nt"; }
}

class MensajeDesconexion extends Mensaje{
	public String toString() { return "/d"; }
}

class MensajeNick extends Mensaje{
	String txt;
	public MensajeNick(String s) { txt = s; }
	public String toString() { return "/p:" + txt;}
}

class MensajePrivado  extends Mensaje{
	String txt,t;
	public MensajePrivado(String s) {
		int fin = s.indexOf(":");
		t = s.substring(0,fin); // Quita los parametros iniciales
		txt = s.substring(fin+1); // Inicia en fin+1 hasta el final del String
	}
	public String toString() { return "/p:" + t + ":" + txt; }
}
