package world;

import bee.Drone;
import bee.Queen;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The queen's chamber is where the mating ritual between the queen and her
 * drones is conducted.  The drones will enter the chamber in order.
 * If the queen is ready and a drone is in here, the first drone will
 * be summoned and mate with the queen.  Otherwise the drone has to wait.
 * After a drone mates they perish, which is why there is no routine
 * for exiting (like with the worker bees and the flower field).
 *
 * @author Sean Strout @ RIT CS
 * @author Jesse Burdick-Pless jb4411@g.rit.edu
 */
public class QueensChamber {
    private ConcurrentLinkedQueue<Drone> drones;
    private ConcurrentLinkedQueue<Drone> matedDrones;
    private Queen queen;

    /**
     * Create the chamber. Initially there are no drones in the chamber and the
     * queen is not ready to mate.
     */
    public QueensChamber() {
        this.drones = new ConcurrentLinkedQueue<>();
        this.matedDrones = new ConcurrentLinkedQueue<>();
    }

    /**
     * Are there any drones that have mated?
     *
     * @return if there is at least one drone that has mated
     */
    public synchronized boolean hasMatedDrone() {
        return this.matedDrones.size() > 0;
    }

    /**
     * Set the chamber's queen.
     *
     * @param queen the chamber's queen
     */
    public void setQueen(Queen queen) {
        this.queen = queen;
    }

    /**
     * Get the front drone in the list of drones.
     *
     * @return the front drone in the list of drones
     */
    public synchronized Drone getTopDrone() {
        return this.drones.peek();
    }

    /**
     * Remove a drone from the list of mated drones.
     *
     * @param drone the drone to be removed
     */
    public synchronized void removeMatedDrone(Drone drone) {
        this.matedDrones.remove(drone);
    }

    /**
     * Remove the top drone from the list of drones that have not yet mated.
     */
    public synchronized void removeTopDrone() {
        this.matedDrones.add(this.drones.peek());
        this.drones.remove(this.drones.peek());
    }

    /**
     * A drone enters the chamber. The first thing you should display is:
     *
     * *QC* {bee} enters chamber
     *
     * The bees should be stored in some queue like collection. If the queen is
     * ready and this drone is at the front of the collection, they are allowed
     * to mate. Otherwise they must wait. The queen isn't into any of this
     * kinky multiple partner stuff so while she is mating with a drone, she is
     * not ready to mate again. When the drone leaves this method, display the message:
     *
     * *QC* {bee} leaves chamber
     *
     * @param drone the drone who just entered the chamber
     */
    public synchronized void enterChamber(Drone drone) {
        System.out.println("*QC* " + drone + " enters chamber");
        this.drones.add(drone);
        while (this.drones.peek() != drone && !this.matedDrones.contains(drone) || !this.queen.isReadyToMate()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("*QC* " + drone + " leaves chamber");
        if (drone.hasMated() && !this.matedDrones.contains(drone)) {
            this.matedDrones.add(drone);
        }
        this.drones.remove(drone);
    }

    /**
     * When the queen is ready, they will summon the next drone from the
     * collection (if at least one is there). The queen will mate with the
     * first drone and display a message:
     *
     * *QC* Queen mates with {bee}
     *
     * It is the job of the queen if mating to notify all of the waiting drones
     * so that the first one can be selected since we can't control which drone
     * will unblock. Doing a notify will lead to deadlock if the drone that
     * unblocks is not the front one.
     *
     * @rit.pre A drone is ready and waiting to mate
     */
    public synchronized void summonDrone() {
        notifyAll();
    }

    /**
     * At the end of the simulation the queen uses this routine repeatedly to
     * dismiss all the drones that were waiting to mate. #rit_irl...
     */
    public synchronized void dismissDrone() {
        notifyAll();
    }

    /**
     * Are there any waiting drones? The queen uses this to check if she can
     * mate, and also in conjunction with dismissDrone().
     *
     * @return if there is still a drone waiting
     */
    public synchronized boolean hasDrone() {
        return this.drones.size() > 0;
    }
}