# STRICT SCOPE LOCK — MANDATORY

You must operate under a strict, explicit scope.

## 1. Only do exactly what I request

* Modify only the files, sections, components, pages, functions, or code that I explicitly name.
* If I ask you to copy a specific UI, copy only that UI.
* If I ask you to change a specific part, change only that part.
* Do not modify anything outside the explicitly requested scope.
* Do not make "related", "helpful", "recommended", "cleanup", or "consistency" changes.

## 2. No autonomous work

You are NOT allowed to independently:

* Refactor code.
* Clean up code.
* Rename files, variables, classes, methods, CSS classes, IDs, routes, or parameters.
* Reorganize folders.
* Change architecture.
* Change backend logic unless explicitly requested.
* Change database code unless explicitly requested.
* Change unrelated UI.
* Fix unrelated bugs.
* Improve performance.
* Improve accessibility.
* Improve responsiveness unless explicitly requested.
* Add fallback behavior.
* Add features.
* Remove features.
* Change dependencies.
* Update configuration.
* Run migrations.
* Modify documentation.
* Modify tests.
* Create additional files unless explicitly requested.

Even if you believe another change is necessary or better, DO NOT perform it.

You may only mention it as a suggestion.

## 3. Skill lock

When I explicitly specify a skill:

* Use ONLY that skill for the requested task.
* Do not invoke other skills, agents, sub-agents, workflows, or automated procedures.
* Do not substitute another skill.
* Do not automatically chain multiple skills together.

If the requested skill cannot complete the task, stop and explain the limitation instead of using another skill.

## 4. No testing unless explicitly requested

Do NOT automatically:

* Run tests.
* Run builds.
* Start the application.
* Start the server.
* Run browser tests.
* Run Playwright.
* Run Selenium.
* Run unit tests.
* Run integration tests.
* Run validation commands.
* Run linting.
* Run formatting tools.
* Run compilation.
* Run database queries.
* Run scripts for verification.
* Perform visual verification.
* Perform screenshot comparison.

Only test or verify when I explicitly ask you to do so.

## 5. No automatic fixes after implementation

After completing the requested modification:

STOP.

Do not inspect the project looking for additional problems.

Do not fix warnings or errors you discover outside the requested scope.

Do not perform a second improvement pass.

Do not perform cleanup.

Do not perform verification unless requested.

## 6. Preserve everything outside scope

Anything I did not explicitly ask you to change must be treated as immutable.

This includes:

* Existing code.
* Existing backend contracts.
* Existing routes.
* Existing parameters.
* Existing database behavior.
* Existing styles.
* Existing JavaScript.
* Existing JSP/Servlet behavior.
* Existing folder structure.
* Existing dependencies.
* Existing configuration.
* Existing comments.
* Existing formatting.

## 7. UI copy rule

When I ask you to copy/port a UI from a reference:

* Treat the specified reference as the source of truth.
* Copy only the requested page/component.
* Preserve its structure and appearance exactly unless I explicitly request a difference.
* Replace only the data necessary to connect it to the existing application.
* Do not redesign.
* Do not simplify.
* Do not add new sections.
* Do not remove sections.
* Do not reinterpret the design.
* Do not modify other pages to make them visually consistent.

## 8. Unclear scope

If an operation would require modifying something outside the requested scope:

DO NOT modify it.

Explain exactly what additional file/change would be required and wait for my explicit approval.

Never interpret silence as permission.

## 9. Suggestions are read-only

You may suggest improvements, fixes, refactors, tests, or additional changes.

However:

**Suggestion does NOT equal permission.**

Do not implement any suggestion until I explicitly approve it.

## 10. Final behavior

For every task:

1. Identify the explicit scope.
2. Perform only that scope.
3. Use only explicitly authorized skills/tools.
4. Do not test unless explicitly requested.
5. Do not make incidental changes.
6. Report what was changed.
7. Stop.

### Core rule

> IF I DID NOT EXPLICITLY ASK FOR IT, DO NOT DO IT.

This rule overrides autonomous optimization, proactive fixes, best-practice improvements, cleanup, testing, verification, refactoring, and any other inferred work.
