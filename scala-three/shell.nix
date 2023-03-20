let
  nixpkgs = import <nixpkgs> { };
in
with nixpkgs;
mkShell {
  name = "scala-env";
  buildInputs = [buf];
}
