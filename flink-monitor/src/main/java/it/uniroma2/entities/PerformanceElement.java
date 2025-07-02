package it.uniroma2.entities;

public abstract class PerformanceElement {
    protected int seqID;
    protected final double arrivalTs;
    protected double processingCompletionTime;

    public PerformanceElement(int seqID, double arrivalTs) {
        this.seqID = seqID;
        this.arrivalTs = arrivalTs;
        this.processingCompletionTime = 0;
    }

    public int getSeqID() {
        return seqID;
    }

    public double getArrivalTs() {
        return arrivalTs;
    }

    public double getProcessingCompletionTime() {
        return processingCompletionTime;
    }

    public void setProcessingCompletionTime(double processingCompletionTime) {
        this.processingCompletionTime = processingCompletionTime;
    }
}
