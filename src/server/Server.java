package server;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
	public static void main(String[] args) throws IOException, AlreadyBoundException {
		Registry reg = LocateRegistry.createRegistry(1022);
		RMIInterface rmiInterface = new Register();

		reg.bind("REGISTER", rmiInterface);
	}
}
