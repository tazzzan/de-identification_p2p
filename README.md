In the modern time the demand for privacy of confidential data is gaining support due to
the amount of data that is gathered through mobile devices, communication and social
networks. For instance, the European Union demands for stronger privacy regulations as
responses to ongoing data mining and selling operations of big companies as Facebook that
recently announced to combine Whatsapp and Facebook platform user data. The growing
exchange of confidential data not only requires cryptographic technologies but also
intelligent privacy preserving algorithms that are able to make the data shareable
throughout networks without to be concerned about the privacy aspects.

The research aims and objectives are to provide an algorithm and its implementation in
order to be able to anonymize data sets with the consideration of the ability to balance the
privacy and utility metrics in a peer to peer network of autonomous researchers. 

Hence, the obejctive is make the anonymized data sets shareable and suitable for all involved shareholder groups
by preserving the privacy without the application of cryptographic technologies.

**Implemented modules:** 

ARX - De-Identification Library
AngularJS - Front-end
Spring MVC - REST Back-end
Spring Integration - P2P Back-End

**2-tier structure:**

Presentation-tier
{User Interactions, Core Operations}
{Controller}
{Request Handling}

Application-tier
{Request Handling}
{User Services, P2P Services}
{Data Set Services}

**P2P Communication flow**

_Task A: Establishment of the peer to peer network_

_Task B: Comparison of data sets in network_

_Task C: Provide metrics about the implication of exchanged parameters on local data sets_

As the consequence of the missing central server entity, peers primarily need to establish
connections to another peers. One user needs to provide an address of a friendly user who
is afterwards requested to submit the new channel in order to establish a connection to the
requesting client. Thereafter, the server updates its friendly peers list and forwards this list
to all submitted channels including the new created one to the requesting client. When
faced with requesting the submission to a client-server channel the routing scheme is
bidirectional unicast and unidirectional multicast oriented when it comes to responding on
the server side.

In addition, peers need to compare their data sets for similarities. To accomplish this
objective each peer’s client side forwards the properties of the affected data set into the
network and respectively peers on the server-side send a response containing similar data
sets in workspace. As a consequence, all clients utilize an bidirectional multicast oriented
routing scheme to provide information about their data sets.

**Equilibrium between data privacy and utility**

Having received the metrics for configured parameters from a peer in network the
proposed algorithm adds the parameters and corresponding metrics into one table that
consists of all analog data sets in network, the applied parameters by each peer and
generated values of metrics. 

As a consequence, each peer holds information about the impact of the application of
specific parameters on each data set in network. Thereafter, the proposed algorithm begins
the seeking process for the best equilibrium between privacy and utility metrics by
consideration of respective weights. 

The level of privacy determined by the threat to re-identify distinct records requires to be as maximal as possible resulting in the maximization
problem. In contrast, the information loss that determines the level of utility and needs to
be as minimal as possible resulting in the minimization problem.