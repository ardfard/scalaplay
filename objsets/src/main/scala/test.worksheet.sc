1 + 1

import objsets._

val v = new Tweet("a", "b", 1)

val tweetset = (new Empty).incl(v)
tweetset.mostRetweeted
