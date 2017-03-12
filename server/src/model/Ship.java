package model;

import exceptions.OutOfShipBoundary;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by ФаличевАЮ on 14.10.2016.
 */
public class Ship implements Cloneable, Serializable {
    private int countDecks;  //количество палуб
    private StatusDeck[] decks;   //массив палуб корабля, у каждой палубы свой статус
    private TypePosition position;   //тип корабля: вертикальный или горизонтальный - устанавливается картой
    private int positionX;
    private int positionY;
    private boolean isDeadFlag;
    private boolean isDamageFlag;

    //тип размещения вертикальный или горизонтальный
    public enum TypePosition implements Serializable {
        VERTICAL,
        HORIZONTAL,
        NONE;
    }

    //статус палубы
    public enum StatusDeck implements Serializable{
        OK, DAMAGE;
    }

    private Ship() {
    }

    public Ship(int countDecks) {
        this.countDecks = countDecks;
        init();
    }

    public void init() {
        delPosition();
        decks = new StatusDeck[countDecks];
        Arrays.fill(decks, StatusDeck.OK);
        isDeadFlag = false;
        isDamageFlag = false;
    }

    //Возвращает TRUE, если корабль потоплен
    public boolean isDead() {
        boolean result = true;
        if (!isDeadFlag) {
            for (StatusDeck deck : decks) {
                if (deck == StatusDeck.OK) {
                    result = false;
                }
            }
        }
        isDeadFlag = result;
        return result;
    }

    public boolean isDamage() {
        return isDamageFlag;
    }

    //Помечает палубу корабля по заданным координатам как "поврежденную"
    public void damage(int x, int y) throws OutOfShipBoundary {
        int i = getIndDeck(x, y);
        decks[i] = StatusDeck.DAMAGE;
        isDamageFlag = true;
        isDead();
    }

    //метод возвращает индекс палубы корабля, по заданным координатам
    private int getIndDeck(int x, int y) throws OutOfShipBoundary {
        int result = -1;
        if (this.position == Ship.TypePosition.HORIZONTAL & y == this.getPositionY()) {
            result = x - this.positionX;
        }
        if (this.position == Ship.TypePosition.VERTICAL & x == this.getPositionX()) {
            result = y - this.positionY;
        }

        if (result < 0 | result >= countDecks) {
            throw new OutOfShipBoundary("Неправильно заданы координаты");
        }

        return result;
    }

    public int countDecksDamage() {
        // TODO ID: 12.12.16 а однотипная обработка элементов массива в дальнейшем будет лаконичнее записана с помощью лямбд
        int result = 0;
        for (StatusDeck deck : decks) {
            if (deck == StatusDeck.DAMAGE) {
                result++;
            }
        }
        return result;
    }

    public TypePosition getPosition() {
        return position;
    }

    public StatusDeck getStatusDecks(int x, int y) throws OutOfShipBoundary {
        int i = getIndDeck(x, y);
        return decks[i];
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getCountDecks() {
        return countDecks;
    }

    public void setPosition(int positionX, int positionY, TypePosition position) {
        if (position != TypePosition.NONE) {
            this.position = position;
            this.positionX = positionX;
            this.positionY = positionY;
        } else {
            delPosition();
        }
    }

    private void delPosition() {
        this.position = TypePosition.NONE;
        this.positionX = -1;
        this.positionY = -1;
    }

    @Override
    protected Ship clone() {
        Ship result = null;
        try {
            result = (Ship) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        return "Палуб: " + countDecks + " Тип: " + getPosition() + " Позиция: " + getPositionX() + "," + getPositionY() + " Адрес: " + super.toString();
    }
}
