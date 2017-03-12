import model.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static final int PORT = 9090;

    public static void main(String[] args) throws CloneNotSupportedException {
        List<User> players = Collections.synchronizedList(new ArrayList<User>());
        User[] users = new User[2];

        try (ServerSocket server = new ServerSocket(PORT)){
            System.out.println("Сервер запущен.");
            while (true) {
                Socket socket = server.accept();
                Thread thread = new Thread(() -> {
                    PlayerFactory playerFactory = new PlayerFactory();
                    User player = playerFactory.createUser(socket);
                    players.add(player);

                    int count = 0;
                    for (User user :
                            players) {
                        if (user.getState() == User.State.READY_COMPUTER) {
                            Player computer = playerFactory.createComputer();
                            startGame(user,computer);
                        }
                        if (user.getState() == User.State.READY_PEOPLE) {
                            count++;
                            users[count - 1] = user;
                        }
                        if (count == 2) {
                            startGame(users[0], users[1]);
                            count = 0;
                        }
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startGame(Player player1, Player player2) {
        Thread thread = new Thread(new Game(player1, player2));
        thread.start();
    }
}