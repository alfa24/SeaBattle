package model;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ФаличевАЮ on 14.10.2016.
 */
public class Fleet implements Serializable {
    private int[][] countShipDecks;
    private ArrayList<Ship> ships;

    public Fleet(int[][] countShipDecks) {
        this.countShipDecks = countShipDecks;
        init();
    }

    //Создаем массив кораблей
    public void init() {
        ships = new ArrayList<>();
        for (int i = 0; i < countShipDecks[0].length; i++) {
            int countDecks = countShipDecks[1][i];
            for (int j = 0; j < countShipDecks[0][i]; j++) {
                Ship ship = new Ship(countDecks);
                ships.add(ship);
            }
        }
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    //Возвращает TRUE если флот разгромлен
    public boolean isDead() {
        boolean result = true;
        for (Ship ship :
                ships) {
            if (!ship.isDead()) {
                result = false;
            }
        }
        return result;
    }

    public ArrayList<Ship> shuffleShips() {
        Comparator<Ship> sortShipComarator = new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                int result = 0;
                if (o1.getCountDecks() == 1) {
                    result = 1;
                }
                if (o2.getCountDecks() == 1) {
                    result = -1;
                }

                if (o1.getCountDecks() == o2.getCountDecks()) {
                    result = 0;
                }
                return result;
            }
        };
        Collections.shuffle(getShips());  //перемешиваем список кораблей
        ships.sort(sortShipComarator); //сортируем, так чтобы однопалубные были внизу списка
        return getShips();
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }
}
