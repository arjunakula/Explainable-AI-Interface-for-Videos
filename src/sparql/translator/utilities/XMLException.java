package sparql.translator.utilities;

/**
 * The <code>XMLException</code> exception is used for illegal XML strings.
 *
 * @author Ken Samuel
 * @version 1.0, Feb 26, 2015
 * @since 1.6
 */
public class XMLException extends Exception {

	/** <code>serialVersionUID</code> is needed, because this class implements Serializable. */
	private static final long serialVersionUID = 7526472295622776147L;

	/**
	 * The <code>XMLException</code> constructor is the same as the Exception constructor.
	 * @see java.lang.Exception#Exception()
	 */
	public XMLException() {
		super();
	}

	/**
	 * The <code>XMLException</code> constructor is the same as the Exception constructor.
	 *
	 * @param messageIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public XMLException(String messageIn) {
		super(messageIn);
	}

	/**
	 * The <code>XMLException</code> constructor is the same as the Exception constructor.
	 *
	 * @param causeIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public XMLException(Throwable causeIn) {
		super(causeIn);
	}

	/**
	 * The <code>XMLException</code> constructor is the same as the Exception constructor.
	 *
	 * @param messageIn has the same interpretation as in Exception.Exception().
	 * @param causeIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public XMLException(String messageIn, Throwable causeIn) {
		super(messageIn, causeIn);
	}

	/**
	 * The <code>XMLException</code> constructor is the same as the Exception constructor.
	 *
	 * @param messageIn has the same interpretation as in Exception.Exception().
	 * @param causeIn has the same interpretation as in Exception.Exception().
	 * @param enableSuppressionIn has the same interpretation as in Exception.Exception().
	 * @param writableStackTraceIn has the same interpretation as in Exception.Exception().
	 * @see java.lang.Exception#Exception()
	 */
	public XMLException(String messageIn, Throwable causeIn,
			boolean enableSuppressionIn, boolean writableStackTraceIn) {
		super(messageIn, causeIn, enableSuppressionIn, writableStackTraceIn);
	}
}