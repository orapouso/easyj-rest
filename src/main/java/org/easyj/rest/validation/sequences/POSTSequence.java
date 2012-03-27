package org.easyj.rest.validation.sequences;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;
import org.easyj.rest.validation.groups.POSTChecks;

@GroupSequence({Default.class, POSTChecks.class})
public interface POSTSequence {}
