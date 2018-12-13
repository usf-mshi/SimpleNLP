package edu.usfca.hs619.serializers

import java.util
import edu.usfca.hs619.util.{ProcessUtils}
import org.apache.ctakes.typesystem.`type`.textsem.{DiseaseDisorderMention}

/**
  * This class gets a collection of Disease Mentions and processes them.
  */
object DiseaseMentionProcessor {
  /**
    * This method takes the DiseseDisorder mention and the metadata header and processes it.
    * @param col This is a collection of Disease Mentions
    * @param dbMetaData This is the string which is used to get the subject id, hadm id and discharg summary id.
    */
    def process(col: util.Collection[DiseaseDisorderMention], dbMetaData:String):Unit = {
      ProcessUtils.processCtakesMention(col, dbMetaData, "mimiciii.m_ct_disease_concept_1")
    }
}
