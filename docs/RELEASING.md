# Sharing and release preparation

This directory is a source-sharing snapshot, not a signed production release. It excludes local TV pairing responses, signing keys, runtime recordings, raw diagnostics and APKs. The project is open source under GNU GPL v3.0 only (`GPL-3.0-only`); include LICENSE when sharing it.

Before the first upload:

1. Choose the repository name. Preserve LICENSE and the GPL-3.0-only license declaration in the README.
2. Run `bash android/tests/run.sh` and build from a clean checkout.
3. Review the exact staged files with `git diff --cached --name-only` and `git diff --cached`.
4. Confirm no device credentials, local recordings, signing files or personal paths are staged. `.gitignore` does not remove files already tracked in a repository.
5. If attaching an APK, identify the tested hardware, version and development/debuggable status, and provide its corresponding source under GPLv3. Keep the signing key local and reuse it for updates.

The package ID remains `local.ambilight.reader` for compatibility with the existing development installation. Renaming the display name to Freelight did not migrate the package ID. Changing it would create a separate application with separate data.

Known work before broader distribution: integrated TV pairing, release signing/provisioning design, more firmware/model testing, real multi-bulb testing, and Bridge streaming if that backend is pursued.

The room and bulb raster assets were generated for this project using an image-generation tool. The editable SVG logo and native Android drawable resources are bundled. No external image download is needed during app operation. Trademark ownership remains with the respective brands; the app is independent.
