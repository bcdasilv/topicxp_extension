package edu.wm.LDATopics.LDA;

import org.eclipse.core.runtime.jobs.ISchedulingRule;


public class LDATopicUpdateRule implements ISchedulingRule  {

	public boolean contains(ISchedulingRule rule) {
		// TODO Auto-generated method stub
		return rule instanceof LDATopicUpdateRule;
	}

	public boolean isConflicting(ISchedulingRule rule) {
		return rule instanceof LDATopicUpdateRule;
	}

}
