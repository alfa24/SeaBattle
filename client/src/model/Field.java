package model;
import java.io.Serializable;


/**
 * Created by ФаличевАЮ on 14.10.2016.
 */
public class Field implements Cloneable, Serializable {
    // Класс для удобства работы с ячейками cells, хранит 2 значения
    public static class Point implements Serializable {
        private int x;
        private int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }


}
