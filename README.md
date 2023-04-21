# Distributed Movie Ticket Booking System (DMTBS)

DMTBS is a highly available web service that tolerates a single software failure or a single process crash failure. The server implementation has the essential code to withstand a software failure and will always remain highly available.

## Architecture

- There are three replicas of the actively replicated DMTBS server system, each of which uses a distinct implementation on different hosts.
- Every replica has a Replica Manager (RM), which identifies the problem and fixes errors accordingly.
- Additionally, a front end (FE) has been implemented to accommodate clients and sends their requests to the sequencer.
- In case of any software failure or fault that might occur, the three replicas take actions to perform the necessary process of the client's request and revert the results to the front end, which always returns the correct and accurate result to the client.
- The front end also notifies the RMs in case of an inaccurate result from the replicas.
- The RMs are responsible for replacing a replica with an accurate one, which works if there are three failed client requests consecutively from the same replica.
- If the front end does not receive a result within twice the time required by the slowest replica, it reverts to the RMs assuming that the error may have arisen due to crashing of a replica.
- All the server-server communication and the communication between the server replicas, the sequencer, the FE, and the RM communicate with one another via the UDP protocol only.
