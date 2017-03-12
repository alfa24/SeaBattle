package model;

import java.io.Serializable;

/**
 * Created by ФаличевАЮ on 19.10.2016.
 */
//Класс 1 ячейки на поле Field
public class Cell implements Cloneable, Serializable {
    private Ship ship;
    private int countHits;  //количество попаданий в данную ячейку
    private Status status;
    private int positionX;
    private int positionY;

    public Cell(int i, int j) {
        this.ship = null;
        this.countHits = 0;
        this.status = Cell.Status.FREE;
        this.positionX = i;
        this.positionY = j;
    }

    //статус ячейки,
    // FREE - свободна, можно ставить корабль
    // SHIP - стоит корабль
    // BOARD - борт корабля, ставить нельзя
    // BOARDDEADSHIP - борт потопленного корабля, ставить нельзя
    public enum Status implements Serializable {
        FREE, SHIP, BOARD, BOARDDEADSHIP
    }


    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
        this.status = Status.SHIP;
    }

    public int getCountHits() {
        return countHits;
    }

    public void setCountHits() {
        this.countHits++;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    @Override
    public Cell clone() {
        Cell result = null;
        try {
            result = (Cell) super.clone();
            result.setShip(ship.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String toString() {
        return "cell " + positionX + "," + positionY + " status: " + status + " Адрес:" + super.toString();
    }
}
