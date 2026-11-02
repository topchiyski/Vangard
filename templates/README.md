# Templates: copy, rename, fill in the TODOs

Each folder holds a starter for one kind of test. It's the quickest way to begin a new test.

| Folder | What you get | Copy the files to |
|---|---|---|
| `web/` | Page Object, step definitions and a feature | `vanguard-web/src/main/.../pages`, `vanguard-web/src/test/.../steps`, `vanguard-web/src/test/resources/features` |
| `api/` | record DTO, API client, step definitions and a feature | `vanguard-api/src/main/.../dto`, `.../client`, `vanguard-api/src/test/...` |
| `mobile/` | Screen Object for Android + iOS, step definitions and a feature | `vanguard-mobile/src/main/.../screens`, `vanguard-mobile/src/test/...` |
| `load/` | Gatling simulation (Java) and a feature | `vanguard-load/src/main/.../simulation`, `vanguard-load/src/test/resources/features` |
| `e2e/` | journey steps (API + web) and a feature | `vanguard-e2e/src/test/...` |

After copying:
1. Change the `package` line to match the folder you copied the file into.
2. Search the files for `TODO` and replace each one.
3. Run the test on its own. The command is in the header comment of each feature file.

The templates are compiled on every build, so they always match the current framework. They never run as tests.
The step-by-step recipes are in [docs/JUNIOR_ONBOARDING_GUIDE.md](../docs/JUNIOR_ONBOARDING_GUIDE.md).
