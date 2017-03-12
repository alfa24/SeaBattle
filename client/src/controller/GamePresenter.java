package controller;

import model.Field;
import model.Game;
import view.GameInterface;

import java.util.ArrayList;

/**
 * Created by Александр on 18.01.2017.
 */
public class GamePresenter {
    private static GamePresenter gamePresenter;
    public static Object isShootKey = new Object();
    private GameInterface gameInterface;
    private Field.Point point;

    private GamePresenter() {
    }

    public static GamePresenter getInstance() {
        if (gamePresenter == null) {
            gamePresenter = new GamePresenter();
        }
        return gamePresenter;
    }

    public void doShoot(int x, int y) {
        this.point = new Field.Point(x,y);
        synchronized (isShootKey) {
            isShootKey.notifyAll();
        }
    }

    public void updateViewField(ArrayList<ArrayList<String>> field, boolean showShips) {
        gameInterface.showField(field, showShips);
    }

    public void setInterface(GameInterface gameInterface) {
        this.gameInterface = gameInterface;
    }

    public void msg(String s) {
        gameInterface.msg(s);
    }

    public String askUserString(String s) {
        return gameInterface.askUserString(s);
    }

    public int getSizeField() {
        return Game.sizeField;
    }

    public Field.Point getShoot() throws InterruptedException {
        synchronized (isShootKey) {
            isShootKey.wait();
        }
        return this.point;
    }

    public String askUserOpponent(String msg) {
        String result = gameInterface.askUserOpponent(msg);
        if (result.equals("Человек")) {
            return "people";
        }
        if (result.equals("Компьютер")) {
            return "computer";
        }
        return "computer";
    }

    public void setStep(boolean firstPlayer) {
        gameInterface.setStep(firstPlayer);
    }

    public void setShips() {
        gameInterface.setShips();
    }
}
