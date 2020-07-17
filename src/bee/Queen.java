package bee;

import util.RandomBee;
import world.BeeHive;
import world.QueensChamber;

/**
 * The queen is the master of the bee hive and the only bee that is allowed
 * to mate with the drones.  The way the queen works is she will try to
 * mate with a drone if these two conditions are met:<br>
 * <br>
 * <ul>
 *     <li>The bee hive has 1 unit of nectar and 1 unit of pollen</li>
 *     <li>There is a drone available and ready to do the wild thing</li>
 * </ul>
 * <br>
 * After the stimulating mating session which takes one unit of time,
 * the queen produces between 1 and 4 new bees (if resources exist).
 * Finally, the queen takes a break and smokes a cigarette and puts on some
 * netflix before she chills with her next drone.
 *
 * @author Sean Strout @ RIT CS
 * @author Jesse Burdick-Pless jb4411@g.rit.edu
 */
public class Queen extends Bee {
    /**
     * the amount of time the queen waits after performing a task, whether she mated
     * this specific time or not.
     */
    public final static int SLEEP_TIME_MS = 1000;
    /** the time it takes for the queen and the drone to mate */
    public final static int MATE_TIME_MS = 1000;
    /** the minimum number of new bees that will be created by one mating session */
    public final static int MIN_NEW_BEES = 1;
    /** the maximum number of new bees that will be created by one mating session */
    public final static int MAX_NEW_BEES = 4;

    private boolean readyToMate;

    /**
     * Create the queen.  She should get the queen's chamber from the bee hive.
     *
     * @param beeHive the bee hive
     */
    public Queen(BeeHive beeHive) {
        super(Role.QUEEN, beeHive);
        this.readyToMate = false;
    }

    public boolean isReadyToMate() {
        return this.readyToMate;
    }

    /**
     * The queen will continue performing her task of mating until the bee hive
     * becomes inactive. Each time she tries to mate, whether successful or not,
     * she will sleep for the required time.
     * The queen will first check that both conditions are met (see the class
     * level description).  If so, the queen will summon the next drone,
     * and sleep to simulate the mating time.  Next,
     * the queen will roll the dice to see how many bees she should
     * try and create, between the min and max inclusive.  Each time there are
     * enough resources a new bee is created.  The bees are created based on
     * another random dice roll - a nectar worker bee has a 20% chance
     * of being created, a pollen bee has a 20% change of being created,
     * and a drone has a 60% change of being created.  After all the bees
     * are created for a single mating message, you should display:<br>
     * <br>
     * <tt>*Q* Queen birthed # children</tt><br>
     * <br>
     * <br>
     * When the simulation is over and before the queen can retire, she needs
     * to make sure that she individually dismisses each drone that is
     * still waiting in her chamber.
     */
    public void run() {
        this.beeHive.getQueensChamber().setQueen(this);
        while (this.beeHive.isActive()) {
            if (this.beeHive.hasResources()) {
                this.readyToMate = true;
                if (this.beeHive.getQueensChamber().hasDrone()) {
                    this.beeHive.getQueensChamber().summonDrone();
                    this.readyToMate = false;
                    try {
                        sleep(SLEEP_TIME_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int num_bees = RandomBee.nextInt(MIN_NEW_BEES, MAX_NEW_BEES);
                    int i = num_bees;
                    while (this.beeHive.hasResources() && i > 0) {
                        this.beeHive.claimResources();
                        int bee_type =RandomBee.nextInt(1, 10);
                        if (bee_type > 4) {
                            this.beeHive.addBee(Bee.createBee(Role.DRONE, Worker.Resource.NONE, this.beeHive));
                        } else if (bee_type < 3) {
                            this.beeHive.addBee(Bee.createBee(Role.WORKER, Worker.Resource.POLLEN, this.beeHive));
                        } else {
                            this.beeHive.addBee(Bee.createBee(Role.WORKER, Worker.Resource.NECTAR, this.beeHive));
                        }
                        i--;
                    }
                    num_bees -= i;
                    System.out.println("*Q* Queen birthed " + num_bees + " children");
                }
            } else {
                this.readyToMate = false;
            }
        }
        this.readyToMate = true;
        for (int i = 0; i < this.beeHive.getQueensChamber().dronesRemaining(); i++) {
            this.beeHive.getQueensChamber().dismissDrone();
        }

        this.beeHive.getQueensChamber().setDismissed();
        while (this.beeHive.getQueensChamber().hasDrone()) {
            this.beeHive.getQueensChamber().dismissDrone();
        }
    }
}