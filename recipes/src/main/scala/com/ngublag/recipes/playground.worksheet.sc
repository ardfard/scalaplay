import zio.http.Path
import zio.http.codec.PathCodec
import PathCodec._

val pathCodec = empty / "recipes" / int("id")

pathCodec.decode(Path("recipes/123"))