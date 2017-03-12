package model;

import exceptions.NoPointForShot;
import exceptions.OutOfShipBoundary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static model.Cell.Status.BOARD;
import static model.Cell.Status.SHIP;

/**
 * Created by ФаличевАЮ on 14.10.2016.
 */
public class Field implements Cloneable, Serializable {

    private int sizeField;
    private Cell[][] cells;
    private ViewPosition viewPosition;

    public enum ViewPosition implements Serializable {LEFT, RIGHT}

    public Field(int sizeField, ViewPosition viewPosition) {
        this.sizeField = sizeField;
        this.viewPosition = viewPosition;
        init();
    }

    public void init() {
        cells = new Cell[sizeField][sizeField];
        clean();
    }

    private void clean() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public ViewPosition getViewPosition() {
        return viewPosition;
    }

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

    //Расчитывает координаты точки, в зависимости от typePosition
    /*typePosition - вертикальная или горизонтальная позиция
    x,y - исходные координаты точки
    i,j - коэффициент на который необходимо увеличить x,y в зависимости от typePosition     */
    private Point toCalculatePoint(Ship.TypePosition typePosition, int x, int y, int i, int j) {
        if (typePosition == Ship.TypePosition.VERTICAL) {
            return new Point(x + j, y + i);
        }

        return new Point(x + i, y + j);
    }

    public Cell[][] getCells() {  //используется при отрисовки поля
        return cells;
    }

    // 12.12.16 отлично, ручную расстановку сложно тестировать, поэтому в последнюю очередь стоит делать
    public void setShipsManual(Fleet fleet) { //заглушка
    }

    //расставляет корабли на карте в зависимости от уровня игрока
    public void setShipsAuto(int skills, Fleet fleet) {
        ArrayList<Ship> listShipsPosition; //список разнообразных комбинаций размещения кораблей
        ArrayList<Ship> fleetShips = fleet.shuffleShips();  //получаем список перемешанных кораблей
        ArrayList<Ship> ships = new ArrayList<>();  //список кораблей размещенных на поле

        int busyCell; //количесвто занимаемых ячеек кораблями (чем выше skills тем меньше ячеек занимают многопалубные корабли)
        switch (skills) {
            case 4:
                busyCell = 2;
                break;
            case 3:
                busyCell = (sizeField * sizeField) / 10;
                break;
            case 2:
                busyCell = (sizeField * sizeField) / 5;
                break;
            default:
                busyCell = sizeField * sizeField;
                break;
        }

        for (Ship ship
                : fleetShips) {

            if (ship.getCountDecks() == 1) {
                busyCell += sizeField * sizeField;
            }
            busyCell += ship.getCountDecks() * 2; //определяем сколько должно быть занято ячеек на поле
            do {
                listShipsPosition = getListShipsPosition(ship, ships, busyCell); //получаем список всех возможных размещений этого коробля на нашем поле
                if (listShipsPosition.size() > 0) {
                    int rnd = new Random().nextInt(listShipsPosition.size()); //выбираем 1 вариант
                    Ship newShip = listShipsPosition.get(rnd); // сохраняем этот корабль
                    ships.add(newShip);                         //в список
                    setShipToField(newShip);                    //устанавливаем на карту
                    break;
                } else {
                    busyCell += 1;
                }
            } while (true);
        }
        fleet.setShips(ships);
    }

    //Возвращает список всевозможного размещения корабля на поле в зависимости от условия
    //ship - корабль который необходимо разместить
    //ships - список уже размещенных кораблей
    //countBusyCell максимальное количество занятых ячеек на поле с установленным кораблем ship
    private ArrayList<Ship> getListShipsPosition(Ship currentShip, ArrayList<Ship> ships, int countBusyCell) {
        ArrayList<Ship> listShipsPosition = new ArrayList<>();  //список кораблей с координатами, подходящими под наши условия countBusyCell

        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {  //перебираем все ячейки поля
                Cell cell = cells[i][j];
                if (cell.getStatus() == Cell.Status.FREE) { //если ячейка свободна
                    for (int l = 0; l < 2; l++) { //выполняем 2 раза для вертикального и горизонтального размещения корабля
                        Ship.TypePosition typePosition = (l == 0) ? Ship.TypePosition.HORIZONTAL : Ship.TypePosition.VERTICAL;


                        if (ifSetShipToField(currentShip.getCountDecks(), i, j, typePosition)) { //если в эту ячейку можно поместить наш корабль
                            Field fieldTmp = new Field(sizeField, viewPosition);
                            Ship shipTmp = (Ship) currentShip.clone(); //работаем с клонированным кораблем
                            shipTmp.setPosition(i, j, typePosition); //записываем в него координаты
                            ships.add(shipTmp);                     //Добавляем в список

                            fieldTmp.setShipsToField(ships); //размещаем корабли ships на временном поле, чтобы можно было посмотреть сколько свободных ячеек останется

                            if (fieldTmp.countFreeCell() >= ((sizeField * sizeField) - countBusyCell)) {  //если количество занятых ячеек меньше countBusyCell
                                listShipsPosition.add(shipTmp);                                            //то добавляем этот вариант в список
                            }

                            ships.remove(shipTmp); //удаляем, для проверки следующего варианта
                        }


                    }
                }
            }
        }

