package fr.dreamin.dreaminTabList.api;

import org.jetbrains.annotations.NotNull;

/**
 * Factory for creating and accessing TabList API instances.
 *
 * <p>This factory provides the main entry point for accessing the DreaminTabList API.
 * It follows the singleton pattern to ensure there is only one API instance
 * per plugin instance.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Get the API instance
 * TabListAPI api = TabListAPIFactory.getAPI();
 *
 * // Use the API
 * api.hideTabForAll();
 *
 * // Check if API is available
 * if (TabListAPIFactory.isInitialized()) {
 *     TabListAPI api = TabListAPIFactory.getAPI();
 *     // Use API safely
 * }
 * }</pre>
 *
 * <p><strong>Note:</strong> The API must be initialized by the DreaminTabList plugin
 * before it can be used. Attempting to access the API before initialization
 * will result in an {@link IllegalStateException}.
 *
 * @author Dreamin
 * @version 0.0.1
 * @since 0.0.1
 */
public final class TabListAPIFactory {

  private static TabListAPI instance;
  private static boolean initialized = false;

  /**
   * Private constructor to prevent instantiation.
   * This is a utility class and should not be instantiated.
   */
  private TabListAPIFactory() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Gets the TabList API instance.
   *
   * <p>This method returns the singleton instance of the TabList API.
   * The API must be initialized by the DreaminTabList plugin before
   * this method can be called successfully.
   *
   * @return the API instance, never null
   * @throws IllegalStateException if the API has not been initialized yet
   * @since 0.0.1
   * @see #isInitialized()
   */
  @NotNull
  public static TabListAPI getAPI() {
    if (!initialized || instance == null) throw new IllegalStateException("TabList API is not initialized. Make sure the DreaminTabList plugin is loaded and enabled.");
    return instance;
  }

  /**
   * Checks if the TabList API has been initialized.
   *
   * <p>This method can be used to safely check if the API is available
   * before attempting to use it.
   *
   * @return true if the API is initialized and ready to use, false otherwise
   * @since 0.0.1
   * @see #getAPI()
   */
  public static boolean isInitialized() {
    return initialized && instance != null;
  }

  /**
   * Gets the version of the API that is currently loaded.
   *
   * <p>This method can be called even if the API is not initialized,
   * as it returns the version of the API classes themselves.
   *
   * @return the API version string
   * @since 0.0.1
   */
  @NotNull
  public static String getAPIVersion() {
    return "0.0.1";
  }

  /**
   * Initializes the API with the specified implementation.
   *
   * <p><strong>Internal use only.</strong> This method is called by the
   * DreaminTabList plugin during initialization and should not be called
   * by external code.
   *
   * <p>This method is public to allow access from the implementation package,
   * but should be considered internal API and may change without notice.
   *
   * @param apiInstance the API implementation instance, must not be null
   * @throws IllegalStateException if the API is already initialized
   * @throws IllegalArgumentException if apiInstance is null
   * @since 0.0.1
   */
  public static void initialize(@NotNull TabListAPI apiInstance) {
    if (apiInstance == null) throw new IllegalArgumentException("API instance cannot be null");

    if (initialized) throw new IllegalStateException("TabList API is already initialized");

    instance = apiInstance;
    initialized = true;
  }

  /**
   * Shuts down the API and cleans up resources.
   *
   * <p><strong>Internal use only.</strong> This method is called by the
   * DreaminTabList plugin during shutdown and should not be called
   * by external code.
   *
   * <p>This method is public to allow access from the implementation package,
   * but should be considered internal API and may change without notice.
   *
   * @since 0.0.1
   */
  public static void shutdown() {
    instance = null;
    initialized = false;
  }

  /**
   * Resets the API factory state.
   *
   * <p><strong>Internal use only.</strong> This method is used for
   * testing purposes and should not be called by external code.
   *
   * @since 0.0.1
   */
  public static void reset() {
    shutdown();
  }
}

