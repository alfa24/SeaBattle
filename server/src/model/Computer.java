package model;

import exceptions.NoPointForShot;

/**
 * Created by Александр on 22.01.2017.
 */
public class Computer extends Player{

    //skils - умения компьютера
    // 4 - умеет расставлять корабли по Перельману
    // 3 - умеет стрелять систематично
    // 2 - умеет добивать корабль
    // 1 - ребенок (не умеет даже добивать корабль)
    private int skills;

    public Computer(int skills) {
        super();
        this.skills = skills;
    }

    // todo id вот здесь вероятно придется изменить механику при переходе к GUI, так как с GUI будет проще все через состояния реализовать. Подробнее поищите: state machine
    @Override
    public void setShips() {
        super.getField().setShipsAuto(skills, super.getFleet());
    }

    @Override
    public Field.Point getShoot() throws NoPointForShot {
        Field fieldForAttack = getFieldForAttack();
        return fieldForAttack.fireAuto(skills);
    }

}
