package org.easyj.rest.validation.sequences;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;
import org.easyj.rest.validation.groups.PUTChecks;

@GroupSequence({Default.class, PUTChecks.class})
public interface PUTSequence {}
