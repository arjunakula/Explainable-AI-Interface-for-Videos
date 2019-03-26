package sparql.translator.utilities;

/**
 * The <code>UnableToRespondException</code> exception is used to express the fact that the machine vision
 * system cannot produce an answer to a query.
 *
 * @author Ken Samuel
 * @version 1.0, Mar 1, 2015
 * @since 1.6
 */
public class UnableToRespondException extends Exception {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * The <code>UnableToRespondException</code> constructor is the same as the Exception constructor.
	 * @see java.lang.Exception#Exception()
	 */
	public UnableToRespondException() {
		super();
	}

	/**
	 * The <code>UnableToRespondException</code> constructor is the same as the Exception constructor.
	 *
	 * @param messageIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public UnableToRespondException(String messageIn) {
		super(messageIn);
	}

	/**
	 * The <code>UnableToRespondException</code> constructor is the same as the Exception constructor.
	 *
	 * @param causeIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public UnableToRespondException(Throwable causeIn) {
		super(causeIn);
	}

	/**
	 * The <code>UnableToRespondException</code> constructor is the same as the Exception constructor.
	 *
	 * @param messageIn has the same interpretation as in Exception.Exception().
	 * @param causeIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public UnableToRespondException(String messageIn, Throwable causeIn) {
		super(messageIn, causeIn);
	}

	/**
	 * The <code>UnableToRespondException</code> constructor is the same as the Exception constructor.
	 *
	 * @param messageIn has the same interpretation as in Exception.Exception().
	 * @param causeIn has the same interpretation as in Exception.Exception().
	 * @param enableSuppressionIn has the same interpretation as in Exception.Exception().
	 * @param writableStackTraceIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public UnableToRespondException(String messageIn, Throwable causeIn,
			boolean enableSuppressionIn, boolean writableStackTraceIn) {
		super(messageIn, causeIn, enableSuppressionIn, writableStackTraceIn);
	}
}