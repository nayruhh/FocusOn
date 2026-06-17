# Testing Guide

This document describes the unit and integration tests for the FocusOnPlus Android app.

## Test Structure

All tests are located in `app/src/test/java/com/example/focusonplus/` and run on the JVM without requiring an Android device or emulator.

### Core Unit Tests

#### `AnalyticsStorageTest.kt`
Tests for the `AnalyticsStorage` object:
- ✅ Saving session records
- ✅ Appending to existing sessions
- ✅ Retrieving session history
- ✅ Handling empty history
- ✅ Error handling for invalid JSON

#### `TimerFunctionsTest.kt`
Tests for time formatting utilities:
- ✅ Formatting seconds to `mm:ss` format
- ✅ Formatting seconds to `hh:mm:ss` format for hours
- ✅ Edge cases (0 seconds, 1 second, etc.)
- ✅ Large values handling

#### `SessionLogicTest.kt`
Tests for session calculation logic:
- ✅ Accuracy calculation (0 distractions = 100%, etc.)
- ✅ Total minutes calculation
- ✅ Day of week adjustment logic
- ✅ Edge cases (negative accuracy prevention)

#### `FaceDetectionLogicTest.kt`
Tests for face detection state management:
- ✅ Warm-up period logic
- ✅ Grace period incrementing
- ✅ Session pause/resume logic
- ✅ Distraction counting
- ✅ Face detection state tracking

### Integration Tests (Unit Test Style)

#### `AnalyticsStorageIntegrationTest.kt`
Integration-style tests for `AnalyticsStorage` using mocks:
- ✅ Save and retrieve sessions together
- ✅ Multiple sessions in order
- ✅ Empty history handling
- ✅ Edge cases (zero minutes, high distractions)

#### `SessionFlowIntegrationTest.kt`
Integration-style tests for complete session flow using mocks:
- ✅ Save session and navigate
- ✅ Accuracy calculation in real flow
- ✅ Zero minutes handling
- ✅ Partial minutes handling
- ✅ Integration with time formatting

#### `TimerCameraScreenLogicTest.kt`
Logic tests for timer screen state management:
- ✅ Timer increment logic
- ✅ Pause/resume functionality
- ✅ State reset on session start
- ✅ Grace period and warm-up logic

## Running Tests

All tests are unit tests and run on the JVM:

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests "com.example.focusonplus.AnalyticsStorageTest"
```

### Run Tests with Coverage
```bash
./gradlew testDebugUnitTest jacocoTestReport
```

## Test Coverage

The test suite covers:
- ✅ **AnalyticsStorage**: 100% coverage of save/retrieve operations
- ✅ **Time Formatting**: All edge cases and formats
- ✅ **Session Logic**: Accuracy, minutes, day calculations
- ✅ **Face Detection**: State management and pause/resume logic
- ✅ **Integration Flows**: End-to-end session saving and navigation

## Dependencies

The tests use:
- **JUnit 4**: Test framework
- **Mockito**: Mocking framework for unit tests
- **AndroidX Test**: For integration tests
- **Compose Test**: For UI testing
- **Coroutines Test**: For testing coroutine-based code

## Adding New Tests

When adding new features:
1. Add unit tests for business logic in `app/src/test/`
2. Add integration tests for UI flows in `app/src/androidTest/`
3. Ensure tests follow the naming convention: `*Test.kt` for unit tests, `*IntegrationTest.kt` for integration tests
4. Use descriptive test names: `testName_describesExpectedBehavior()`

## Best Practices

1. **Isolation**: Each test should be independent
2. **Setup/Teardown**: Use `@Before` and `@After` for cleanup
3. **Mocking**: Mock external dependencies (Context, NavController, etc.)
4. **Assertions**: Use descriptive assertion messages
5. **Coverage**: Aim for >80% code coverage

