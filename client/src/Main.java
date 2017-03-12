import controller.GamePresenter;
import model.Game;
import view.GameInterface;
import view.GameWindow;

public class Main {
    /**
     * Created by ФаличевАЮ on 14.10.2016.
     */
    public static void main(String[] args) throws CloneNotSupportedException {
        //view
        GameInterface gameInterface = GameWindow.getInstance();
        gameInterface.init();

        //presenter
        GamePresenter gamePresenter = GamePresenter.getInstance();
        gamePresenter.setInterface(GameWindow.getInstance());

        //model
        Game game = Game.getInstance();
        game.init();
        game.start();
    }
}