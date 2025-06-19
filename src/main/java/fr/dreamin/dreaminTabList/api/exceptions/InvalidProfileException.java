package fr.dreamin.dreaminTabList.api.exceptions;

import java.io.Serial;

/**
 * Thrown when a profile configuration is invalid or cannot be processed.
 * 
 * <p>This exception is typically thrown when:
 * <ul>
 *   <li>Required profile fields are missing or null</li>
 *   <li>Profile values are outside acceptable ranges</li>
 *   <li>Skin data is malformed or invalid</li>
 *   <li>Profile names contain invalid characters</li>
 * </ul>
 * 
 * <p>Example scenarios:
 * <pre>{@code
 * // This would throw InvalidProfileException
 * TabProfile profile = api.getProfileManager()
 *     .createProfile()
 *     .name("")  // Empty name is invalid
 *     .latency(-1)  // Negative latency is invalid
 *     .build();
 * }</pre>
 * 
 * @author Dreamin
 * @version 0.0.3
 * @since 0.0.1
 */
public class InvalidProfileException extends TabListException {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new invalid profile exception with the specified detail message.
     * 
     * @param message the detail message explaining why the profile is invalid
     */
    public InvalidProfileException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new invalid profile exception with the specified detail message and cause.
     * 
     * @param message the detail message explaining why the profile is invalid
     * @param cause the underlying cause of the invalid profile
     */
    public InvalidProfileException(String message, Throwable cause) {
        super(message, cause);
    }
}

