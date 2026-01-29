# SonarQube API Compatibility

## Current Status

**Plugin Version:** 1.8.0  
**Minimum SonarQube Version:** 7.9 LTS  
**Tested With:** SonarQube 7.9  
**Build Target:** SonarQube API 7.9

## SonarQube Version Clarification

There is **no SonarQube version 25**. SonarQube uses a different versioning scheme:

- **6.7 LTS** (May 2017)
- **7.9 LTS** (July 2019) ← **Current plugin target**
- **8.9 LTS** (February 2021)
- **9.9 LTS** (February 2023)
- **10.x** (Latest, 2024+)

If you are referring to "SonarQube 25," you might mean:
- SonarQube 10.x (latest LTS version)
- An internal build or API version number
- A misunderstanding of the version numbering

## Compatibility with Modern SonarQube Versions

### SonarQube 8.x Compatibility

The plugin should work with SonarQube 8.x with minimal changes:
- ✅ PropertyDefinition API is compatible (we use the modern API)
- ✅ Sensor API is stable
- ✅ Rules Definition API is compatible
- ⚠️ Some deprecations may generate warnings but should not break functionality

**Status:** Likely compatible, needs testing

### SonarQube 9.x Compatibility

Changes required for SonarQube 9.9 LTS:
- Update `sonar.version` to `9.9.0.65466`
- Update `sonar-orchestrator.version` to appropriate version
- Verify no breaking API changes
- Test all sensors and rules definitions

**Status:** Requires updates and testing

### SonarQube 10.x Compatibility

Major changes in SonarQube 10.x:
- **Java 17+ required** (currently using Java 8)
- Modernized APIs
- Potential breaking changes in sensor API
- Updated testing framework

**Status:** Requires significant updates

## API Features Currently Used

The plugin uses these SonarQube APIs:
- **Plugin API:** `org.sonar.api.Plugin`
- **PropertyDefinition:** Modern configuration API (already migrated from deprecated `@Property`)
- **Sensor API:** `org.sonar.api.batch.sensor.Sensor`
- **Rules Definition:** `org.sonar.api.server.rule.RulesDefinition`
- **Profile Importers:** Quality profile import API
- **Language API:** `org.sonar.api.resources.AbstractLanguage`
- **CPD Analyzer:** Copy-Paste Detection
- **Test Coverage:** Coverage sensor API

## How to Upgrade to Newer SonarQube Versions

To upgrade the plugin for compatibility with newer SonarQube versions:

1. **Update version numbers** in `pom.xml`:
   ```xml
   <sonarQubeMinVersion>X.X</sonarQubeMinVersion>
   <sonar.version>X.X.X.XXXXX</sonar.version>
   <sonar-orchestrator.version>X.X.X.XXXX</sonar-orchestrator.version>
   ```

2. **Check for deprecated APIs:**
   ```bash
   mvn compile 2>&1 | grep -i "deprecated"
   ```

3. **Update deprecated code:**
   - Review SonarQube release notes for breaking changes
   - Update any deprecated API calls
   - Update tests if testing API changed

4. **Test thoroughly:**
   ```bash
   mvn clean test
   mvn package
   ```

5. **Verify plugin functionality:**
   - Install in target SonarQube version
   - Run analysis on test projects
   - Verify rules, profiles, and sensors work correctly

## Recommendation

For production use, we recommend:
- **SonarQube 7.9 LTS** - Fully tested and supported ✅
- **SonarQube 8.9 LTS** - Should work, needs validation
- **SonarQube 9.9 LTS** - Requires plugin update
- **SonarQube 10.x** - Requires significant updates (Java 17+)

## Known Limitations

- Plugin is built with Java 8 target
- Uses older SSLR versions (1.23)
- StaxMate dependencies explicitly added for 7.9+ compatibility
- Some test dependencies may need updates for newer SonarQube versions

## Further Information

For SonarQube API documentation:
- [Plugin API Documentation (Latest)](https://docs.sonarqube.org/latest/extend/developing-plugin/) - Note: Shows latest version docs
- [Plugin API Documentation (7.9)](https://docs.sonarqube.org/7.9/extend/developing-plugin/) - Specific to current target version
- [SonarQube Release Notes](https://www.sonarqube.org/downloads/)
- [API Javadoc (7.9)](https://javadoc.io/doc/org.sonarsource.sonarqube/sonar-plugin-api/7.9)

## Questions or Issues?

If you need support for a specific SonarQube version, please:
1. Specify the exact SonarQube version number (e.g., 9.9, 10.3)
2. Describe any error messages or compatibility issues
3. Indicate if this is for production use or testing
