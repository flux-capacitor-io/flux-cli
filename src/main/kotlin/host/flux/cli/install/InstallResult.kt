package host.flux.cli.install

sealed class InstallResult {
    data class Upgraded(val fromVersion: String, val toVersion: String) : InstallResult()
    data class AlreadyLatest(val currentVersion: String) : InstallResult()
    data class FreshInstall(val version: String) : InstallResult()
}