package edu.usfca.hs619.serializers

import java.util

import edu.usfca.hs619.util.ProcessUtils
import org.apache.ctakes.typesystem.`type`.textsem.{SignSymptomMention}

/**
  * This class gets a collection of Procedure Mentions and processes them.
  */
object SignSymptomMentionProcessor {
  /**
    *
    * @param col This is a collection of signs and symptoms Mentions
    * @param dbMetaData
    */
  def process(col: util.Collection[SignSymptomMention], dbMetaData:String):Unit = {
    ProcessUtils.processCtakesMention(col, dbMetaData, "mimiciii.m_ct_symptom_concept_1")
  }
}
