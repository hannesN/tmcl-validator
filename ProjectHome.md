The Topic Maps Constraint Language (TMCL, ISO 19756) is a language defined to specify constraints and schemas for topic maps. The TMCL validator validates a given topic map against a schema compatible to the current draft (2009-11-19, http://www.isotopicmaps.org/tmcl/tmcl.html).

The TMCL Validator validates a TMAPI topic map against a schema and returns a list of invalid constructs together with a description what constraint was violated.
Note: The validator does only a loose validation, i.e. constructs for which no constraint exist will be considered valid.

For more information take a look at the [documentation](http://docs.topicmapslab.de/tmcl-validator/).