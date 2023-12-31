package models;

import java.io.Serializable;
import java.time.LocalDate;

public class Candidate implements Serializable {
	private int candidateID;
	private String cadidateName;
	private LocalDate doBirth;
	private String address;
	
	public Candidate(int candidateID, String cadidateName, LocalDate doBirth, String address) {
		this.candidateID = candidateID;
		this.cadidateName = cadidateName;
		this.doBirth = doBirth;
		this.address = address;
	}
	
	public int getCandidateID() {
		return candidateID;
	}
	public void setCandidateID(int candidateID) {
		this.candidateID = candidateID;
	}
	public String getCadidateName() {
		return cadidateName.length() > 25 ? cadidateName.substring(0, 25) : cadidateName;
	}
	public void setCadidateName(String cadidateName) {
		this.cadidateName = cadidateName;
	}
	public LocalDate getDoBirth() {
		return doBirth;
	}
	public void setDoBirth(LocalDate doBirth) {
		this.doBirth = doBirth;
	}
	public String getAddress() {
		return address.length() > 25 ? address.substring(0, 25) : address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
