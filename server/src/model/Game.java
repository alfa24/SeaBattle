package model;

import exceptions.NoPointForShot;

import java.io.IOException;

/**
 * Created by ФаличевАЮ on 14.10.2016.
 */
public class Game implements Runnable {
    private Player[] players;

    public Game(Player player1, Player player2) {
        players = new Player[2];
        players[0] = player1;
        players[1] = player2;
    }

    public void init() {
        players[0].setFieldForAttack(players[1].getField());
        players[1].setFieldForAttack(players[0].getField());
    }

    public void start() {
        boolean firstPlayer = true;
        Boolean endGame = false;

        updateGui(firstPlayer, endGame);

        do {
            Field.Point point = null;
            Player player = currentPlayer(firstPlayer);
            try {
                //ожидание ввода данных
                point = input(player);
            } catch (NoPointForShot | InterruptedException | IOException | ClassNotFoundException noPointForShot) {
                broadcast("Ваш соперник отключился.");
                disconnectPlayers();
            }

            //выстрел
            if (!shoot(player, point)) {
                firstPlayer = !firstPlayer;
            }

            endGame = endGame();
            updateGui(firstPlayer, endGame);

        } while (!endGame);
        broadcast("Конец игры! Победу одержал " + currentPlayer(firstPlayer).getName());
    }

    private void disconnectPlayers() {
        for (int i = 0; i < 2; i++) {
            if (players[i] instanceof User) {
                User user = (User) players[i];
                user.disconnect();
            }
        }
    }

    private void broadcast(String msg) {
        for (int i = 0; i < 2; i++) {
            if (players[i] instanceof User) {
                User user = (User) players[i];
                user.msg(msg);
            }
        }
    }

    private Player currentPlayer(boolean firstPlayer) {
        Player player;
        if (firstPlayer) {
            player = players[0];
        } else {
            player = players[1];
        }
        return player;
    }

    private void updateGui(boolean firstPlayer, Boolean endGame) {
        if (players[0] instanceof User) {
            User user = (User) players[0];
            user.updateGui(firstPlayer, endGame);
        }

        if (players[1] instanceof User) {
            User user = (User) players[1];
            user.updateGui(!firstPlayer, endGame);
        }
    }

    private boolean shoot(Player player, Field.Point point) {
        Field field;
        field = player.getFieldForAttack();
        return field.fire(point);
    }

    private Field.Point input(Player player) throws NoPointForShot, InterruptedException, IOException, ClassNotFoundException {
        return player.getShoot();
    }

    private boolean endGame() {
        boolean result = false;
        for (int i = 0; i < 2; i++) {
            if (players[i] instanceof User) {
                User user = (User) players[i];
                if (!user.isConnected()) {
                    return true;
                }
            }
            if (players[i].getFleet().isDead()) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public void run() {
        init();
        start();
    }
}
