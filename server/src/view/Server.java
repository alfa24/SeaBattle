package view;

import exceptions.OutOfShipBoundary;
import model.Cell;
import model.Field;
import model.Ship;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Александр on 17.02.2017.
 */
public class Server {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public Server(Socket socket) {
        objectInputStream = null;
        objectOutputStream = null;
        this.socket = socket;
    }

    public void init() {
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Соединение разорвано...");
        }
    }

    public String askUserString(String ask) {
        try {
            objectOutputStream.writeObject(NetCommand.ASK_USER_STRING);
            objectOutputStream.writeObject(ask);
            String result = (String) objectInputStream.readObject();
            return result;
        } catch (IOException e) {
            System.out.println("Соединение разорвано...");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showField(Field field, boolean firstPlayer, Boolean showShips) {
        ArrayList<ArrayList<String>> fieldNet = new ArrayList<>();
        Cell[][] cells = field.getCells();
        for (int j = 0; j < cells.length; j++) {
            ArrayList<String> row = new ArrayList<>();
            for (int i = 0; i < cells[0].length; i++) {
                if (i >= 0 & j >= 0) {  //само поле
                    String tmp = "null";
                    if (cells[i][j].getCountHits() > 0) {
                        tmp = "Hit";
                    }

                    Ship ship = cells[i][j].getShip();
                    if (ship != null) {
                        Ship.StatusDeck statusDeck;
                        try {
                            statusDeck = ship.getStatusDecks(i, j);
                            if (statusDeck == Ship.StatusDeck.OK & showShips) {
                                tmp = "ShipOk";
                            }
                            if (statusDeck == Ship.StatusDeck.DAMAGE) {
                                tmp = "ShipDamage";
                            }
                        } catch (OutOfShipBoundary e) {
                            System.err.println(e.getMessage());
                            e.printStackTrace();
                        }
                        if (ship.isDead()) {
                            tmp = "ShipDead";
                        }
                    }
                    row.add(tmp);
                }
            }
            fieldNet.add(row);
        }

        try {
            objectOutputStream.writeObject(NetCommand.SHOW_FIELD);
            objectOutputStream.writeObject(fieldNet);
            objectOutputStream.writeObject(firstPlayer);
        } catch (IOException e) {
            System.out.println("Соединение разорвано...");
        }

    }

    public void msg(String text) {
        try {
            objectOutputStream.writeObject(NetCommand.MSG);
            objectOutputStream.writeObject(text);
        } catch (IOException e) {
            System.out.println("Соединение разорвано...");
        }
    }

    public Field.Point getShoot() throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(NetCommand.GET_SHOOT);
        Field.Point point = (Field.Point) objectInputStream.readObject();
        return point;

    }

    public String askUserOpponent(String s) {
        try {
            objectOutputStream.writeObject(NetCommand.ASK_USER_OPPONENT);
            objectOutputStream.writeObject(s);
            String result = (String) objectInputStream.readObject();
            return result;
        } catch (IOException e) {
            System.out.println("Соединение разорвано...");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setStep(boolean firstPlayer) {
        try {
            objectOutputStream.writeObject(NetCommand.SET_STEP);
            objectOutputStream.writeObject(firstPlayer);
        } catch (IOException e) {
            System.out.println("Соединение разорвано...");
        }
    }

    public void setShips() {
        try {
            objectOutputStream.writeObject(NetCommand.SET_SHIPS);
//            String result = (String) objectInputStream.readObject();
        } catch (IOException e) {
            System.out.println("Соединение разорвано...");
        }
    }
}