        return listShipsPosition;
    }

    //количество свободных ячеек.
    private int countFreeCell() {
        int k = 0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                Cell cell = cells[i][j];
                if (cell.getStatus() == Cell.Status.FREE) {
                    k++;
                }
            }
        }
        return k;
    }

    //Метод проверяет, можно ли установить корабль на карту по заданным координатам
    private boolean ifSetShipToField(int countDecks, int i, int j, Ship.TypePosition typePosition) {
        boolean result = true;
        for (int k = 0; k < countDecks; k++) {
            Point point = toCalculatePoint(typePosition, i, j, k, 0);
            Cell cell = getCell(point);
            if (cell == null) {
                result = false;
            }

            if (typePosition != Ship.TypePosition.NONE & result) {
                switch (cell.getStatus()) {
                    case BOARD:  //если в данной ячейке уже стоит рядом корабль
                        result = false;
                        break;
                    case SHIP:  //если в данной ячейке уже стоит корабль
                        result = false;
                        break;
                }
            }
        }
        return result;
    }

    //устанавливает корабль в ячейки и помечает соседние ячейки как занятык
    private boolean setShipToField(Ship ship) {
        int i = ship.getPositionX();
        int j = ship.getPositionY();
        Ship.TypePosition typePosition = ship.getPosition();
        int countDecks = ship.getCountDecks();

        if (!ifSetShipToField(countDecks, i, j, typePosition)) {
            return false;
        }

        for (int k = 0; k < countDecks; k++) {
            Point point = toCalculatePoint(typePosition, i, j, k, 0);
            Cell cell = getCell(point);
            if (cell != null) {
                cell.setShip(ship);
            }
        }
        setBoardShip(ship);

        return true;
    }

    // Прописывает флотилию на поле
    private void setShipsToField(ArrayList<Ship> ships) {
        clean(); //очищаем поле от кораблей

        for (Ship ship :
                ships) {
            if (ship.getPositionY() >= 0 & ship.getPositionY() >= 0) {
                setShipToField(ship);
            }
        }
    }

    //метод помечает клетки вокруг корабля как "занятые"
    private void setBoardShip(Ship ship) {
        Ship.TypePosition typePosition = ship.getPosition();
        Cell.Status status;
        if (ship.isDead()) {
            status = Cell.Status.BOARDDEADSHIP;  //если корабль убит, то помечаем соседние ячейки как "стрелянные"
        } else {
            status = BOARD;
        }

        for (int i = -1; i <= ship.getCountDecks(); i++) { //перебирает ячейки по всем палубам корабля, включая 2 ячейки по краям корабля
            Point tmp = toCalculatePoint(typePosition, ship.getPositionX(), ship.getPositionY(), i, 0);

            for (int j = -1; j <= 1; j++) { //перебирает соседние ячейки поперек корабля
                Point point = toCalculatePoint(typePosition, tmp.getX(), tmp.getY(), 0, j);
                Cell cell = getCell(point);
                if (cell != null) {
                    if (cell.getStatus() != SHIP) {
                        cell.setStatus(status);
                    }
                }
            }
        }
    }

    //возвращает ячейку Cell из точки Point, либо null, если точка вне массива cells[][]
    private Cell getCell(Point point) {
        int x = point.getX();
        int y = point.getY();
        if (cells != null) {
            if (x >= 0 & y >= 0 & x < cells.length & y < cells[0].length) {
                return cells[x][y];
            }
        }
        return null;
    }

    //метод выбирает координаты выстрела автоматически
    public Point fireAuto(int skills) throws NoPointForShot {
        ArrayList<Cell> listCell = null;

        if (skills > 1) {
            listCell = selectCellForFireShip(getDamageShip());  //сначало стреляем недобитые корабли
        }

        if (listCell == null) {
            listCell = selectCellForFire(skills);
        }

        int ind = 0;
        if (listCell.size() > 1) {
            ind = new Random().nextInt(listCell.size());       //выбираем случайный вариант
        }

        if (listCell.size() > 0) {
            int x = listCell.get(ind).getPositionX();
            int y = listCell.get(ind).getPositionY();
            return new Point(x, y);
        }
        throw new NoPointForShot("Компьютеру не удалось найти координаты для выстрела");
    }

    /*    Возвращает ИСТИНА, если попали в первый раз и помечает палубу корабля как поврежденную     */
    public boolean fire(Point point) {
        boolean result = false;
        Cell cell = getCell(point);
        if (cell != null) {
            if (cell.getStatus() == SHIP) {
                if (cell.getCountHits() == 0) { //если это не повторный выстрел
                    Ship ship = cell.getShip();
                    try {
                        ship.damage(cell.getPositionX(), cell.getPositionY());
                        if (ship.isDead()) {
                            setBoardShip(ship);
                        }
                        result = true;
                    } catch (OutOfShipBoundary e) {
                        System.err.println(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            cell.setCountHits();
        }
        return result;
    }

    /*    Ищем все координаты возможного расположения кораблей противника
            Если Умения хватает, то Ходы выбираем по диагонали,так чтобы не было возможности пропустить самый большой, на данный момент
            не поврежденный корабль
            Иначе простреливаем все ячейки подряд*/
    private ArrayList<Cell> selectCellForFire(int skills) {
        ArrayList<Cell> listCell = new ArrayList<Cell>();

        int shipCountDecks = 1;
        if (skills > 2) {
            shipCountDecks = getSizeLargeLivingShip();
        }

        int k = shipCountDecks - 1;
        for (int i = 0; i < cells.length; i++) {
            k++;
            if (k == shipCountDecks) {
                k = 0;
            }
            int j = k;

            do {
                if (cells[i][j].getCountHits() == 0 & cells[i][j].getStatus() != Cell.Status.BOARDDEADSHIP) { //попаданий по ячейке 0 И это ячейка не рядом с потопленным кораблем
                    listCell.add(cells[i][j]);
                }
                j += shipCountDecks;  // стреляем со сдвигом (размер самого большого необнаруженного корабля), так чтобы корабль был быстрее обнаружен
            } while (j < cells[0].length);

        }
        return listCell;
    }

    /* Если ранена одна палуба, то метод возвращает 4 возможных координаты для выстрела.
        Если повреждены уже 2 палубы, то уже можно определить какой тип корабля (верт или гориз.)
        и соответсвенно ментод возвращает 2 координаты по краям поврежденных палуб.     */
    private ArrayList<Cell> selectCellForFireShip(Ship ship) { //возвращает список координат, для добивания корабля
        ArrayList<Cell> listCell = null;
        if (ship != null) {
            listCell = new ArrayList<>();
            Ship.TypePosition typePosition = ship.getPosition();
            for (int i = 0; i < ship.getCountDecks(); i++) {
                Point pos = toCalculatePoint(typePosition, ship.getPositionX(), ship.getPositionY(), i, 0);
                int x = pos.getX();
                int y = pos.getY();


                Point tmp = toCalculatePoint(typePosition, 0, 0, 1, 0); //устанавливаем координаты соседних ячеек в зависимости от вертикального или горизонтального расположенния
                int kx = tmp.getX();
                int ky = tmp.getY();

                try {
                    if (ship.getStatusDecks(x, y) == Ship.StatusDeck.DAMAGE) {  //если палуба в статусе (повреждена)
                        do {
                            Point point = new Point(x + kx, y + ky);
                            Cell cell = getCell(point);
                            if (cell != null) {
                                if (cell.getCountHits() == 0) {
                                    listCell.add(cell);
                                }
                            }
                            kx *= -1;
                            ky *= -1;

                            //если повреждена 1 палуба, то игрок еще не знает вертикальный или горизонтальный корабль
                            //соотв-но добавляем координаты для 2х типов коробля (верт или гориз)
                            if (ship.countDecksDamage() == 1 & kx >= 0 & ky >= 0) {
                                int tmp1 = kx;
                                kx = ky;
                                ky = tmp1;
                            }

                            if (kx == tmp.getX() & ky == tmp.getY()) {
                                break;
                            }
                        } while (true);
                    }
                } catch (OutOfShipBoundary e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return listCell;
    }

    // Получить размер самого большого целого корабля на поле
    private int getSizeLargeLivingShip() {
        int shipCountDecks = 1;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (cells[i][j].getStatus() == SHIP) {
                    Ship ship = cells[i][j].getShip();
                    if (!ship.isDead()) {
                        if (shipCountDecks < ship.getCountDecks()) {
                            shipCountDecks = ship.getCountDecks();  //Определяем размер самого большого неподбитый корабль
                        }
                    }
                }
            }
        }
        return shipCountDecks;
    }

    /*    Возвращает первый недобитый корабль.     */
    private Ship getDamageShip() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                if (cells[i][j].getStatus() == SHIP) {
                    Ship ship = cells[i][j].getShip();
                    if (ship.isDamage() & !ship.isDead()) {
                        return ship;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected Field clone() throws CloneNotSupportedException {
        Cell[][] cellsClone = new Cell[sizeField][sizeField];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                cellsClone[i][j] = cells[i][j].clone();
            }
        }
        cells = cellsClone;
        Field result = null;
        try {
            result = (Field) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return result;
    }

}
