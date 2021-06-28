package todo.tools;

public class Filter {
    boolean prioLow = false;
    boolean prioMedium = false;
    boolean prioHigh = false;
    boolean stateInProgress = false;
    boolean stateWait = false;
    boolean stateDone = false;

    public boolean getPrioLow() {
        return prioLow;
    }

    public void setPrioLow(boolean prioLow) {
        this.prioLow = prioLow;
    }

    public boolean getPrioMedium() {
        return prioMedium;
    }

    public void setPrioMedium(boolean prioMedium) {
        this.prioMedium = prioMedium;
    }

    public boolean getPrioHigh() {
        return prioHigh;
    }

    public void setPrioHigh(boolean prioHigh) {
        this.prioHigh = prioHigh;
    }

    public boolean getStateInProgress() {
        return stateInProgress;
    }

    public void setStateInProgress(boolean stateInProgress) {
        this.stateInProgress = stateInProgress;
    }

    public boolean getStateWait() {
        return stateWait;
    }

    public void setStateWait(boolean stateWait) {
        this.stateWait = stateWait;
    }

    public boolean getStateDone() {
        return stateDone;
    }

    public void setStateDone(boolean stateDone) {
        this.stateDone = stateDone;
    }

    public boolean isSomeOneFiltersActive() {

        return getPrioLow() || getPrioMedium() || getPrioHigh() || getStateInProgress() || getStateWait() || getStateDone();
    }
}
