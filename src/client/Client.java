package client;

import models.Candidate;
import server.RMIInterface;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

public class Client {
    public static void main(String[] args) throws IOException, NotBoundException {
        Registry reg = LocateRegistry.getRegistry(1022);
        RMIInterface registerImpl = (RMIInterface) reg.lookup("REGISTER");


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer inTokens;

        int sessionID = registerImpl.getSessionID();
        int candidateID = -1;

        System.out.println(registerImpl.getBanner() + " your session ID is: " + sessionID);

        String command = "";
        String param1 = "";
        String param2 = "";
        String param3 = "";
        long currentFilePointer = -1;

        clientLoop:
        while (true) {
            System.out.println("Please type command: ");

            String userPrompt = reader.readLine();
            inTokens = new StringTokenizer(userPrompt, " ");

            if (inTokens.countTokens() == 1) {
                command = inTokens.nextToken();
                if (command.equalsIgnoreCase("quit")) {
                    registerImpl.endSession(sessionID);
                    break clientLoop;
                }
            }

            if (inTokens.countTokens() == 2) {
                command = inTokens.nextToken().toLowerCase().trim();
                param1 = inTokens.nextToken();

                switch (command) {
                    case "foto": {
                        if (currentFilePointer != -1) {
                            File f = new File(param1);
                            long fileSize;
                            if (f.exists()) {
                                fileSize = f.length();
                                if (f.length() <= 100000 && f.getName().endsWith(".png")) {
                                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));

                                    currentFilePointer = registerImpl.writeImgFileSize(sessionID, currentFilePointer, fileSize);

                                    byte[] data = new byte[102400];
                                    int byteRead;

                                    registerImpl.writeImgFileSize(sessionID, currentFilePointer, fileSize);
                                    while ((byteRead = bis.read(data)) != -1) {
                                        byte[] fitData = new byte[byteRead];

                                        registerImpl.uploadImage(sessionID, fitData);
                                    }

                                    bis.close();
//                                    registerImpl.closeRAF(sessionID);
                                } else {
                                    System.out.println("File size must be smaller than 100KB and must be a PNG file!");
                                }
                            } else {
                                System.out.println("File not existed!");
                            }
                        } else {
                            System.out.println("Current file pointer is not valid!");
                        }
                        break;

                    }

                    case "view": {
                        candidateID = Integer.parseInt(param1);

                        if (candidateID != -1) {
                            String result = registerImpl.viewCandidateData(sessionID, candidateID);
                            System.out.println(result);
                        } else {
                            System.out.println("Candidate ID not valid!");
                        }

                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Unexpected value: " + command);
                }

            }

            if (inTokens.countTokens() == 3) {
                command = inTokens.nextToken().toLowerCase().trim();
                param1 = inTokens.nextToken();
                param2 = inTokens.nextToken();

                switch (command) {
                    case "update": {
                        candidateID = Integer.parseInt(param1);
                        if (candidateID != -1) {

                            if (registerImpl.updateCandidateAddress(sessionID, candidateID, param2))
                                System.out.println("Update successful!");
                            else
                                System.out.println("Update failed!");
                        } else {
                            System.out.println("Candidate ID not valid!");
                        }
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Unexpected value: " + command);
                }

            }

            if (inTokens.countTokens() == 4) {

                command = inTokens.nextToken().toLowerCase().trim();
                param1 = inTokens.nextToken();
                param2 = inTokens.nextToken();
                param3 = inTokens.nextToken();

                if (command.equals("register")) {
                    LocalDate doBirth = LocalDate.parse(param2, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    Candidate candidate = registerImpl.registerInfo(sessionID, param1, doBirth, param3);

                    if (candidate != null) {
                        candidateID = candidate.getCandidateID();
                        currentFilePointer = registerImpl.writeCandidateData(sessionID, candidate);
                        System.out.println("Your Candidate ID: " + candidateID);
                    } else {
                        System.out.println("Has an error!");
                    }
                } else {
                    System.out.println("Unexpected value: " + command);
                }
            }
        }

    }
}
