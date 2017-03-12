package model;

import view.Server;

import java.net.Socket;

/**
 * Created by Александр on 12.03.2017.
 */
public class PlayerFactory {
    public User createUser(Socket socket) {
        User user = new User();
        user.setSocket(socket);

        Server server = new Server(socket);
        server.init();
        user.setGamePresenter(server);
        user.setName(server.askUserString("Введите ваше имя:"));

        Field field = new Field(Constants.sizeField, Field.ViewPosition.LEFT);
        user.setField(field);
        user.setFleet(new Fleet(Constants.countShipDecks));
        user.setShips();
        if (user.getOpponent().equals("people")) {
            user.setState(User.State.READY_PEOPLE);
        } else {
            user.setState(User.State.READY_COMPUTER);
        }
        return user;
    }

    public Computer createComputer() {
        Computer computer = new Computer(4);
        computer.setName("Computer");
        Field field = new Field(Constants.sizeField, Field.ViewPosition.RIGHT);
        computer.setField(field);
        computer.setFleet(new Fleet(Constants.countShipDecks));
        computer.setShips();
        return computer;
    };
}
