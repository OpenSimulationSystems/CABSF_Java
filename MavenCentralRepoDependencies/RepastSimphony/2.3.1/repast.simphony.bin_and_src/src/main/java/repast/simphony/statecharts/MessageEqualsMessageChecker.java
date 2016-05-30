package repast.simphony.statecharts;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import simphony.util.messages.MessageCenter;

public class MessageEqualsMessageChecker<T, U> extends
		AgentTransitionMessageChecker<T> {

	private MessageEquals<T> messageEquals;
	private Class<? extends U> messageClass;
	private Parameters params;

	protected Parameters getParams() {
		if (params == null) {
			RunEnvironment re = RunEnvironment.getInstance();
			if (re != null)
				params = re.getParameters();
		}
		return params;
	}

	public MessageEqualsMessageChecker(MessageEquals<T> messageEquals,
			Class<? extends U> messageClass) {
		this.messageEquals = messageEquals;
		this.messageClass = messageClass;
	}

	public MessageEqualsMessageChecker(final U messageEquals,
			Class<? extends U> messageClass) {
		this(new MessageEquals<T>() {
			@Override
			public Object messageValue(T agent, Transition<T> transition,
					Parameters params) throws Exception {
				return messageEquals;
			}

			@Override
			public String toString() {
				return messageEquals.toString();
			}
		}, messageClass);
	}

	@Override
	public boolean checkMessage(Object message) {
		boolean result = false;

		if (messageClass.isInstance(message)) {
			try {
				result = message.equals(messageEquals.messageValue(getAgent(),
						transition, getParams()));
			} catch (Exception e) {
				MessageCenter.getMessageCenter(getClass()).error(
						"Error encountered when calling message equals in: "
								+ this, e);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "MessageEqualsMessageChecker with " + messageEquals;
	}

}
