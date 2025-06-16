Contributing to DreaminTabList API

Thank you for your interest in contributing to DreaminTabList API! This document provides guidelines and information for contributors.

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Contributing Guidelines](#contributing-guidelines)
5. [Code Standards](#code-standards)
6. [Testing](#testing)
7. [Documentation](#documentation)
8. [Pull Request Process](#pull-request-process)
9. [Issue Reporting](#issue-reporting)
10. [Community](#community)

## Code of Conduct

### Our Pledge

We are committed to making participation in our project a harassment-free experience for everyone, regardless of age, body size, disability, ethnicity, gender identity and expression, level of experience, nationality, personal appearance, race, religion, or sexual identity and orientation.

### Our Standards

Examples of behavior that contributes to creating a positive environment include:

- Using welcoming and inclusive language
- Being respectful of differing viewpoints and experiences
- Gracefully accepting constructive criticism
- Focusing on what is best for the community
- Showing empathy towards other community members

### Enforcement

Instances of abusive, harassing, or otherwise unacceptable behavior may be reported by contacting the project team. All complaints will be reviewed and investigated promptly and fairly.

## Getting Started

### Prerequisites

- Java 21 or higher
- Gradle 7.0 or higher
- Git
- A Minecraft server (Bukkit/Spigot/Paper) for testing
- Basic knowledge of Minecraft plugin development

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/DreaminTabList.git
   cd DreaminTabList
   ```
3. Add the upstream repository:
   ```bash
   git remote add upstream https://github.com/dreamin/DreaminTabList.git
   ```

## Development Setup

### Environment Setup

1. **Import the project** into your IDE (IntelliJ IDEA recommended)
2. **Install dependencies**:
   ```bash
   ./gradlew build
   ```
3. **Set up test server**:
  - Create a `test-server` directory
  - Download Paper/Spigot JAR
  - Configure basic server setup

### Project Structure

```
src/
├── main/java/fr/dreamin/dreaminTabList/
│   ├── api/                    # Public API interfaces
│   │   ├── profile/           # Profile management
│   │   ├── player/            # Player management
│   │   ├── events/            # API events
│   │   └── exceptions/        # API exceptions
│   ├── impl/                  # API implementations
│   │   ├── profile/           # Profile implementations
│   │   ├── player/            # Player implementations
│   │   └── cache/             # Caching implementations
│   ├── config/                # Configuration management
│   ├── event/                 # Legacy event handling
│   └── player/                # Legacy player management
├── test/java/                 # Unit tests
└── main/resources/            # Plugin resources
```

### Building the Project

```bash
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Generate Javadoc
./gradlew javadoc

# Create distribution JARs
./gradlew javadocJar sourcesJar
```

## Contributing Guidelines

### Types of Contributions

We welcome several types of contributions:

- **Bug fixes** - Fix issues in existing functionality
- **Feature enhancements** - Improve existing features
- **New features** - Add new functionality to the API
- **Documentation** - Improve or add documentation
- **Tests** - Add or improve test coverage
- **Performance improvements** - Optimize existing code

### Before You Start

1. **Check existing issues** - Look for related issues or feature requests
2. **Create an issue** - If none exists, create one to discuss your proposal
3. **Get feedback** - Wait for maintainer feedback before starting work
4. **Assign yourself** - Comment on the issue to indicate you're working on it

### Development Workflow

1. **Create a branch** from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following our code standards

3. **Test your changes** thoroughly:
   ```bash
   ./gradlew test
   ```

4. **Update documentation** if needed

5. **Commit your changes** with clear messages:
   ```bash
   git commit -m "feat: add new TabProfile sorting functionality"
   ```

6. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request** on GitHub

## Code Standards

### Java Code Style

- **Indentation**: 2 spaces (no tabs)
- **Line length**: Maximum 120 characters
- **Naming conventions**:
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

### Code Quality Requirements

- **Null safety**: Use `@NotNull` and `@Nullable` annotations
- **Error handling**: Proper exception handling with meaningful messages
- **Thread safety**: Consider concurrent access in shared components
- **Performance**: Avoid unnecessary object creation in hot paths
- **Memory management**: Proper cleanup of resources

### Example Code Style

```java
/**
 * Creates a new TabProfile with the specified parameters.
 * 
 * @param name the profile name, must not be null or empty
 * @param uuid the unique identifier, must not be null
 * @return the created profile, never null
 * @throws IllegalArgumentException if parameters are invalid
 */
@NotNull
public TabProfile createProfile(@NotNull String name, @NotNull UUID uuid) {
    if (name == null || name.trim().isEmpty()) {
        throw new IllegalArgumentException("Profile name cannot be null or empty");
    }
    
    if (uuid == null) {
        throw new IllegalArgumentException("UUID cannot be null");
    }
    
    return new TabProfileImpl(name, uuid);
}
```

### API Design Principles

- **Consistency**: Follow established patterns in the codebase
- **Immutability**: Prefer immutable objects where possible
- **Builder pattern**: Use builders for complex object creation
- **Fluent interfaces**: Support method chaining where appropriate
- **Backward compatibility**: Don't break existing API contracts

## Testing

### Test Requirements

- **Unit tests** for all new functionality
- **Integration tests** for API interactions
- **Performance tests** for critical paths
- **Minimum 80% code coverage** for new code

### Writing Tests

```java
@Test
public void testCreateProfile() {
    // Given
    String name = "TestPlayer";
    UUID uuid = UUID.randomUUID();
    
    // When
    TabProfile profile = profileManager.createProfile()
            .name(name)
            .uuid(uuid)
            .build();
    
    // Then
    assertNotNull(profile);
    assertEquals(name, profile.getName());
    assertEquals(uuid, profile.getUniqueId());
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests TabProfileTest

# Run tests with coverage
./gradlew test jacocoTestReport
```

## Documentation

### Javadoc Requirements

All public APIs must have comprehensive Javadoc:

```java
/**
 * Brief description of the method.
 * 
 * <p>Longer description with more details about the method's behavior,
 * including any important notes about thread safety, performance, or
 * side effects.
 * 
 * @param parameter description of the parameter
 * @return description of the return value
 * @throws ExceptionType when this exception is thrown
 * @since 1.0.0
 * @see RelatedClass#relatedMethod()
 */
```

### Documentation Updates

When making changes, update:

- **Javadoc** for modified methods/classes
- **README.md** if public API changes
- **API_USAGE_GUIDE.md** for new features
- **CHANGELOG.md** with your changes

## Pull Request Process

### Before Submitting

- [ ] Code follows style guidelines
- [ ] Tests pass locally
- [ ] Documentation is updated
- [ ] Commit messages are clear
- [ ] Branch is up to date with main

### PR Description Template

```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests pass
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes (or properly documented)
```

### Review Process

1. **Automated checks** must pass (CI/CD)
2. **Code review** by at least one maintainer
3. **Testing** on test server if applicable
4. **Documentation review** for public API changes
5. **Final approval** by project maintainer

## Issue Reporting

### Bug Reports

Use the bug report template and include:

- **Environment details** (Java version, server version, plugin version)
- **Steps to reproduce** the issue
- **Expected behavior** vs actual behavior
- **Error logs** or stack traces
- **Minimal reproduction case** if possible

### Feature Requests

Use the feature request template and include:

- **Problem description** - what problem does this solve?
- **Proposed solution** - how should it work?
- **Alternatives considered** - what other approaches were considered?
- **Additional context** - any other relevant information

### Security Issues

For security vulnerabilities:

1. **Do NOT** create a public issue
2. **Email** the maintainers directly
3. **Include** full details of the vulnerability
4. **Wait** for acknowledgment before public disclosure

## Community

### Communication Channels

- **GitHub Issues** - Bug reports and feature requests
- **GitHub Discussions** - General questions and community discussion
- **Discord** - Real-time chat and support (if available)

### Getting Help

- **Documentation** - Check README and API guide first
- **Search issues** - Your question might already be answered
- **Ask questions** - Use GitHub Discussions for general questions
- **Be patient** - Maintainers are volunteers

### Recognition

Contributors will be:

- **Listed** in the CONTRIBUTORS.md file
- **Mentioned** in release notes for significant contributions
- **Credited** in commit messages and PR descriptions

## Development Tips

### IDE Setup

**IntelliJ IDEA** (recommended):
- Install Minecraft Development plugin
- Configure code style settings
- Set up live templates for common patterns

**Eclipse**:
- Install Buildship Gradle plugin
- Configure formatter settings
- Set up code templates

### Debugging

- Use the test server for debugging
- Enable debug logging in plugin configuration
- Use breakpoints in IDE for step-through debugging
- Test with multiple players using multiple clients

### Performance Considerations

- **Avoid blocking operations** on the main thread
- **Cache frequently accessed data** appropriately
- **Use efficient data structures** for collections
- **Profile performance** for critical paths
- **Consider memory usage** in long-running operations

## Release Process

### Version Numbering

We follow [Semantic Versioning](https://semver.org/):

- **MAJOR** version for incompatible API changes
- **MINOR** version for backward-compatible functionality additions
- **PATCH** version for backward-compatible bug fixes

### Release Checklist

- [ ] All tests pass
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Version numbers updated
- [ ] Release notes prepared
- [ ] Artifacts built and tested

---

Thank you for contributing to DreaminTabList API! Your contributions help make Minecraft server development better for everyone.

For questions about contributing, please open a GitHub Discussion or contact the maintainers.
