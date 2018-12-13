package edu.usfca.hs619.serializers

import java.util

import edu.usfca.hs619.util.ProcessUtils
import org.apache.ctakes.typesystem.`type`.textsem.MedicationMention

object MedicationMentionProcessor {
  def process(col: util.Collection[MedicationMention], dbMetaData:String):Unit = {
    ProcessUtils.processCtakesMention(col, dbMetaData, "mimiciii.m_ct_medication_concept_1")
  }
}
