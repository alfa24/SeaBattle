package view;

import model.Field;

import java.util.ArrayList;

/**
 * Created by ФаличевАЮ on 14.10.2016.
 * Интерфейс отображения игры игроку
 */
public interface GameInterface {

    void init();

    String askUserString(String ask);

    int askUserInteger(String ask);

    void showField(ArrayList<ArrayList<String>> field, boolean b);

    void msg(String text);

    Field.Point getShoot();

    String askUserOpponent(String msg);

    void setStep(boolean firstPlayer);

    void setShips();
}
