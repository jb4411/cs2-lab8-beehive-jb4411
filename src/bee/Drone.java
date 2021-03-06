package bee;

import world.BeeHive;

import static bee.Queen.MATE_TIME_MS;

/**
 * The male drone bee has a tough life.  His only job is to mate with the queen
 * by entering the queen's chamber and awaiting his royal highness for some
 * sexy time.  Unfortunately his reward from mating with the queen is his
 * endophallus gets ripped off and he perishes soon after mating.
 *
 * @author Sean Strout @ RIT CS
 * @author Jesse Burdick-Pless jb4411@g.rit.edu
 */
public class Drone extends Bee {
    private boolean mated;
    /**
     * When the drone is created they should retrieve the queen's
     * chamber from the bee hive and initially the drone has not mated.
     *
     * @param beeHive the bee hive
     */
    public Drone(BeeHive beeHive){
        super(Role.DRONE, beeHive);
        this.mated = false;
    }

    /**
     * The queen will let the drone know when they have mated.
     */
    public synchronized void setMated() {
        this.mated = true;
    }

    /**
     * Tells whether or not this drone has mated.
     *
     * @return whether or not this drone has mated
     */
    public synchronized boolean hasMated() {
        return this.mated;
    }

    /**
     * When the drone runs, they check if the bee hive is active.  If so,
     * they perform their sole task of entering the queen's chamber.
     * If they return from the chamber, it can mean only one of two
     * things.  If they mated with the queen, they sleep for the
     * required mating time, and then perish (the beehive should be
     * notified of this tragic event).  You should display a message:<br>
     * <br>
     * <tt>*D* {bee} has perished!</tt><br>
     * <br>
     * <br>
     * Otherwise if the drone has not mated it means they survived the
     * simulation and they should end their run without any
     * sleeping.
     */
    public void run() {
        if (this.beeHive.isActive()) {
            this.beeHive.getQueensChamber().enterChamber(this);
        }
        if (this.mated) {
            this.beeHive.getQueensChamber().removeMatedDrone(this);
            try {
                sleep(MATE_TIME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.beeHive.beePerished(this);
            System.out.println("*D* " + this + " has perished!");
        }
    }
}