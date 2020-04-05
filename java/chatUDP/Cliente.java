package chatUDP;

import java.net.*;
import java.io.*;

public class Cliente extends Thread{
	
	Conversacion conv;
	Cliente sig;
	BufferedReader in;
	PrintWriter out;
	String nick;
	boolean activo = true;
	
	public Cliente (Conversacion conv, Socket socket, Cliente sig) {
		this.conv = conv;
		this.sig = sig;
		this.nick = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
		} catch(IOException e) {
			System.out.println("Error de entrada o salida");
			e.getStackTrace();
			System.exit(-1);
		}
	}
	
	public void run() {
		try {
			while(activo) procesar(Mensaje.decodificar(in.readLine()));
		}catch(IOException e) {
			System.out.println("Error de entrada o salida (run)");
			System.exit(-1);
		}
	}
	
	public void enviar(String s) {
		out.println(s);
		out.flush(); // liberar 
	}
	
	public void procesar(Mensaje m) {
		if(m instanceof MensajePublico) {
			System.out.println("Publico");
			conv.difundirMensaje(nick + ": " + ((MensajePublico)m).txt);
		}
		if(m instanceof MensajePrivado) {
			System.out.println("Privado");
			Cliente destino = conv.buscarNick(((MensajePrivado)m).t);
			if(destino != null) destino.enviar("[" + nick + "] " + ((MensajePrivado)m).txt);
		}
		if(m instanceof MensajeNick) {
			System.out.println("Nick");
			String nuevo = ((MensajeNick)m).txt;
			Cliente c = conv.buscarNick(nuevo);
			if(c != null) enviar("" + new MensajeRechazoNick());
			else {
				if(nick == null) {
					conv.difundirMensaje("SERVIDOR: '" + nuevo + "' ha ingresado a la sala");
				}
				else {
					conv.difundirMensaje("SERVIDOR: '" + nick + "' ahora se llama '" + nuevo + "'");
				}
				nick = nuevo;
			}
		}
		if(m instanceof MensajeDesconexion) {
			System.out.println("Desconexion");
			conv.bajaCliente(this);
			activo = false;
			conv.difundirMensaje("SERVIDOR: '" + nick + "' ha salido de la sala");
		}
	}
}
