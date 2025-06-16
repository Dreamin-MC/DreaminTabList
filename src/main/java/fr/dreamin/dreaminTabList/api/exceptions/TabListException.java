package fr.dreamin.dreaminTabList.api.exceptions;

import java.io.Serial;

/**
 * Base exception for all TabList API errors.
 * 
 * <p>This is the root exception class for all exceptions thrown by the
 * DreaminTabList API. It extends RuntimeException to avoid forcing
 * developers to handle every possible exception, while still providing
 * clear error information when issues occur.
 * 
 * <p>Common scenarios that may throw this exception:
 * <ul>
 *   <li>Invalid configuration</li>
 *   <li>Network/packet errors</li>
 *   <li>Plugin state issues</li>
 * </ul>
 * 
 * @author Dreamin
 * @version 0.0.1
 * @since 0.0.1
 */
public class TabListException extends RuntimeException {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new TabList exception with the specified detail message.
     * 
     * @param message the detail message explaining the cause of the exception
     */
    public TabListException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new TabList exception with the specified detail message and cause.
     * 
     * @param message the detail message explaining the cause of the exception
     * @param cause the cause of this exception (which is saved for later retrieval)
     */
    public TabListException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new TabList exception with the specified cause.
     * 
     * @param cause the cause of this exception (which is saved for later retrieval)
     */
    public TabListException(Throwable cause) {
        super(cause);
    }
}

