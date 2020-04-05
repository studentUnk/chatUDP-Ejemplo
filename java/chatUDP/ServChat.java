package chatUDP;

import java.io.IOException;
import java.net.ServerSocket;

public class ServChat {
	
	public static void main(String [] args) {
		int port = (args.length > 0) ? Integer.parseInt(args[0]) : 54321;
		Conversacion conv = new Conversacion();
		System.out.println("Servidor activado en el puerto: " + port);
		ServerSocket ss;
		try {
			ss = new ServerSocket(port);
			System.out.println("El servidor ha iniciado.");
			System.out.println("Esperando clientes...");
			while(true) conv.altaCliente(ss.accept());
		} catch(IOException e) {
			System.out.println("El servidor no ha podido arrancar.");
			e.getStackTrace();
			System.exit(-1);
		}
	}
}
