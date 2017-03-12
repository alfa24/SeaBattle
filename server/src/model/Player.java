package model;

import exceptions.NoPointForShot;

import java.io.IOException;

/**
 * Created by ФаличевАЮ on 14.10.2016.
 */
public abstract class Player {
    private String name;
    private Fleet fleet;
    private Field field;  //main field
    private Field fieldForAttack;

    public Player() {
    }

    public Field getFieldForAttack() {
        return fieldForAttack;
    }

    public void setFieldForAttack(Field fieldForAttack) {
        this.fieldForAttack = fieldForAttack;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract void setShips();

    public abstract Field.Point getShoot() throws NoPointForShot, IOException, ClassNotFoundException, InterruptedException;
}
