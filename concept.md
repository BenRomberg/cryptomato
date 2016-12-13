The goal is to create an easy-to-use messaging platform on the web that can transmit messages and
files with end-to-end encryption:
* A and B want to communicate. They have a means of direct authentic communication (e.g.
on the phone recognizing each other's voices).
* Every communication between A, B and our service (S) has to be considered open and vulnerable,
including the phone conversation. We assume that the phone conversation cannot be altered by a MITM
though.
* S has to be considered vulnerable.
* The only reliabilities should be towards the web-client provided by S. The source is open and can
be read and verified by anyone. The web-client consists of a single HTML file (with CSS/JavaScript
embedded). A checksum of that file is published on other services (e.g. Twitter) so users can verify
the integrity of their clients.
* Any other reliabilities should be kept to a minimum. If other services are used, those should be
easily exchangeable and have a very low chance of getting compromised. Currently those are:
  * A service to publish the checksum of the web-client: Twitter
  * A Time Stamping Authority (TSA): freetsa.org
* We need to assume that the computers A and B are using are free of any malware as well. Otherwise
it would be easy to listen to the messages (or take screenshots every few seconds) and access any
files that are sent or received.
* Communication should be as anonymous as possible. No accounts need to be created. On the other
hand, if the browser is closed there is no way to resume communication or retrieve the messages
that were sent.
* If any intercepted message is compromised, the other messages are not affected (perfect forward
secrecy).

The ultimate goal of the service is to establish secure communication between A and B with perfect
forward secrecy, using Diffie Hellman. For a Diffie Hellman key-exchange to be established, A and B
need to know each other's public keys (PK) so that no MITM can interfere in the key-exchange.

On initialization of the webclient, A and B create a private-public-keypair for themselves. The goal
is now to safely and easily communicate B's PK to A, so that she can safely initiate a secure
channel of communication with B.

Exchanging public keys over telephone is not easy and very error-prone. What could work is that
they exchange identifiers that are 6-8 characters long (Base36 so they don't need to worry about
character case). 6^36 is about 2 billion possibilities, combined with the timestamp of the public
key it should be reasonably safe to assume that it cannot be duplicated.

What we need is an authority to verify the timestamp our PK gets created at. A TSA can provide the
certification that our PK was created at a specific time by signing a SHA-512 hash of the PK,
turning it into a "TSA token". If we then communicate not only the first few characters of the
TSA token, but also the time it was created over the phone, we can be reasonably certain that
it cannot be duplicated.

Unfortunately due to browser restrictions (same origin policy), we cannot call the TSA from the
browser directly, but have to go through S to generate the signature. We can then verify that our
TSA has really created the signature by storing the corresponding public key of the TSA (TSAPK) on
the client, so the client can be sure it was really the TSA who signed our PK. A potential
duplication of S could occur if it would pregenerate all possible PKs in advance, but due
to the number of combinations this is impossible (and we can show a warning to the user on the
client if the timestamp of the TSA token varies greatly from the local time of the browser).

The TSA token, together with the PK and the timestamp get stored on S where they can be
requested by anyone who wants to initiate communication with another party. The identifier used
for identification is the TSA token, since it can guarantee the relationship between the PK and
the timestamp. As explained before, for ease of use a Base36 version of the TSA token is
used for users to lookup other users. They can then see the corresponding timestamp and verify it
with the other party. After fetching the TSA token, the timestamp and the corresponding PK
from S, the webclient can verify their integrity by verifying the TSA token signature with the
TSAPK, the PK and the timestamp provided by S.

So hopefully, if A wants to communicate to B, all she has to do is to enter 6 characters given by
B and verify the timestamp with him.

Afterwards, the actual communication will happen through websockets for messages and potentially
WebRTC for files. Each message requires another Diffie Hellman key exchange.

Open questions:
* Does the concept make sense?
* Are there any open vulnerabilities with this concept?
* Are 6 characters (~2 billion possibilities) considered secure enough? 8 would be acceptable as
well I guess, but the shorter the better. We also have to assume that not every user will verify
the timestamp down to the second.
* Can any reliabilities be eliminated?
* Is the setup as simple as possible?
* Do similar concepts already exist and what are key differences?
