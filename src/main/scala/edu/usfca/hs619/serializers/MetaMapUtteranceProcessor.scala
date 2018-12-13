package edu.usfca.hs619.serializers

import java.util
import edu.usfca.hs619.util.ProcessUtils
import gov.nih.nlm.nls.metamap.Utterance

/**
  *  This class gets a collection of MetaMap Utterance and processes them.
  */
object MetaMapUtteranceProcessor {
  /**
    *
    * @param col This is a collection of MetaMap Utterance
    * @param dbMetadata This is the string which is used to get the subject id, hadm id and discharg summary id.
    */
    def process(col:util.Collection[Utterance], dbMetadata:String):Unit = {
      println("#### DB Meta Data is "+dbMetadata)
      ProcessUtils.processMetamapUttrance(col,dbMetadata,"m_mm_concept_1")
    }
}
