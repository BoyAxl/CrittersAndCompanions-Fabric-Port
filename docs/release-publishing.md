# Release Publishing

This repo publishes release files from GitHub Releases.

## One-time setup

### CurseForge

1. Open the CurseForge API token page:

```text
https://www.curseforge.com/account/api-tokens
```

2. Log in if CurseForge asks you to.
3. Create or copy a CurseForge API token.
4. Open this GitHub repository.
5. Go to Settings -> Secrets and variables -> Actions.
6. Click New repository secret.
7. Use this name:

```text
CURSEFORGE_TOKEN
```

8. Paste the CurseForge token as the value.
9. Save it.

Do not put the token in a commit, release body, Discord message, or issue.

### Modrinth

1. Create a Modrinth account:

```text
https://modrinth.com
```

2. Create a new Modrinth project for this port, or get added to an existing project's team.

Do not use the upstream Critters and Companions project ID unless this GitHub account has explicit publish permission on that Modrinth team.

3. Copy the Modrinth project ID or slug from the project page.
4. Generate a Modrinth personal access token from the account settings.
5. Give the token the `VERSION_CREATE` scope.
6. Open this GitHub repository.
7. Go to Settings -> Secrets and variables -> Actions.
8. Click New repository secret.
9. Use this name:

```text
MODRINTH_TOKEN
```

10. Paste the Modrinth token as the value.
11. Save it.

The Modrinth project ID is stored in `.github/workflows/release.yml`. This port currently uses:

```text
shNmXuqa
```

The Modrinth publish step is skipped until `MODRINTH_TOKEN` exists, so CurseForge publishing can keep working while Modrinth is not set up yet.

## Publishing a new version

1. Update `mod_version` in `gradle.properties`.

For example, if the previous CurseForge file was `crittersandcompanions-26.1.x-0.1.5-fabric.jar`, use:

```properties
mod_version=26.1.x-0.1.6-fabric
```

2. Commit and push the change.
3. Open GitHub -> Releases -> Draft a new release.
4. Create a new tag, for example:

```text
v26.1.x-0.1.6-fabric
```

5. Set the release title, for example:

```text
Critters and Companions 26.1.x - 0.1.6 Fabric (Unofficial Port)
```

6. Write the changelog in the release body.
7. Click Publish release.

GitHub Actions will build the mod, attach the jar to the GitHub Release, and upload the same jar to CurseForge. If Modrinth credentials are configured, it will also upload the same jar to Modrinth. The GitHub Release body becomes the platform changelog.

## Publishing an existing release to Modrinth

Use this when a GitHub Release already exists and only Modrinth needs to be backfilled.

1. Open GitHub -> Actions.
2. Click Publish Release.
3. Click Run workflow.
4. Set `tag` to the existing release tag, for example:

```text
v26.1.x-0.1.6-fabric
```

5. Set `publish_curseforge` to `false`.
6. Set `publish_modrinth` to `true`.
7. Fill `name` and `changelog`, or leave them empty if the tag is enough for the manual upload.
8. Click Run workflow.

## If it fails

1. Open GitHub -> Actions.
2. Click Publish Release.
3. Open the failed run.
4. Read the red step.
5. Fix the problem and use Re-run jobs.

Most CurseForge failures are caused by a missing `CURSEFORGE_TOKEN`, an expired token, a wrong CurseForge project ID, or a release version that already exists on CurseForge.

Most Modrinth failures are caused by a missing `MODRINTH_TOKEN`, a wrong Modrinth project ID, a token without `VERSION_CREATE`, no team access to the project, a project still waiting for approval, or a release version that already exists on Modrinth.
