package chatUDP;

import java.net.*;

public class Conversacion {
	
	Cliente C = null;
	Mensaje M = null;
	
	public void nuevoMensaje(Mensaje m) {
		m.sig = M;
		M = m;
	}
	
	public void altaCliente(Socket s) {
		System.out.println("Nuevo cliente");
		(C = new Cliente(this,s,C)).start(); // nuevo cliente
	}
	
	public void bajaCliente(Cliente c) {
		if(c == C) C = c.sig;
		else {
			Cliente p;
			p = C;
			while(p.sig != c) p = p.sig;
			p.sig = p.sig.sig; // Eliminacion por punteros
		}
	}
	
	public Cliente buscarNick(String s) {
		Cliente c;
		c = C;
		while((c != null) && (!s.equals(c.nick))) { c = c.sig; }
		return c;
	}
	
	public void difundirMensaje(String s) {
		Cliente c = C;
		while(c != null) {
			c.enviar(s);
			c = c.sig;
		}
	}
}
