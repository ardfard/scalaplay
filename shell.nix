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
    ammonite
  ];
  JDBC_DATABASE_URL="postgres://127.0.0.1/playground";
  POSTGRES_USER="ardfard";
  POSTGRES_PASSWORD="";
}
