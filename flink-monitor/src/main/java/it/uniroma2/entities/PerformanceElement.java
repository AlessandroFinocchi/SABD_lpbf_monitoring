package it.uniroma2.entities;

public abstract class PerformanceElement {
    protected int seqID;
    protected final long arrivalTs;
    protected long processingCompletionTime;

    public PerformanceElement(int seqID, long arrivalTs) {
        this.seqID = seqID;
        this.arrivalTs = arrivalTs;
        this.processingCompletionTime = 0;
    }

    public int getSeqID() {
        return seqID;
    }

    public long getArrivalTs() {
        return arrivalTs;
    }

    public long getProcessingCompletionTime() {
        return processingCompletionTime;
    }

    public void setProcessingCompletionTime(long processingCompletionTime) {
        this.processingCompletionTime = processingCompletionTime;
    }
}
