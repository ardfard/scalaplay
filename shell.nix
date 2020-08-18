{ sources ? import ./nix/sources.nix }:
let
nixpkgs = import sources.nixpkgs { };
in
with nixpkgs;
mkShell {
  name = "scala-env";
  buildInputs = [
    scala
    sbt
    libsodium
    postgresql
  ];
  JDBC_DATABASE_URL="postgres://127.0.0.1/playground";
  POSTGRES_USER="ardfard";
  POSTGRES_PASSWORD="";
  LC_ALL="en_US.UTF-8";
  LC_CTYPE="en_US.UTF-8";
}
