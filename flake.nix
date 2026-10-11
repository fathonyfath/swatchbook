{
  description = "The toolchain CI builds Swatchbook's release with";

  # Pinned to a commit rather than a branch: nobody runs Nix locally to maintain a flake.lock,
  # so this line is what keeps CI builds on the same tools. Bump it to update them.
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/6510408d147d8c5e6d76043e899e9251756f6a75";

  outputs =
    { nixpkgs, ... }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs {
        inherit system;
        config = {
          # The Android SDK is unfree and has a license of its own to accept.
          allowUnfree = true;
          android_sdk.accept_license = true;
        };
      };
      jdk = pkgs.jdk25;
      android = pkgs.androidenv.composeAndroidPackages {
        # compileSdk in android/build.gradle.kts and shared/build.gradle.kts.
        platformVersions = [ "37.0" ];
        # What Android Gradle Plugin 9.4 builds with by default; release.sh takes zipalign and
        # apksigner from it too.
        buildToolsVersions = [ "36.0.0" ];
        includeCmake = false;
      };
      sdkRoot = "${android.androidsdk}/libexec/android-sdk";
    in
    {
      # CI runs the release inside this: nix develop --command ./release.sh <keystore>
      devShells.${system}.default = pkgs.mkShell {
        packages = [
          jdk
          android.androidsdk
          # release.sh takes the version from git, and repacks the APK with the other two.
          pkgs.git
          pkgs.p7zip
          pkgs.unzip
        ];
        JAVA_HOME = jdk.home;
        ANDROID_HOME = sdkRoot;
        # GitHub's runners set this to an SDK of their own, and Gradle refuses to choose
        # between two.
        ANDROID_SDK_ROOT = sdkRoot;
      };
    };
}
