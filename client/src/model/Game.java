package model;

import controller.GamePresenter;
import view.NetCommand;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by ФаличевАЮ on 14.10.2016.
 */
public class Game {
    public static final int sizeField = 10;
    private static Game game;
    private GamePresenter gamePresenter;

    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;


    private Game() {
    }

    public static Game getInstance() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    public void init() {
        gamePresenter = GamePresenter.getInstance();
    }

    public void start() {
        try {
            socket = new Socket("localhost", 9090);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            String msg;

            while (true) {
                NetCommand command = (NetCommand) objectInputStream.readObject();
                switch (command) {
                    case ASK_USER_STRING:
                        msg = (String) objectInputStream.readObject();
                        String result = gamePresenter.askUserString(msg);
                        objectOutputStream.writeObject(result);
                        objectOutputStream.flush();
                        break;
                    case ASK_USER_INTEGER:
                        break;
                    case MSG:
                        msg = (String) objectInputStream.readObject();
                        gamePresenter.msg(msg);
                        break;
                    case SHOW_FIELD:
                        ArrayList<ArrayList<String>> fieldNet;
                        fieldNet = (ArrayList<ArrayList<String>>) objectInputStream.readObject();
                        boolean b = (boolean) objectInputStream.readObject();
                        gamePresenter.updateViewField(fieldNet,b);
                        break;
                    case GET_SHOOT:
                        Field.Point point = gamePresenter.getShoot();
                        objectOutputStream.writeObject(point);
                        objectOutputStream.flush();
                        break;
                    case ASK_USER_OPPONENT:
                        msg = (String) objectInputStream.readObject();
                        String opponent = gamePresenter.askUserOpponent(msg);
                        objectOutputStream.writeObject(opponent);
                        objectOutputStream.flush();
                        break;
                    case SET_STEP:
                        boolean firstPlayer = (boolean) objectInputStream.readObject();
                        gamePresenter.setStep(firstPlayer);
                        break;
                    case SET_SHIPS:
                        gamePresenter.setShips();
                        break;
                }
            }
        } catch (IOException e) {
            gamePresenter.msg("Соединение разорвано.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
