# topicxp_extension

This project is an extension of the TopicXP Eclipse plugin, which is a tool for topic modeling through LDA over Java source code.

This tool was previously developed by the SEMERU group at the College of William and Mary.
http://www.cs.wm.edu/semeru/TopicXP/

Features we have implemented and added to the original version:
   * Removing the license comments at the beginning of each file as part of the preprocessing step. This eliminates noise from the license text content.
   * LCbC (Lack of Concern-based Cohesion) metric computation.
   * CSV exportation of the LCbC values.