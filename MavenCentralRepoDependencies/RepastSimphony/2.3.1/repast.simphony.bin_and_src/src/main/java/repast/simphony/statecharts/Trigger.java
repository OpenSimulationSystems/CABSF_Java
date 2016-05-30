package repast.simphony.statecharts;

public interface Trigger {
	public void initialize();
	public boolean isTriggered();
	public boolean isTriggerConditionTrue();
	public boolean isRecurring();
	public double getInterval();
	public void setInterval(double interval);
	public double getNextTime();
	public boolean canTransitionZeroTime();
	public boolean isQueueConsuming();
}
