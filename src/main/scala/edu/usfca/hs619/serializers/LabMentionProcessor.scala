package edu.usfca.hs619.serializers

import java.util

import edu.usfca.hs619.util.ProcessUtils
import org.apache.ctakes.typesystem.`type`.textsem.{LabMention}

/**
  * his class gets a collection of Lab Mentions and processes them.
  */
object LabMentionProcessor {
  /**
    * This method takes the Lab mention and the metadata header and processes it.
    * @param col This is a collection of Lab Mentions
    * @param dbMetaData This is the string which is used to get the subject id, hadm id and discharg summary id.
    */
  def process(col: util.Collection[LabMention], dbMetaData:String):Unit = {
    ProcessUtils.processCtakesMention(col, dbMetaData, "mimiciii.m_ct_lab_concept_1")
  }
}
