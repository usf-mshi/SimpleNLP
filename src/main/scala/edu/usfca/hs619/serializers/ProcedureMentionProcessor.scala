package edu.usfca.hs619.serializers

import org.apache.ctakes.typesystem.`type`.textsem.{ProcedureMention}
import java.util

import edu.usfca.hs619.util.ProcessUtils

/**
  *  This class gets a collection of Procedure Mentions and processes them.
  */
object ProcedureMentionProcessor {
  /**
    *
    * @param col This is a collection of Procedure Mentions
    * @param dbMetaData This is the string which is used to get the subject id, hadm id and discharg summary id.
    */
  def process(col: util.Collection[ProcedureMention], dbMetaData:String):Unit = {
    ProcessUtils.processCtakesMention(col, dbMetaData, "mimiciii.m_ct_procedure_concept_1")
  }
}
