package model;

import exceptions.NoPointForShot;
import view.Server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Александр on 22.01.2017.
 */
public class User extends Player {
    private Server gamePresenter;
    private Socket socket;
    private State state;
    public enum State {
        READY_PEOPLE, READY_COMPUTER, OFFLINE, PREP, GAME
    };

    public void setGamePresenter(Server gamePresenter) {
        this.gamePresenter = gamePresenter;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void msg(String s) {
        gamePresenter.msg(s);
    }

    public String getOpponent() {
        String result = gamePresenter.askUserOpponent("Выберите своего соперника:");
        return result;
    }

    public void updateGui(boolean firstPlayer, Boolean endGame) {
        gamePresenter.showField(super.getField(), true, true); //свое поле показываем всегда
        gamePresenter.showField(super.getFieldForAttack(), false, endGame); //вражеское поле после окончания игры
        gamePresenter.setStep(firstPlayer);
    }

    public void setShips() {
//        gamePresenter.setShips();
        getField().setShipsAuto(1, getFleet());
//        super.getField().setShipsAuto(1, super.getFleet());
//        field.setShipsManual(fleet);
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public Field.Point getShoot() throws NoPointForShot, IOException, ClassNotFoundException, InterruptedException {
        Field.Point point = null;

            point = gamePresenter.getShoot();


        if (point != null) {
            return point;
        }

        throw new NoPointForShot("Нет координат для выстрела");    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object getState() {
        return state;
    }

}



