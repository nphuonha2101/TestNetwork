package server;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;

import models.Candidate;

public interface RMIInterface extends Remote {
	int getSessionID() throws RemoteException;
	String getBanner() throws RemoteException;
	Candidate registerInfo(int sessionID, String fullName, LocalDate doBirth, String address) throws RemoteException;
	long writeCandidateData(int sessionID, Candidate candidate) throws RemoteException;
	boolean uploadImage(int sessionID, byte[] imgData) throws RemoteException;
	String viewCandidateData(int sessionID, int candidateID) throws RemoteException;
	boolean updateCandidateAddress(int sessionID, int candidateID, String newAddress) throws RemoteException;
	long writeImgFileSize(int sessionID, long pos, long size) throws RemoteException;
	void closeRAF(int sessionID) throws RemoteException;
	void endSession(int sessionID) throws RemoteException;
	void seek(int sessionID, long pos) throws RemoteException;
}
