package server;

import models.Candidate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Register extends UnicastRemoteObject implements RMIInterface {
    private static final String FILE_PATH = "src/DATA/thisinh.info";
    private static int totalRecord;
    private final Map<Integer, RandomAccessFile> sessions;
    private static int sessionID = 0;
    private static Map<Integer, Long> headerPos;

    protected Register() throws IOException {
        sessions = new HashMap<>();
        headerPos = new HashMap<>();
        File f = new File(FILE_PATH);
        System.out.println("is file exist: " + f.exists());
    }

    @Override
    public int getSessionID() throws RemoteException {
        sessionID++;
        return sessionID;
    }

    @Override
    public Candidate registerInfo(int sessionID, String fullName, LocalDate doBirth, String address)
            throws RemoteException {
        try {

            RandomAccessFile raf = new RandomAccessFile(FILE_PATH, "rw");
            sessions.put(sessionID, raf);

            if (LocalDate.now().getYear() - doBirth.getYear() <= 6) {
                System.out.println("OK");

                int candidateID = (int) Math.floor((double) LocalDateTime.now().getNano() / 1000);
                String newFullName = fullName.length() > 25 ? fullName.substring(0, 25) : fullName;
                String newAddress = address.length() > 25 ? address.substring(0, 25) : address;

                Candidate result = new Candidate(candidateID, newFullName, doBirth, newAddress);

                return result;
            } else {
                System.out.println("The candidate's age must be smaller or equals 6");
                return null;
            }
        } catch (FileNotFoundException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean uploadImage(int sessionID, byte[] imgData) throws RemoteException {
        RandomAccessFile raf = sessions.get(sessionID);

        try {
            raf.write(imgData);

            return true;
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public String getBanner() throws RemoteException {
        return "Welcome to Registration";
    }

    @Override
    public void endSession(int sessionID) {
        sessions.remove(sessionID);
    }

    @Override
    public void seek(int sessionID, long pos) throws RemoteException {
        try {
            RandomAccessFile raf = sessions.get(sessionID);
            raf.seek(pos);
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public long writeCandidateData(int sessionID, Candidate candidate) throws RemoteException {
        try {
            RandomAccessFile raf = sessions.get(sessionID);

            totalRecord++;

            raf.seek(0);
            raf.writeInt(totalRecord);

            raf.seek(raf.length());
            headerPos.put(candidate.getCandidateID(), raf.getFilePointer());

            raf.writeInt(candidate.getCandidateID());

            raf.writeUTF(handleString(candidate.getCadidateName()));

            LocalDate doBirth = candidate.getDoBirth();
            raf.writeUTF(handleString(doBirth.getDayOfMonth() + "/" + doBirth.getMonthValue() + "/" + doBirth.getYear()));
            raf.writeUTF(handleString(candidate.getAddress()));

            return raf.getFilePointer();

        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public String viewCandidateData(int sessionID, int candidateID) throws RemoteException {
        String result = "no result";
        try {
            RandomAccessFile raf = sessions.get(sessionID);

            long candidatePos = headerPos.get(candidateID);
            System.out.println("candidatePos: " + candidatePos);
            raf.seek(candidatePos);
            raf.readInt();

            String candidateFullName = raf.readUTF();
            String candidateDoB = raf.readUTF();
            String candidateAddress = raf.readUTF();
            long candidateImgSize = raf.readLong();

            result = candidateFullName.trim() + " | " + candidateDoB.trim() + " | " + candidateAddress.trim() + " | "
                    + candidateImgSize + "bytes";


            return result;
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean updateCandidateAddress(int sessionID, int candidateID, String newAddress) throws RemoteException {
        try {
            RandomAccessFile raf = sessions.get(sessionID);

            long candidatePos = headerPos.get(candidateID);
            System.out.println("candidatePos: " + candidatePos);
            raf.seek(candidatePos);

            // skip to address
            raf.readInt(); // candidate id
            raf.readUTF(); // candidate name
            raf.readUTF();  // candidate dob

            raf.writeUTF(handleString(newAddress));

            return true;
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }

    }

    @Override
    public long writeImgFileSize(int sessionID, long pos, long size) throws RemoteException {
        try {
            RandomAccessFile raf = sessions.get(sessionID);
            raf.seek(pos);

            raf.writeLong(size);

            return raf.getFilePointer();
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }

    }

    @Override
    public void closeRAF(int sessionID) throws RemoteException {
        try {
            RandomAccessFile raf = sessions.get(sessionID);
            raf.close();
        } catch (IOException e) {
            throw new RemoteException(e.getMessage());
        }

    }

    private String handleString(String str) {
        StringBuilder result = null;
        if (str.length() > 25)
            result = new StringBuilder(str.substring(0, 25));
        if (str.length() < 25) {
            int diff = 25 - str.length();
            result = new StringBuilder(str);
            for (int i = 0; i < diff; i++) {
                result.append(" ");
            }
        }

        return result.toString();
    }

}
